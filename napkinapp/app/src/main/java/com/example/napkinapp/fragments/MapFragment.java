/**
 * Fragment of the profile screen. Allows all users to edit their profile.
 */

package com.example.napkinapp.fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.User;

import org.osmdroid.api.IMapController;
import org.osmdroid.api.IMapView;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;

public class MapFragment extends Fragment {
    private final User user;
    private TitleUpdateListener titleUpdateListener;
    private Context m_context;

    private final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView map = null;
    private ItemizedOverlayWithFocus<OverlayItem> markersOverlay;

    public MapFragment(){
        this.user = new User();
    }
    public MapFragment(User user){
        this.user = user;
    }

    final String[] permissionsToRequest = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
                if (isGranted.values().stream().allMatch(granted -> granted == Boolean.TRUE)) {
                    // Permission is granted. Continue the action or workflow in your
                    // app.
                    Log.i("Map", "all permissions granted");
                } else {
                    // Explain to the user that the feature is unavailable because the
                    // feature requires a permission that the user has denied. At the
                    // same time, respect the user's decision. Don't link to system
                    // settings in an effort to convince the user to change their
                    // decision.
                    Log.i("Map", "some permissions not granted");
                }
            });

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

        // if permissions not granted
        requestPermissionLauncher.launch(permissionsToRequest);

        // add my location overlay
//        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(m_context), map);
//        mLocationOverlay.enableMyLocation();
//        map.getOverlays().add(mLocationOverlay);

        // add default controls and gestures support
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(18);
        GeoPoint startPoint = new GeoPoint(53.527309714453466, -113.52931950296305);
        mapController.setCenter(startPoint);

        ArrayList<OverlayItem> items = new ArrayList<>();
        markersOverlay = new ItemizedOverlayWithFocus<>(items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        //do something
                        return true;
                    }
                    @Override
                    public boolean onItemLongPress(final int index, final OverlayItem item) {
                        return false;
                    }
                }, m_context);
        markersOverlay.setFocusItemsOnTap(true);
        map.getOverlays().add(markersOverlay);
        //your items
        addMarker("brian's best friend's house", "they might go bouldering together sometime idk", 53.401052538742526, -113.58270907542327); // Lat/Lon decimal degrees
        addMarker("ui test factory", "throrough handwritten tests written here", 53.490991702911465, -113.51791513242499);
        addMarker("intern", "he's doing his best", 53.52087332627619, -113.5328317301677);
        addMarker("Your Location", ";-)", 53.527309714453466, -113.52931950296305);
        addMarker("MVP's house", "literally wrote the most lines of code <3 omg hes so hot!!!", 53.508247805120284, -113.47414073221799);
        addMarker("Certain Eastern-European Individual", "writes code occasionally i guess", 53.470003731447584, -113.39308303414089);
        addMarker("Illegal Immigrant", "wrote, like, ALL the backend! Way to go!", 53.5282620031484, -113.57275089319474);
        addMarker("Queen of Hackathon", "Wanted to be pretty princess but that is cringe", 53.406111, -113.458472);

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

    /**
     * Adds a marker to the map.
     * @param title the title of the marker
     * @param description the description of the marker
     * @param longitude the longitude of the marker
     * @param latitude the latitude of the marker
     * @return the newly-created overlay item
     */
    public OverlayItem addMarker(String title, String description, double longitude, double latitude) {
        OverlayItem item = new OverlayItem(title, description, new GeoPoint(longitude, latitude));
        markersOverlay.addItem(item);
        return item;
    }
}