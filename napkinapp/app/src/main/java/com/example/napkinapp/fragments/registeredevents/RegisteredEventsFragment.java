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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegisteredEventsFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;
    private User loggedInUser;
    RegisteredEventArrayAdapter eventArrayAdapter;

    public RegisteredEventsFragment(){}
    public RegisteredEventsFragment(User user){
        loggedInUser = user;
    }

    RegisteredEventArrayAdapter.RegisteredEventListCustomizer customizer = (button1, button2, text3, event) -> {
        if(loggedInUser.getWaitlist().contains(event.getId())) {
            // this event card is for a whitelist event! display text only.
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
            text3.setText("Waiting for organizer to draw lottery...");
            Log.i("RegisteredEventsFragment", String.format("eventid %s is in waitlist", event.getId()));

        } else if (loggedInUser.getChosen().contains(event.getId())) {
            // this user has been chosen but not yet accepted nor decline. display buttons.
            button1.setText("Accept");
            button1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.add, 0, 0, 0);
            button1.setOnClickListener(v->{
                // move this event from Chosen to Registered
                // add to this user's copy
                ArrayList<String> chosenCopy = loggedInUser.getChosen();
                ArrayList<String> registeredCopy = loggedInUser.getRegistered();

                chosenCopy.remove(event.getId());
                chosenCopy.add(event.getId());

                loggedInUser.setChosen(chosenCopy);
                loggedInUser.setRegistered(registeredCopy);

                DB_Client db = new DB_Client();
                db.writeData("Users", loggedInUser.getAndroidId(), loggedInUser, DB_Client.IGNORE);

                // add to event's copy
                chosenCopy = event.getChosen();
                registeredCopy = event.getRegistered();

                chosenCopy.remove(loggedInUser.getAndroidId());
                registeredCopy.add(loggedInUser.getAndroidId());

                event.setChosen(chosenCopy);
                event.setRegistered(registeredCopy);

                db.writeData("Events", event.getId(), loggedInUser, DB_Client.IGNORE);

                eventArrayAdapter.notifyDataSetChanged();
            });
            button1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0);

            button2.setText("Decline");
            button2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.add, 0, 0, 0);
            button2.setOnClickListener(v->{
                Log.i("Button", String.format("List Events: Clicked on event %s\n", event.getName()));
            });
            button2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_close_24, 0, 0, 0);
            text3.setVisibility(View.GONE);
            Log.i("RegisteredEventsFragment", String.format("eventid %s is in chosen", event.getId()));

        } else if(loggedInUser.getRegistered().contains(event.getId())) {
            // this user has been chosen and then accepted. display a text.
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
            text3.setText("You have accepted this event.");
            Log.i("RegisteredEventsFragment", String.format("eventid %s is in registered", event.getId()));

        } else {
            Log.i("RegisteredEventsFragment", String.format("eventid %s is none!", event.getId()));
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
            text3.setText(String.format("Evvent id=%s error", event.getId()));
        }
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

        eventslist = view.findViewById(R.id.events_list_view);
        events = new ArrayList<>();

        //Update title
        titleUpdateListener.updateTitle("Event List");

        // Attach EventArrayAdapter to ListView
        eventArrayAdapter = new RegisteredEventArrayAdapter(mContext, events, customizer);
        eventslist.setAdapter(eventArrayAdapter);

        DB_Client db = new DB_Client();
        ArrayList<String> androidIds = new ArrayList<>(loggedInUser.getWaitlist());
        androidIds.addAll(loggedInUser.getChosen());
        androidIds.addAll(loggedInUser.getRegistered());
        db.findAllIn("Events", "id", new ArrayList<>(androidIds), new DB_Client.DatabaseCallback<List<Event>>() {
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
