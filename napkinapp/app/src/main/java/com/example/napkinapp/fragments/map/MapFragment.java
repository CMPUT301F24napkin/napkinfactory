/**
 * Fragment of the profile screen. Allows all users to edit their profile.
 *
 * US 02.02.02 As an organizer I want to see on a map where entrants joined my event waiting list from
 * US 01.08.01 As an entrant, I want to be warned before joining a waiting list that requires geolocation.
 */

package com.example.napkinapp.fragments.map;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.fragments.facility.ViewFacilityFragment;
import com.example.napkinapp.fragments.viewevents.OrganizerViewEventFragment;
import com.example.napkinapp.models.Facility;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractMapFragment;
import com.example.napkinapp.utils.DB_Client;
import com.google.android.gms.common.util.MapUtils;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.List;

import org.osmdroid.views.overlay.Marker;

public class MapFragment extends AbstractMapFragment {
    private final User user;
    private TitleUpdateListener titleUpdateListener;
    private Context m_context;

    private MapView map = null;

    public MapFragment(){
        this.user = new User();
    }
    public MapFragment(User user){
        this.user = user;
    }

    CustomInfoWindow customInfoWindow = null;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof TitleUpdateListener){
            titleUpdateListener = (TitleUpdateListener) context;
        }else{
            throw new RuntimeException(context + " needs to implement TitleUpdateListener");
        }

        m_context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Update title
        titleUpdateListener.updateTitle("Map");

        //handle permissions first, before map is created. not depicted here

        //load/initialize the osmdroid configuration, this can be done
        Configuration.getInstance().load(m_context, PreferenceManager.getDefaultSharedPreferences(m_context));
        //setting this before the layout is inflated is a good idea
        //it 'should' ensure that the map has a writable location for the map cache, even without permissions
        //if no tiles are displayed, you can try overriding the cache path using Configuration.getInstance().setCachePath
        //see also StorageUtils
        //note, the load method also sets the HTTP User Agent to your application's package name, abusing osm's
        //tile servers will get you banned based on this string

        //inflate and create the map
        View view =  inflater.inflate(R.layout.map, container, false);

        map = view.findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);

        customInfoWindow = new CustomInfoWindow(map, v -> {


            Facility facility = (Facility) v.getTag();
            if(facility == null) {
                facility = new Facility();
            }
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_fragmentcontainer, new ViewFacilityFragment(facility, user)) // Use your actual container ID
                    .addToBackStack(null) // Allows user to go back to ListEventsFragment
                    .commit();

        });

        // if permissions not granted
        requestMapPermissions();

        // add my location overlay DOESN"T WORK
//        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(m_context), map);
//        mLocationOverlay.enableMyLocation();
//        map.getOverlays().add(mLocationOverlay);

        // add default controls and gestures support
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(15.0);
        GeoPoint startPoint = new GeoPoint(53.527309714453466, -113.52931950296305);
        mapController.setCenter(startPoint);

        addFacilityMarkers();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
        map.onResume(); //needed for compass, my location overlays, v6.0.0 and up
    }

    @Override
    public void onPause() {
        super.onPause();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        map.onPause();  //needed for compass, my location overlays, v6.0.0 and up
    }

    void addFacilityMarkers() {
        DB_Client db = new DB_Client();
        db.findAll("Facilities", null, new DB_Client.DatabaseCallback<List<Facility>>() {
            @Override
            public void onSuccess(@Nullable List<Facility> data) {
                if(data != null) {
                    for(Facility facility : data) {
                        addMarker(map, getDefaultIcon(), facility.getName(), facility.getDescription(), facility.getLocation().get(0), facility.getLocation().get(1), customInfoWindow, facility);
                    }
                }
            }
        }, Facility.class);
    }
}