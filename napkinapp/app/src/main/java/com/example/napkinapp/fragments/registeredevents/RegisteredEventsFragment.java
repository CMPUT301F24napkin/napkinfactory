package com.example.napkinapp.fragments.registeredevents;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.fragments.listevents.EventArrayAdapter;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;

import java.util.ArrayList;
import java.util.List;

public class RegisteredEventsFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;
    private User loggedInUser;

    public RegisteredEventsFragment(){}
    public RegisteredEventsFragment(User user){
        loggedInUser = user;
    }

    EventArrayAdapter.EventListCustomizer customizer = (button, event) -> {
        button.setText("Add to Watchlist");
        button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.add, 0, 0, 0);
        button.setOnClickListener(v->{
            Log.i("Button", String.format("List Events: Clicked on event %s\n", event.getName()));
        });
    };

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
        View view = inflater.inflate(R.layout.event_list, container, false);
        ListView eventslist;
        ArrayList<Event> events;
        EventArrayAdapter eventArrayAdapter;

        eventslist = view.findViewById(R.id.events_list_view);
        events = new ArrayList<>();

        //Update title
        titleUpdateListener.updateTitle("Event List");

        // Attach EventArrayAdapter to ListView
        eventArrayAdapter = new EventArrayAdapter(mContext, events, customizer);
        eventslist.setAdapter(eventArrayAdapter);

        DB_Client db = new DB_Client();
        db.findAll("Events", null, new DB_Client.DatabaseCallback<List<Event>>() {
            @Override
            public void onSuccess(@Nullable List<Event> data) {
                events.clear();
                events.addAll(data);
                eventArrayAdapter.notifyDataSetChanged();

                Log.d("RegisteredEventsFragment", "Event list loaded with " + events.size() + " items.");
            }
        }, Event.class);


        eventslist.setOnItemClickListener((parent, view1, position, id) -> {
            Event clickedEvent = events.get(position);
            Log.d("RegisteredEventsFragment", "Clicked an event at position " + position);
            if(clickedEvent != null) {
                // Replace fragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.content_fragmentcontainer, new ViewEventFragment(clickedEvent)) // Use your actual container ID
                        .addToBackStack(null) // Allows user to go back to RegisteredEventsFragment
                        .commit();
            }
        });
        return view;
    }
}
