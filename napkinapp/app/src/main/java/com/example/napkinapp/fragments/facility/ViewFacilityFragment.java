package com.example.napkinapp.fragments.facility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.fragments.createevent.CreateEventFragment;
import com.example.napkinapp.fragments.listevents.EventArrayAdapter;
import com.example.napkinapp.fragments.viewevents.OrganizerViewEventFragment;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Facility;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractMapFragment;
import com.example.napkinapp.utils.DB_Client;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewFacilityFragment extends AbstractMapFragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;
    private Facility facility;
    private User user;

    public ViewFacilityFragment(Facility facility, User user){
        this.facility = facility;
        this.user = user;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof TitleUpdateListener){
            titleUpdateListener = (TitleUpdateListener) context;
        }else{
            throw new RuntimeException(context + " needs to implement TitleUpdateListener");
        }
        mContext = context;
    }

    /**
     * Custom customizer for the button in the event card. Makes it open the event view screen.
     */
    EventArrayAdapter.EventListCustomizer customizer = (button, event) -> {
        button.setText("View");
        button.setOnClickListener(v->{
            Log.i("Button", String.format("My Events: Clicked on event %s\n", event.getName()));


            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_fragmentcontainer, new ViewEventFragment(event, user))
                    .addToBackStack(null)
                    .commit();
        });
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Update title
        titleUpdateListener.updateTitle("Facility Details");

        View view = inflater.inflate(R.layout.facility_details, container, false);
        TextView nameTextView = view.findViewById(R.id.facility_name);
        TextView descriptionTextView = view.findViewById(R.id.facility_description);
        MapView map = view.findViewById(R.id.map);
        Button deleteFacilityButton = view.findViewById(R.id.button);

        nameTextView.setText(facility.getName());
        descriptionTextView.setText(facility.getDescription());

        // set button callback to delete this facility
        deleteFacilityButton.setOnClickListener(v -> {
            DB_Client db = new DB_Client();

            HashMap<String, Object> filter = new HashMap<>();
            filter.put("id", facility.getId());
            db.deleteAll("Facilites", filter, DB_Client.IGNORE);

            // Update the event's waitlist and chosen list
            Map<String, Object> userUpdates = Map.of(
                    "facility", ""
            );

            db.updateAll("Users", Map.of("id", user.getAndroidId()), userUpdates, DB_Client.IGNORE);

            getParentFragmentManager().popBackStack();
        });
        // do map
        Configuration.getInstance().setUserAgentValue("NapkinApp/1.0 (Android; OS Version; Device Model)");
        map.setTileSource(TileSourceFactory.MAPNIK);
        requestMapPermissions();
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);

        List<Double> location = facility.getLocation();

        GeoPoint startPoint = new GeoPoint(location.get(0), location.get(1));
        mapController.setCenter(startPoint);
        map.setOnTouchListener((v, event) -> {
            // Request parent to not intercept touch events when MapView is touched
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;  // Let the MapView handle the touch event
        });

        // populate map
        addMarker(map, getDefaultIcon(), location.get(0), location.get(1));

        return view;
    }
}
