package com.example.napkinapp.fragments.adminfacilitysearch;

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
import com.example.napkinapp.fragments.admineventsearch.AdminEventArrayAdapter;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Facility;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AdminListFacilitiesFragment extends Fragment implements AdminFacilityArrayAdapter.FacilityClickListener {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;
    private ArrayList<Facility> facilities;
    private AdminFacilityArrayAdapter facilityArrayAdapter;
    private DB_Client db;
    public AdminListFacilitiesFragment() {
        // Required null constructor
    }



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

        facilities = new ArrayList<>();
        facilityArrayAdapter = new AdminFacilityArrayAdapter(mContext, facilities, this);//, customizer);
        eventsListView.setAdapter(facilityArrayAdapter);

        // Update title
        titleUpdateListener.updateTitle("Facility List");

        // Load all events initially
        loadFacilities("");

        // Set up search button click listener
        searchButton.setOnClickListener(v -> {
            String query = searchEventName.getText().toString().trim();
            loadFacilities(query);
        });

        return view;
    }

    public void loadFacilities(String facilityName) {
        // Create a query with a "like" match on event names
        List<Function<Query, Query>> conditions = List.of(
                query -> query.whereGreaterThanOrEqualTo("name", facilityName),
                query -> query.whereLessThanOrEqualTo("name", facilityName + "\uf8ff")
        );

        // Execute the query using the modified executeQuery method
        db.executeQueryList("Facilities", conditions, new DB_Client.DatabaseCallback<List<Facility>>() {
            @Override
            public void onSuccess(List<Facility> data) {
                Log.d("data", data != null ? data.toString() : "No data returned");
                facilities.clear();
                if (data != null && !data.isEmpty()) {
                    facilities.addAll(data);
                    facilityArrayAdapter.notifyDataSetChanged();
                    Log.d("RegisteredEventsFragment", "facility list loaded with " + facilities.size() + " items.");
                } else {
                    Log.d("RegisteredEventsFragment", "No events found matching the name.");
                    facilityArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("RegisteredEventsFragment", "Error loading facilities: " + e.getMessage(), e);
            }
        }, Facility.class);
    }
    @Override
    public void onDeleteButtonClick(Facility facility) {
        // Handle the deletion of the facility
        deleteFacility(facility);
        facilities.remove(facility);
        facilityArrayAdapter.notifyDataSetChanged();
    }

    private void deleteFacility(Facility facility) {
        HashMap<String, Object> filter = new HashMap<>();
        filter.put("id", facility.getId());
        db.findOne("Facilities", filter, new DB_Client.DatabaseCallback<Facility>() {
            @Override
            public void onSuccess(@Nullable Facility facility) {
                Log.d("AdminListFacilitiesFragment", "Facility found: " + facility.getName());
                if (facility == null) {
                    Log.e("AdminListFacilitiesFragment", "Facility not found or is null.");
                    return;
                }
                Log.d("AdminListFacilitiesFragment", "Facility found: " + facility.getName());
                // find the user who made the facility
                Map<String, Object> filters = new HashMap<>();
                filters.put("facility", facility.getId());
                Log.d("AdminListFacilitiesFragment", "filters " + filters);
                db.updateAll("Users", filters, Map.of("facility", ""), new DB_Client.DatabaseCallback<Void>() {});

                // delete the facility
                db.deleteOne("Facilities", filter, new DB_Client.DatabaseCallback<Void>() {});

            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(mContext, "Failed to get event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RegisteredEventsFragment", "Error getting evnet", e);
            }
        }, Facility.class);
    }
}
