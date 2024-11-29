package com.example.napkinapp.fragments.facility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.napkinapp.utils.DB_Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewFacilityFragment extends Fragment {
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
        ListView eventsList = view.findViewById(R.id.events_list_view);
        ArrayList<Event> events = new ArrayList<>();

        // Attach EventArrayAdapter to ListView
        EventArrayAdapter eventArrayAdapter = new EventArrayAdapter(mContext, events, customizer);
        eventsList.setAdapter(eventArrayAdapter);


        Button deleteFacilityButton = view.findViewById(R.id.button);

        // Add sample events
        DB_Client db = new DB_Client();

        db.findAll("Events",  null, new DB_Client.DatabaseCallback<List<Event>>() {
            @Override
            public void onSuccess(@Nullable List<Event> data) {
                events.clear();
                if(data != null){
                    events.addAll(data);
                }
                eventArrayAdapter.notifyDataSetChanged();
            }
        }, Event.class);



        eventsList.setOnItemClickListener((parent, view1, position, id) -> {
            Event clickedEvent = events.get(position);
            Log.d("ListEventsFragment", "Clicked an event at position " + position);
            if(clickedEvent != null) {
                // Replace fragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.content_fragmentcontainer, new OrganizerViewEventFragment(clickedEvent)) // Use your actual container ID
                        .addToBackStack(null) // Allows user to go back to ListEventsFragment
                        .commit();
            }
        });

        // set button callback to delete this facility
        deleteFacilityButton.setOnClickListener(v -> {
            // delete this facility
        });

        return view;
    }
}
