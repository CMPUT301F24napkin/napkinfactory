package com.example.napkinapp.fragments.listevents;

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

import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.R;

import java.util.ArrayList;
import java.util.Date;

public class ListEventsFragment extends Fragment {
    private ListView eventslist;
    private ArrayList<Event> events;
    private EventArrayAdapter eventArrayAdapter;
    private TitleUpdateListener titleUpdateListener;

    public ListEventsFragment(){
        // Required null constructor
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
        titleUpdateListener.updateTitle("Event List");

        // Add sample events
        for (int i = 0; i < 5; i++) {
            events.add(new Event(String.valueOf(i), "Event " + i, new Date()));
        }

        // Attach EventArrayAdapter to ListView
        eventArrayAdapter = new EventArrayAdapter(getContext(), events);
        eventslist.setAdapter(eventArrayAdapter);

        Log.d("ListEventsFragment", "Event list loaded with " + events.size() + " items.");
        return view;
    }



}
