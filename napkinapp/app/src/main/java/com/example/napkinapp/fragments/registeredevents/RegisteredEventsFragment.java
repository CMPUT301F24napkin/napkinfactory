package com.example.napkinapp.fragments.registeredevents;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
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
    RegisteredEventArrayAdapter eventArrayAdapter;
    private ArrayList<Event> events;

    public RegisteredEventsFragment() {
    }

    public RegisteredEventsFragment(User user) {
        loggedInUser = user;
    }

    RegisteredEventArrayAdapter.RegisteredEventListCustomizer customizer = (button1, button2, text3, event) -> {
        Log.i("IsEventInUser", "Chosen: " + loggedInUser.getChosen().contains(event.getId()) + "\n Registered: " + loggedInUser.getRegistered().contains(event.getId()));
        // TODO: Bug where when user registers for event and then leaves screen, both chosen and registered lists return true for containing event
        if (loggedInUser.getRegistered().contains(event.getId())) {
            // this user has been chosen and then accepted. display a text.
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
            text3.setText("You have accepted this event.");
            Log.i("RegisteredEventsFragment", String.format("ARRAY ADDAPTER eventid %s is in registered", event.getId()));
        } else if (loggedInUser.getChosen().contains(event.getId())) {
            // this user has been chosen but not yet accepted nor decline. display buttons.
            setUpChosenEvent(event, button1, button2, text3);
            Log.i("RegisteredEventsFragment", String.format("eventid %s is in chosen", event.getId()));

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

        if (context instanceof TitleUpdateListener) {
            titleUpdateListener = (TitleUpdateListener) context;
        } else {
            throw new RuntimeException(context + " needs to implement TitleUpdateListener");
        }

        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_list, container, false);
        ListView eventslist;

        eventslist = view.findViewById(R.id.events_list_view);
        events = new ArrayList<>();

        //Update title
        titleUpdateListener.updateTitle("Registered Events");

        // Attach EventArrayAdapter to ListView
        eventArrayAdapter = new RegisteredEventArrayAdapter(mContext, events, customizer);
        eventslist.setAdapter(eventArrayAdapter);

        // Load registered/chosen events
        initializeList();

        eventslist.setOnItemClickListener((parent, view1, position, id) -> {
            Event clickedEvent = events.get(position);
            Log.d("RegisteredEventsFragment", "Clicked an event at position " + position);
            if (clickedEvent != null) {
                // Replace fragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.content_fragmentcontainer, new ViewEventFragment(clickedEvent, loggedInUser)) // Use your actual container ID
                        .addToBackStack(null) // Allows user to go back to RegisteredEventsFragment
                        .commit();
            }
        });
        return view;
    }

    /**
     * Loads the list of registered and chosen events pertaining to the user
     */
    private void initializeList() {
        DB_Client db = new DB_Client();
        ArrayList<String> androidIds = loggedInUser.getChosen();
        androidIds.addAll(loggedInUser.getRegistered());

        // Query requires non empty list
        if (androidIds.isEmpty()) {
            return;
        }

        db.findAllIn("Events", "id", new ArrayList<>(androidIds), new DB_Client.DatabaseCallback<List<Event>>() {
            @Override
            public void onSuccess(@Nullable List<Event> data) {
                events.clear();
                if (data != null) {
                    events.addAll(data);
                }
                eventArrayAdapter.notifyDataSetChanged();

                Log.d("RegisteredEventsFragment", "Event list loaded with " + events.size() + " items.");
            }
        }, Event.class);
    }

    private void setUpChosenEvent(Event event, Button accept, Button decline, TextView txt) {
        accept.setText("Accept");
        accept.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0);
        accept.setOnClickListener(v -> {
            // Register user for event
            registerUser(event);
        });

        decline.setText("Decline");
        decline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_close_24, 0, 0, 0);
        decline.setOnClickListener(v -> {
            // Reject event
            declineEvent(event);
            Log.i("Button", String.format("List Events: Clicked on event %s\n", event.getName()));
        });
        txt.setVisibility(View.GONE);

        Log.i("RegisteredEventsFragment", String.format("eventid %s is in chosen", event.getId()));
    }

    private void registerUser(Event event) {
        // move this event from Chosen to Registered
        // add to this user's copy
        loggedInUser.addEventToRegistered(event.getId());
        loggedInUser.removeEventFromChosen(event.getId());

        event.addUserToRegistered(loggedInUser.getAndroidId());
        event.removeUserFromChosen(loggedInUser.getAndroidId());

        DB_Client db = new DB_Client();
        db.writeData("Users", loggedInUser.getAndroidId(), loggedInUser, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                DB_Client.DatabaseCallback.super.onSuccess(data);
                Log.i("Register User", "Successfully registered user for " + event.getName());
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Register User", "Something went wrong registering user " + loggedInUser.getName() + " " + loggedInUser.getAndroidId() + " for event " + event.getName());
            }
        });

        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                DB_Client.DatabaseCallback.super.onSuccess(data);
                Log.i("Register User - Event", "Successfully registered user for " + event.getName());
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Register User - Event", "Something went wrong registering user " + loggedInUser.getName() + " " + loggedInUser.getAndroidId() + " for event " + event.getName());
            }
        });

        eventArrayAdapter.notifyDataSetChanged();
    }

    private void declineEvent(Event event) {
        loggedInUser.removeEventFromChosen(event.getId());
        event.addUserToCancelled(loggedInUser.getAndroidId());

        DB_Client db = new DB_Client();
        db.writeData("Users", loggedInUser.getAndroidId(), loggedInUser, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                DB_Client.DatabaseCallback.super.onSuccess(data);
                Log.i("Cancelling User", "Successfully canelled user for " + event.getName());
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Cancelling User", "Something went wrong cancelling user " + loggedInUser.getName() + " " + loggedInUser.getAndroidId() + " for event " + event.getName());
            }
        });

        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                DB_Client.DatabaseCallback.super.onSuccess(data);
                Log.i("Cancel User - Event", "Successfully cancelled user for " + event.getName());
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Cancel User - Event", "Something went wrong cancelling user " + loggedInUser.getName() + " " + loggedInUser.getAndroidId() + " for event " + event.getName());
            }
        });

        eventArrayAdapter.notifyDataSetChanged();
    }
}
