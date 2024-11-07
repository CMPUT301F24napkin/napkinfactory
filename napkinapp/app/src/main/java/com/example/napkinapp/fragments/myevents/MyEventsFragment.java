/**
 * This file is literally the same as ListEventsFragment.java however the parameters to the constructor
 * of the EventArrayAdapter are different to allow for different button look and functionality.
 */

package com.example.napkinapp.fragments.myevents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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
import com.example.napkinapp.models.Event;
import com.example.napkinapp.utils.DB_Client;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyEventsFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;

    public MyEventsFragment() {

    }

    EventArrayAdapter.EventListCustomizer customizer = button -> {
        button.setText("View");
        button.setOnClickListener(v->{
            Event event = (Event)v.getTag();
            Log.i("Button", String.format("My Events: Clicked on event %s\n", event.getName()));
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
        //Update title
        titleUpdateListener.updateTitle("My Events");

        View view = inflater.inflate(R.layout.event_list, container, false);
        ListView eventsList = view.findViewById(R.id.events_list_view);
        ArrayList<Event> events = new ArrayList<>();

        // Attach EventArrayAdapter to ListView
        EventArrayAdapter eventArrayAdapter = new EventArrayAdapter(mContext, events, customizer);
        eventsList.setAdapter(eventArrayAdapter);


        // Add sample events
        DB_Client db = new DB_Client();
        db.findAll("Events", null, new DB_Client.DatabaseCallback<List<Event>>() {
            @Override
            public void onSuccess(@Nullable List<Event> data) {
                events.clear();
                events.addAll(data);
                eventArrayAdapter.notifyDataSetChanged();
            }
        }, Event.class);


        // set callback on trailing button

        @SuppressLint("InflateParams") // if root is set to container, the app crashes, if null, get warning. so supress.
        View footerView = inflater.inflate(R.layout.event_list_footer_button, null, false);
        Button trailingButton = footerView.findViewById(R.id.trailing_button);
        trailingButton.setOnClickListener(v-> {
            Log.i("Button", "My Events: Clicked on the create event button!");
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_fragmentcontainer, new CreateEventFragment())
                    .addToBackStack(null)
                    .commit();
        });


        eventsList.addFooterView(footerView);
        return view;
    }
}
