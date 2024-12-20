/**
 * List all events that are currently available in the db.
 * Allows filtering by waitlisted, etc.
 * Does not show your own events.
 */

package com.example.napkinapp.fragments.listevents;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.location.Location;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Tag;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.Location_Utils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * custom button customization callback. call the handleToggleButton method.
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
            btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorRemoveDark)));
            btn.setSelected(true);
        } else {
            // It is not
            btn.setText(R.string.add_to_waitlist);
            btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.neutralGray)));
            btn.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.add, 0, 0, 0);
            btn.setSelected(false);
        }
    }

    /**
     * Removes the logged in user from the event deeply.
     * @param event the event to delete.
     */
    public void removeEventFromWaitlist(Event event) {
        event.removeUserFromWaitList(loggedInUser.getAndroidId());
        loggedInUser.removeEventFromWaitList(event.getId());

        DB_Client db = new DB_Client();

        // Update events
        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(mContext, "Left waitlist for " + event.getName() + "!", Toast.LENGTH_SHORT).show();
                });
                Log.d("ListEventsFragment", "Removed event from waitlist! " + event.getName() + " id: " + event.getId() + loggedInUser.getName());
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Remove Event from waitlist", "Something went wrong! " + e);
            }
        });

        // Update person
        db.writeData("Users", loggedInUser.getAndroidId(), loggedInUser, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Log.d("ListEventsFragment", "Removed event from users waitlist " + loggedInUser.getName());
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Remove Event to waitlist", "Something went wrong! " + e);
            }
        });

    }

    /**
     * Add a event to waitlist. Adds it to both copies of waitlist, on the Event and User.
     * @param event event to add current logged in user to.
     */
    public void addEventToWaitlist(Event event) {
        DB_Client db = new DB_Client();
        event.addUserToWaitlist(loggedInUser.getAndroidId());

        if (loggedInUser.getEnLocation()) {
            Location_Utils locationUtils = new Location_Utils(this.getContext());
            locationUtils.getLastLocation(new Location_Utils.LocationCallbackInterface() {
                @Override
                public void onLocationRetrieved(Location location) {
                    // Handle location data
                    Log.d("ListEventsFragment", "Location: " + location.getLatitude() + ", " + location.getLongitude());
                    ArrayList<Double> coordinates = new ArrayList<>();
                    coordinates.add(location.getLatitude());
                    coordinates.add(location.getLongitude());
                    event.addEntrantLocation(loggedInUser.getAndroidId(), coordinates);
                    // Update events
                    db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
                        @Override
                        public void onSuccess(@Nullable Void data) {
                            Toast.makeText(mContext, "Joined waitlist for " + event.getName() + "!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            DB_Client.DatabaseCallback.super.onFailure(e);
                            Log.e("Adding Event to waitlist", "Something went wrong! " + e);
                        }
                    });
                    loggedInUser.addEventToWaitlist(event.getId());
                    // Update person
                    db.writeData("Users", loggedInUser.getAndroidId(), loggedInUser, new DB_Client.DatabaseCallback<Void>() {
                        @Override
                        public void onSuccess(@Nullable Void data) {
                            Log.d("Adding Event to waitlist", "Added event to users waitlist! " + loggedInUser.getName());
                        }

                        @Override
                        public void onFailure(Exception e) {
                            DB_Client.DatabaseCallback.super.onFailure(e);
                            Log.e("Adding Event to waitlist", "Something went wrong! " + e);
                        }
                    });
                }

                @Override
                public void onPermissionDenied() {
                    // Handle permission denial
                    Log.e("ListEventsFragment", "Location permissions are denied!");
                    Toast.makeText(mContext, "Location permissions are denied!", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 102);
                }

                @Override
                public void onError(Exception e) {
                    // Handle errors
                    Log.e("ListEventsFragment", "Error retrieving location: " + e.getMessage());
                }
            });
        } else if (event.isRequireGeolocation()){
            showGeolocationPopup(loggedInUser);
        } else {
            db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
                @Override
                public void onSuccess(@Nullable Void data) {

                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(mContext, "Joined the waitlist for " + event.getName(), Toast.LENGTH_SHORT).show();
                    });
                  
                    loggedInUser.addEventToWaitlist(event.getId());
                    // Update person
                    db.writeData("Users", loggedInUser.getAndroidId(), loggedInUser, new DB_Client.DatabaseCallback<Void>() {
                        @Override
                        public void onSuccess(@Nullable Void data) {
                            Log.d("Adding Event to waitlist", "Added event to users waitlist! " + loggedInUser.getName());

                        }

                        @Override
                        public void onFailure(Exception e) {
                            DB_Client.DatabaseCallback.super.onFailure(e);
                            Log.e("Adding Event to waitlist", "Something went wrong! " + e);
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    DB_Client.DatabaseCallback.super.onFailure(e);
                    Log.e("Adding Event to waitlist", "Something went wrong! " + e);
                }
            });
        }
    }

    public void showGeolocationPopup(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Geolocation Required")
                .setMessage("This event requires geolocation to proceed.")
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 102);
                        }

                        user.setEnLocation(true);
                        DB_Client db = new DB_Client();
                        db.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.e("User update/creation", "Something went wrong updating user");
                                Toast.makeText(getContext(), "Error communication with database! Please try again!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(@Nullable Void data) {
                                Log.i("User update/creation", "User updated/created");
                                Toast.makeText(getContext(), "Enabled Geolocation", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * Load the chips based on harcoded defaults.
     * @param inflater inflated to use to inflate new chip obejcts from
     */
    private void loadChips(@NonNull LayoutInflater inflater) {
        ArrayList<String> tags = new ArrayList<>();
        tags.add("Waitlist");
        Log.d("TAG", "loading chips");

        DB_Client db = new DB_Client();
        db.findAll("Tags", null, new DB_Client.DatabaseCallback<List<Tag>>() {

            @Override
            public void onSuccess(@Nullable List<Tag> data) {
                if(data == null){
                    return;
                }

                data.forEach(tag -> tags.add(tag.getName()));

                Log.d("TAGS", "Retrieved tags: " + tags);

                for (String tag : tags) {
                    Log.d("GENERATING TAG", "Generating tag for name: " + tag);
                    Chip chip = (Chip)inflater.inflate(R.layout.chip_base, chips, false);
                    chip.setText(tag);

                    chips.addView(chip);
                }
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
            }
        }, Tag.class);
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
            filterEvents(selectedCategories);
        }
    }

    /**
     * filters the events by selected tags. notifies the arrayAdapter that data changed.
     * @param selectedCategories
     */
    private void filterEvents(ArrayList<String> selectedCategories) {
        // Implement when tags are added
        DB_Client db = new DB_Client();

        events.clear();

        if(selectedCategories.contains("Waitlist")) {
            Log.i("Chips", "Selected categories contains waitlist!");
            if(!loggedInUser.getWaitlist().isEmpty()) {
                db.findAllIn("Events", "id", new ArrayList<>(loggedInUser.getWaitlist()), new DB_Client.DatabaseCallback<List<Event>>() {
                    @Override
                    public void onSuccess(@Nullable List<Event> data) {
                        if (data == null)
                            return;

                        // Check if anything other than waitlist checked
                        if(selectedCategories.size() > 1){
                            // Doing add event ensures no dupes are shown by spamming
                            ArrayList<String> tempList = new ArrayList<>(selectedCategories.subList(1, selectedCategories.size()));
                            for(Event event: data){
                                if(event.getTags() != null && event.getTags().containsAll(tempList)){
                                    events.add(event);
                                }
                            }
                        }else{
                            events.addAll(data);
                        }

                        eventArrayAdapter.notifyDataSetChanged();
                        Log.i("Chip Query", "Filter by Waitlist Success");
                    }
                }, Event.class);
            }
        } else {
            List<Function<Query, Query>> conditions = List.of(
                    query -> query.whereNotEqualTo("organizerId", loggedInUser.getAndroidId())
            );

            db.executeQueryList("Events", conditions, new DB_Client.DatabaseCallback<List<Event>>() {
                @Override
                public void onSuccess(@Nullable List<Event> data) {
                    if(data == null)
                        return;

                    for(Event event: data){
                        if(event.getTags() != null && event.getTags().containsAll(selectedCategories)){
                            events.add(event);
                        }
                    }

                    eventArrayAdapter.notifyDataSetChanged();

                    Log.i("Chip Query", "Filter by tags: " + selectedCategories + " Was Successful");
                }

                @Override
                public void onFailure(Exception e) {
                    DB_Client.DatabaseCallback.super.onFailure(e);
                }
            }, Event.class);
        }
        eventArrayAdapter.notifyDataSetChanged(); // already cleared later, just update adapter
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
                    for(Event event: data){
                        if (event.getChosen().contains(loggedInUser.getAndroidId()) || event.getRegistered().contains(loggedInUser.getAndroidId()) || event.getCancelled().contains(loggedInUser.getAndroidId())){
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
}
