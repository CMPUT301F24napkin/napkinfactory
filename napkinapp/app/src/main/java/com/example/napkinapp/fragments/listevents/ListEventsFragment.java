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
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;

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

        // Add sample events
        for (int i = 0; i < 5; i++) {
            events.add(new Event(String.valueOf(i), "Event " + i, new Date()));
        }

        // Attach EventArrayAdapter to ListView
        eventArrayAdapter = new EventArrayAdapter(getContext(), events, "Add to Watchlist", R.drawable.add, v -> {
            Event event = (Event)v.getTag();
            Log.i("Button", String.format("Clicked on event %s\n", event.getName()));
        });

        eventslist.setAdapter(eventArrayAdapter);

        Log.d("ListEventsFragment", "Event list loaded with " + events.size() + " items.");
        return view;
    }
}
