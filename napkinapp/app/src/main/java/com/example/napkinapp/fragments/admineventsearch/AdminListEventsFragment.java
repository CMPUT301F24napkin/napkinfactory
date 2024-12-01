/**
 * Fragment for the Admin search events page.
 * Current issue is that it does a shallow delete of the event and should delete references to thsi event too.
 */

package com.example.napkinapp.fragments.admineventsearch;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AdminListEventsFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;
    private ArrayList<Event> events;
    private AdminEventArrayAdapter eventArrayAdapter;
    private DB_Client db;
    public AdminListEventsFragment() {
        // Required null constructor
    }

    AdminEventArrayAdapter.EventListCustomizer customizer = button -> {
        button.setText("Remove");
        button.setOnClickListener(v -> {
            Event event = (Event) v.getTag();
            Log.i("Button", String.format("List Events: Clicked on event %s\n", event.getName()));

            // Call the delete method with the event ID
            deleteEvent(event);
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
        View view = inflater.inflate(R.layout.admin_event_search, container, false);
        ListView eventsListView = view.findViewById(R.id.events_list_view);
        EditText searchEventName = view.findViewById(R.id.search_event_name);
        Button searchButton = view.findViewById(R.id.search_button);
        db = new DB_Client();

        events = new ArrayList<>();
        eventArrayAdapter = new AdminEventArrayAdapter(mContext, events, customizer);
        eventsListView.setAdapter(eventArrayAdapter);

        // Update title
        titleUpdateListener.updateTitle("Event List");

        // Load all events initially
        loadEvents("");

        // Set up search button click listener
        searchButton.setOnClickListener(v -> {
            String query = searchEventName.getText().toString().trim();
            loadEvents(query);
            //searchEventsByName(query);
        });

        return view;
    }

    private void loadEvents(String eventName) {
        // Create a query with a "like" match on event names
        List<Function<Query, Query>> conditions = List.of(
                query -> query.whereGreaterThanOrEqualTo("name", eventName),
                query -> query.whereLessThanOrEqualTo("name", eventName + "\uf8ff")
        );

        // Execute the query using the modified executeQuery method
        db.executeQueryList("Events", conditions, new DB_Client.DatabaseCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> data) {
                Log.d("data", data != null ? data.toString() : "No data returned");
                events.clear();
                if (data != null && !data.isEmpty()) {
                    events.addAll(data);
                    eventArrayAdapter.notifyDataSetChanged();
                    Log.d("RegisteredEventsFragment", "Event list loaded with " + events.size() + " items.");
                } else {
                    Log.d("RegisteredEventsFragment", "No events found matching the name.");
                    eventArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("RegisteredEventsFragment", "Error loading events: " + e.getMessage(), e);
            }
        }, Event.class);
    }

    private void
    deleteEvent(Event event) {
        if (event.getId() == null || event.getId().isEmpty()) {
            Log.e("RegisteredEventsFragment", "Event ID is null or empty. Cannot delete event.");
            return;
        }

        // remove from users waitlists first
        String eventId = event.getId();
        ArrayList<String> userIds = event.getWaitlist();

        for (String userID : userIds) {
            Map<String, Object> filters = new HashMap<>();
            filters.put("androidId", userID);
            Log.d("RegisteredEventsFragment", "User ID: " + userID);

            db.findOne("Users", filters, new DB_Client.DatabaseCallback<User>() {
                @Override
                public void onSuccess(@Nullable User user) {
                    if (user == null) {
                        Log.e("RegisteredEventsFragment", "Event not found for ID: " + eventId);
                        return;
                    }
                    List<String> waitlist = (List<String>) user.getWaitlist();
                    List<String> registered = (List<String>) user.getRegistered();
                    // adjust the waitlist
                    waitlist.remove(eventId);
                    registered.remove(eventId);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("waitlist", waitlist);
                    updates.put("registered", registered);
                    db.updateAll("Users", filters, updates, new DB_Client.DatabaseCallback<Void>() {});

                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(mContext, "Failed to get event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("RegisteredEventsFragment", "Error getting evnet", e);
                }
            }, User.class);
        }



        // Set up the filter to find the event by its ID
        Map<String, Object> filters = new HashMap<>();
        filters.put("id", event.getId());

        // Call DB_Client's deleteOne method
        db.deleteOne("Events", filters, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Event deleted successfully", Toast.LENGTH_SHORT).show();
                events.remove(event); // Remove the event from the local list
                eventArrayAdapter.notifyDataSetChanged(); // Update the adapter
                Log.d("RegisteredEventsFragment", "Event deleted from Firestore and list updated.");
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(mContext, "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RegisteredEventsFragment", "Error deleting event", e);
            }
        });
    }
}