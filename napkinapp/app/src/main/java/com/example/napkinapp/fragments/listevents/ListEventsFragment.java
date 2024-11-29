/**
 * List all events that are currently available in the db.
 * Allows filtering by waitlisted, etc.
 * Does not show your own events.
 */

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
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListEventsFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;
    private User loggedInUser;
    private ChipGroup chips;
    private ArrayList<Event> events;
    EventArrayAdapter eventArrayAdapter;

    public ListEventsFragment() {
    }

    public ListEventsFragment(User user) {
        loggedInUser = user;
    }

    /**
     * custom button customization callback. call the handleToggleBUtton method.
     */
    EventArrayAdapter.EventListCustomizer customizer = (button, event) -> {
        updateButtonState(button, event);
        button.setOnClickListener(v -> {
            Log.i("Button", String.format("List Events: Clicked on event %s\n", event.getName()));
            handleToggleButtonClick(button, event);
        });
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
        chips = view.findViewById(R.id.chipGroup);

        eventslist = view.findViewById(R.id.events_list_view);
        events = new ArrayList<>();

        //Update title
        titleUpdateListener.updateTitle("Event List");

        // Attach EventArrayAdapter to ListView
        eventArrayAdapter = new EventArrayAdapter(mContext, events, customizer);
        eventslist.setAdapter(eventArrayAdapter);

        // Load event
        displayAllEvents();

        // Load chips
        loadChips(inflater);

        // Set listeners
        eventslist.setOnItemClickListener((parent, view1, position, id) -> {
            Event clickedEvent = events.get(position);
            Log.d("ListEventsFragment", "Clicked an event at position " + position);
            if (clickedEvent != null) {
                // Replace fragment
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.content_fragmentcontainer, new ViewEventFragment(clickedEvent, loggedInUser)) // Use your actual container ID
                        .addToBackStack(null) // Allows user to go back to ListEventsFragment
                        .commit();
            }
        });

        chips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            Log.i("Chip", "checking chips " + checkedIds);
            handleChipSelection(group, checkedIds);
        });

        return view;
    }

    /**
     * handle button clicks in the list of events.
     * @param btn the main button
     * @param event the event attached to this event card
     */
    private void handleToggleButtonClick(Button btn, Event event) {
        Toast.makeText(mContext, "Button state: " + btn.isSelected(), Toast.LENGTH_SHORT).show();
        if (btn.isSelected()) {
            removeEventFromWaitlist(event);
        } else {
            addEventToWaitlist(event);
        }
        updateButtonState(btn, event);
        handleChipSelection(chips, chips.getCheckedChipIds());
    }

    /**
     * Updates the button's state based on whether the logged in user is on its waitlist or not.
     * @param btn the button to update
     * @param event the event to check if this user is waitlisted in.
     */
    private void updateButtonState(Button btn, Event event) {
        if (event.getWaitlist().contains(loggedInUser.getAndroidId())) {
            // It is waitlisted
            btn.setText(R.string.remove_from_waitlist);
            btn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.remove, 0, 0, 0);
            btn.setSelected(true);
        } else {
            // It is not
            btn.setText(R.string.add_to_waitlist);
            btn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.add, 0, 0, 0);
            btn.setSelected(false);
        }
    }

    /**
     * Removes the logged in user from the event deeply.
     * @param event the event to delete.
     */
    private void removeEventFromWaitlist(Event event) {
        event.removeUserFromWaitList(loggedInUser.getAndroidId());
        loggedInUser.removeEventFromWaitList(event.getId());

        DB_Client db = new DB_Client();

        // Update events
        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(mContext, "Added event to waitlist! " + event.getName() + " id: " + event.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Adding Event to waitlist", "Something went wrong! " + e);
            }
        });

        // Update person
        db.writeData("Users", loggedInUser.getAndroidId(), loggedInUser, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(mContext, "Added event to users waitlist! " + loggedInUser.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Adding Event to waitlist", "Something went wrong! " + e);
            }
        });

    }

    /**
     * Add a event to waitlist. Adds it to both copies of waitlist, on the Event and User.
     * @param event event to add current logged in user to.
     */
    private void addEventToWaitlist(Event event) {
        event.addUserToWaitlist(loggedInUser.getAndroidId());
        loggedInUser.addEventToWaitlist(event.getId());

        DB_Client db = new DB_Client();

        // Update events
        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(mContext, "Added event to waitlist! " + event.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Adding Event to waitlist", "Something went wrong! " + e);
            }
        });

        // Update person
        db.writeData("Users", loggedInUser.getAndroidId(), loggedInUser, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(mContext, "Added event to users waitlist! " + loggedInUser.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Adding Event to waitlist", "Something went wrong! " + e);
            }
        });
    }

    /**
     * Load the chips based on harcoded defaults.
     * @param inflater inflated to use to inflate new chip obejcts from
     */
    private void loadChips(@NonNull LayoutInflater inflater) {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("Waitlist");

        // Grab tags from tag object

        for (String tag : tags) {
            Chip chip = (Chip)inflater.inflate(R.layout.chip_base, chips, false);

            chip.setText(tag);

            chips.addView(chip);
        }
    }

    /**
     * Handle what happens when a chip is checked. Redraw all events in the eventList.
     * @param group
     * @param checkedIds
     */
    private void handleChipSelection(ChipGroup group, List<Integer> checkedIds) {

        ArrayList<String> selectedCategories = new ArrayList<>();
        for (int id : checkedIds) {
            Chip selectedChip = group.findViewById(id);

            if (selectedChip != null) {
                String category = selectedChip.getText().toString();
                selectedCategories.add(category);
                Log.i("Chip", String.join(", ", selectedCategories));
            }
        }

        if (selectedCategories.isEmpty()) {
            displayAllEvents();
        } else {
            filterEventsWaitlist(selectedCategories);
        }
    }

    private void filterEvents(ArrayList<String> selectedCategories) {
        // Implement when tags are added
    }

    /**
     * filters the events by who is on the waitlist. notifies the arrayAdapter that data changed.
     * @param selectedCategories
     */
    private void filterEventsWaitlist(ArrayList<String> selectedCategories) {
        DB_Client db = new DB_Client();

        events.clear();

        if(selectedCategories.contains("Waitlist")) {
            if(!loggedInUser.getWaitlist().isEmpty()) {
                db.findAllIn("Events", "id", new ArrayList<>(loggedInUser.getWaitlist()), new DB_Client.DatabaseCallback<List<Event>>() {
                    @Override
                    public void onSuccess(@Nullable List<Event> data) {
                        if (data != null) {
                            events.addAll(data);
                            eventArrayAdapter.notifyDataSetChanged();
                            Log.i("Chip Query", "Filter by Waitlist Success");
                        }
                    }
                }, Event.class);
            }
        }

        // Implement when tags are added
    }

    /**
     * Does not filter by events and updated eventList to display all possible events, excluding your own.
     */
    private void displayAllEvents() {
        DB_Client db = new DB_Client();
        List<Function<Query, Query>> conditions = List.of(
                query -> query.whereNotEqualTo("organizerId", loggedInUser.getAndroidId())
        );
        db.executeQueryList("Events", conditions, new DB_Client.DatabaseCallback<List<Event>>() {
            @Override
            public void onSuccess(@Nullable List<Event> data) {
                events.clear();
                if(data != null){
                    events.addAll(data);
                }
                eventArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
            }
        }, Event.class);
    }
}
