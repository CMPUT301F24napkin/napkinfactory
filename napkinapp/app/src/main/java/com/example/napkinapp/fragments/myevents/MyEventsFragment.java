/**
 * This file is literally the same as ListEventsFragment.java however the parameters to the constructor
 * of the EventArrayAdapter are different to allow for different button look and functionality.
 */

package com.example.napkinapp.fragments.myevents;

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
import com.example.napkinapp.models.Event;

import java.util.ArrayList;
import java.util.Date;

public class MyEventsFragment extends Fragment {
    private ListView eventslist;
    private ArrayList<Event> events;
    private EventArrayAdapter eventArrayAdapter;
    private TitleUpdateListener titleUpdateListener;

    public MyEventsFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof TitleUpdateListener){
            titleUpdateListener = (TitleUpdateListener) context;
        }else{
            throw new RuntimeException(context + " needs to implement TitleUpdateListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_list, container, false);

        eventslist = view.findViewById(R.id.events_list_view);
        events = new ArrayList<>();

        //Update title
        titleUpdateListener.updateTitle("My Events");

        // Add sample events
        for (int i = 0; i < 7; i++) {
            events.add(new Event(String.valueOf(i), "Event " + i, new Date()));
        }

        // Attach EventArrayAdapter to ListView
        eventArrayAdapter = new EventArrayAdapter(getContext(), events, "View", 0, v -> {
            Event event = (Event)v.getTag();
            Log.i("Button", String.format("Clicked on event %s\n", event.getName()));
        });
        eventslist.setAdapter(eventArrayAdapter);

        Log.d("MyEventsFragment", "Event list loaded with " + events.size() + " items.");
        return view;
    }
}
