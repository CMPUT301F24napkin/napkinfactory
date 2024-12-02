package com.example.napkinapp.fragments.facility;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.fragments.listevents.EventArrayAdapter;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Facility;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewFacilityEventsListFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;
    private Facility facility;
    private User user;
    private ArrayList<Event> events;
    private EventArrayAdapter eventArrayAdapter;

    public ViewFacilityEventsListFragment(Facility facility, User user){
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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        titleUpdateListener.updateTitle("Facility Events");

        View view = inflater.inflate(R.layout.event_list, container, false);

        ListView facilityEventsListView = view.findViewById(R.id.events_list_view);

        EventArrayAdapter.EventListCustomizer customizer = (button, event) -> {
            button.setText("View Details");
            button.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.neutralGray)));
            button.setOnClickListener(v -> {
                Log.i("Button", String.format("List Events: Clicked on event %s\n", event.getName()));
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.content_fragmentcontainer, new ViewEventFragment(event, user)) // Use your actual container ID
                        .addToBackStack(null) // Allows user to go back to ListEventsFragment
                        .commit();
            });
        };

        events = new ArrayList<>();

        eventArrayAdapter = new EventArrayAdapter(mContext, events, customizer);
        facilityEventsListView.setAdapter(eventArrayAdapter);

        displayFacilityEvents();

        return view;
    }

    private void displayFacilityEvents() {
        DB_Client db = new DB_Client();

        db.findOne("Users", Map.of("facility", facility.getId()), new DB_Client.DatabaseCallback<User>() {
            @Override
            public void onSuccess(@Nullable User organizer) {
                db.findAll("Events", Map.of("organizerId", organizer.getAndroidId()), new DB_Client.DatabaseCallback<List<Event>>() {
                    @Override
                    public void onSuccess(@Nullable List<Event> data) {
                        events.clear();
                        if(data != null){
                            events.addAll(data);
                        }
                        eventArrayAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Exception e) {
                        DB_Client.DatabaseCallback.super.onFailure(e);
                    }
                }, Event.class);
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
            }
        }, User.class);

    }


}
