package com.example.napkinapp.fragments.admineventsearch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.napkinapp.R;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.fragments.admineventsearch.EventAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchEventFragment extends Fragment {

    private DB_Client dbClient;
    private RecyclerView eventsRecyclerView;
    private EventAdapter eventsAdapter; // Assuming you have a RecyclerView adapter for displaying events
    private EditText searchBar; // Add this to capture user input
    private Button searchButton; // Add this to trigger the search

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_event_search, container, false);

        dbClient = new DB_Client();
        eventsRecyclerView = view.findViewById(R.id.events_recycler_view);
        eventsAdapter = new EventAdapter();
        eventsRecyclerView.setAdapter(eventsAdapter);

        searchBar = view.findViewById(R.id.search_event_name); // Link to the search bar EditText
        searchButton = view.findViewById(R.id.search_button); // Link to the search button

        // Set up the search button click listener
        searchButton.setOnClickListener(v -> {
            String eventName = searchBar.getText().toString().trim();
            if (!eventName.isEmpty()) {
                searchEventsByName(eventName); // Search with the entered name
            } else {
                Toast.makeText(getContext(), "Please enter an event name to search", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void searchEventsByName(String eventName) {
        // Create a filter map to search by event name
        Map<String, Object> filters = new HashMap<>();
        filters.put("name", eventName); // Assuming your Event documents have a "name" field

        // Use the findAll method with the filters
        dbClient.findAll("events", filters, new DB_Client.DatabaseCallback<List<Event>>() {
            @Override
            public void onSuccess(List<Event> data) {
                if (data != null && !data.isEmpty()) {
                    // Update the RecyclerView with the list of events
                    eventsAdapter.setEvents(data);
                    eventsAdapter.notifyDataSetChanged();

                    // Build a string to display in the toast for testing
                    StringBuilder results = new StringBuilder("Results:\n");
                    for (Event event : data) {
                        results.append("Name: ").append(event.getName()).append("\n");
                    }

                    // Display the results in a toast
                    Toast.makeText(getContext(), results.toString(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "No events found with that name", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(getContext(), "Failed to load events: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }, Event.class);
    }
}