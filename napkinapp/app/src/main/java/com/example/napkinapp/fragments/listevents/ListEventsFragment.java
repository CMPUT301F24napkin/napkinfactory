package com.example.napkinapp.fragments.listevents;

import android.content.Context;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import androidx.fragment.app.Fragment;

import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
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
        updateButtonState(button, event);
        button.setOnClickListener(v->{
            Log.i("Button", String.format("List Events: Clicked on event %s\n", event.getName()));
            handleToggleButtonClick(button, event);
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

                Log.d("ListEventsFragment", "Event list loaded with " + events.size() + " items.");
            }
        }, Event.class);


        eventslist.setOnItemClickListener((parent, view1, position, id) -> {
            Event clickedEvent = events.get(position);
            Log.d("ListEventsFragment", "Clicked an event at position " + position);
            if(clickedEvent != null) {
                // Replace fragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.content_fragmentcontainer, new ViewEventFragment(clickedEvent)) // Use your actual container ID
                        .addToBackStack(null) // Allows user to go back to ListEventsFragment
                        .commit();
            }
        });
        return view;
    }

    private void handleToggleButtonClick(Button btn, Event event){
        Toast.makeText(mContext, "Button state: " + btn.isSelected(), Toast.LENGTH_SHORT).show();
        if(btn.isSelected()){
            removeEventFromWaitlist(event);
        }else{
            addEventToWaitlist(event);
        }
        updateButtonState(btn, event);
    }

    private void updateButtonState(Button btn, Event event){
        if(event.getWaitlist().contains(loggedInUser.getAndroidId())){
            // It is waitlisted
            btn.setText(R.string.remove_from_waitlist);
            btn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.remove, 0,0,0);
            btn.setSelected(true);
        }else{
            // It is not
            btn.setText(R.string.add_to_waitlist);
            btn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.add, 0,0,0);
            btn.setSelected(false);
        }
    }

    private void removeEventFromWaitlist(Event event){
        event.removeUserFromWaitList(loggedInUser.getAndroidId());
        loggedInUser.removeEventFromWaitList(event.getId());

        DB_Client db = new DB_Client();

        // Update events
        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(mContext, "Added event to waitlist! "+event.getName() + " id: "+event.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Adding Event to waitlist", "Something went wrong! "+e);
            }
        });

        // Update person
        db.writeData("Users", loggedInUser.getAndroidId(), loggedInUser, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(mContext,"Added event to users waitlist! " + loggedInUser.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Adding Event to waitlist", "Something went wrong! "+e);
            }
        });

    }

    private void addEventToWaitlist(Event event){
        event.addUserToWaitlist(loggedInUser.getAndroidId());
        loggedInUser.addEventToWaitlist(event.getId());

        DB_Client db = new DB_Client();

        // Update events
        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(mContext, "Added event to waitlist! "+event.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Adding Event to waitlist", "Something went wrong! "+e);
            }
        });

        // Update person
        db.writeData("Users", loggedInUser.getAndroidId(), loggedInUser, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(mContext,"Added event to users waitlist! " + loggedInUser.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Adding Event to waitlist", "Something went wrong! "+e);
            }
        });
    }
}
