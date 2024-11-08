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
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;

import java.util.ArrayList;
import java.util.List;

public class ListEventsFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;
    private User loggedInUser;

    public ListEventsFragment(){}
    public ListEventsFragment(User user){
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
            }
        }, Event.class);

        Log.d("ListEventsFragment", "Event list loaded with " + events.size() + " items.");
        return view;
    }
}
