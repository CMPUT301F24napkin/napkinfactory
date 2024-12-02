/**
 * Fragment for viewing the events a user is registered in.
 * Customizes the look of the event card based on its status;
 * - if it is waitlisted, do not show it
 * - if the user is chosen, provide two buttons to accept or decline the invitation
 * - if the user is registered, display some text saying you registered
 * - if the user is cancelled, do not show it.
 */

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
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

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

    /**
     * customize view of buttons based on if current logged in user is waitlisted, chosen, registered.
     */
    RegisteredEventArrayAdapter.RegisteredEventListCustomizer customizer = (button1, button2, text3, event) -> {
        Log.i("IsEventInUser", "Chosen: " + loggedInUser.getChosen().contains(event.getId()) + "\n Registered: " + loggedInUser.getRegistered().contains(event.getId()));
        // TODO: Bug where when user registers for event and then leaves screen, both chosen and registered lists return true for containing event
        if (event.getRegistered().contains(loggedInUser.getAndroidId())) {
            // this user has been chosen and then accepted. display a text.
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
            text3.setText("You have accepted this event");
            Log.i("RegisteredEventsFragment", String.format("ARRAY ADDAPTER eventid %s is in registered", event.getId()));
        } else if (loggedInUser.getChosen().contains(event.getId())) {
            // this user has been chosen but not yet accepted nor decline. display buttons.
            setUpChosenEvent(event, button1, button2, text3);
            Log.i("RegisteredEventsFragment", String.format("eventid %s is in chosen", event.getId()));

        } else {
            Log.i("RegisteredEventsFragment", String.format("eventid %s is none!", event.getId()));
            button1.setVisibility(View.GONE);
            button2.setVisibility(View.GONE);
            text3.setText("You have declined this event");
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
        List<Function<Query, Query>> conditions = List.of(
                query -> query.whereNotEqualTo("organizerId", loggedInUser.getAndroidId())
        );
        db.executeQueryList("Events", conditions, new DB_Client.DatabaseCallback<List<Event>>() {
                    @Override
                    public void onSuccess(@Nullable List<Event> data) {
                        events.clear();
                        if (data != null) {
                            events.addAll(data);
                            for (Event event : data) {
                                if (event.getChosen().contains(loggedInUser.getAndroidId()) || event.getRegistered().contains(loggedInUser.getAndroidId())) {
                                    continue;
                                } else {
                                    events.remove(event);
                                }
                            }
                        }

                        eventArrayAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onFailure(Exception e) {
                        DB_Client.DatabaseCallback.super.onFailure(e);
                    }
                }, Event.class);
    }


    /**
     * set up event card in the case that the event is in the chosen list
     * @param event the event
     * @param accept the accept button handle
     * @param decline the decline button handle
     * @param txt the text view handle
     */
    private void setUpChosenEvent(Event event, Button accept, Button decline, TextView txt) {
        accept.setText("Accept");
        accept.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0);
        accept.setOnClickListener(v -> {
            // Register user for event
            registerUser(event);
            txt.setText("You have accepted this event");
            txt.setVisibility(View.VISIBLE);
        });
        accept.setVisibility(View.VISIBLE);

        decline.setText("Decline");
        decline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_close_24, 0, 0, 0);
        decline.setOnClickListener(v -> {
            // Reject event
            declineEvent(event);
            txt.setVisibility(View.VISIBLE);
            txt.setText("You have decline this event");
            Log.i("Button", String.format("List Events: Clicked on event %s\n", event.getName()));
        });
        decline.setVisibility(View.VISIBLE);
        txt.setVisibility(View.GONE);

        Log.i("RegisteredEventsFragment", String.format("eventid %s is in chosen", event.getId()));
    }

    /**
     * helper function to register the currently logged in user in an event. Does it deeply.
     * @param event the event to register in
     */
    public void registerUser(Event event) {
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

        getActivity().runOnUiThread(() -> {
            eventArrayAdapter.notifyDataSetChanged();
        });
    }

    /**
     * Make the currently logged in user decline this event. Does it deeply.
     * Works by moving the currently logged in user's androidId out of the chosen list into the cancelled list.
     * @param event the event to decline
     */
    public void declineEvent(Event event) {
        loggedInUser.removeEventFromChosen(event.getId());
        event.addUserToCancelled(loggedInUser.getAndroidId());
        event.removeUserFromChosen(loggedInUser.getAndroidId());

        DB_Client db = new DB_Client();
        db.writeData("Users", loggedInUser.getAndroidId(), loggedInUser, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                DB_Client.DatabaseCallback.super.onSuccess(data);
                Log.i("Cancelling User", "Successfully cancelled user for " + event.getName());
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

        getActivity().runOnUiThread(() -> {
            eventArrayAdapter.notifyDataSetChanged();
        });
    }
}
