/**
 * Fragment for the Admin search events page.
 * Current issue is that it does a shallow delete of the event and should delete references to thsi event too.
 */

package com.example.napkinapp.fragments.adminusersearch;

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

public class AdminListUsersFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;
    private ArrayList<User> users;
    private AdminUserArrayAdapter userArrayAdapter;
    private DB_Client db;
    public AdminListUsersFragment() {
        // Required null constructor
    }

    AdminUserArrayAdapter.UserListCustomizer customizer = button -> {
        button.setText("Remove");
        button.setOnClickListener(v -> {
            User user = (User) v.getTag();
            Log.i("Button", String.format("List Events: Clicked on event %s\n", user.getName()));


            // delete the user from the event waitlist


            Toast.makeText(mContext, "Delete User", Toast.LENGTH_SHORT).show();
            deleteUser(user);
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
        View view = inflater.inflate(R.layout.admin_user_search, container, false);
        ListView eventsListView = view.findViewById(R.id.user_list_view);
        EditText searchUserName = view.findViewById(R.id.search_user_name);
        Button searchButton = view.findViewById(R.id.search_button);
        db = new DB_Client();

        users = new ArrayList<>();
        userArrayAdapter = new AdminUserArrayAdapter(mContext, users, customizer);
        eventsListView.setAdapter(userArrayAdapter);

        // Update title
        titleUpdateListener.updateTitle("Users List");

        // Load all events initially
        loadUsers("");

        // Set up search button click listener
        searchButton.setOnClickListener(v -> {
            String query = searchUserName.getText().toString().trim();

            loadUsers(query);
            Toast.makeText(mContext, "search Event", Toast.LENGTH_SHORT).show();
            //searchEventsByName(query);
        });

        return view;
    }

    private void loadUsers(String userName) {
        // Create a query with a "like" match on event names
        List<Function<Query, Query>> conditions = List.of(
                query -> query.whereGreaterThanOrEqualTo("name", userName),
                query -> query.whereLessThanOrEqualTo("name", userName + "\uf8ff")
        );

        // Execute the query using the modified executeQuery method
        db.executeQueryList("Users", conditions, new DB_Client.DatabaseCallback<List<User>>() {
            @Override
            public void onSuccess(List<User> data) {
                Log.d("data", data != null ? data.toString() : "No data returned");
                users.clear();
                if (data != null && !data.isEmpty()) {
                    users.addAll(data);
                    userArrayAdapter.notifyDataSetChanged();
                    Log.d("RegisteredEventsFragment", "Event list loaded with " + users.size() + " items.");
                } else {
                    Log.d("RegisteredEventsFragment", "No events found matching the name.");
                    userArrayAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("RegisteredEventsFragment", "Error loading events: " + e.getMessage(), e);
            }
        }, User.class);

    }



    private void deleteUser(User user) {
        if (user.getAndroidId() == null || user.getAndroidId().isEmpty()) {
            Log.e("RegisteredEventsFragment", "Event ID is null or empty. Cannot delete event.");
            return;
        }
        //remove from events waitlists
        String userID = user.getAndroidId();
        ArrayList<String> eventIds = user.getWaitlist();

        for (String eventId : eventIds) {
            Map<String, Object> filters = new HashMap<>();
            filters.put("id", eventId); // Assuming the event ID is stored as 'id'
            Log.d("RegisteredEventsFragment", "Event ID: " + eventId);

            db.findOne("Events", filters, new DB_Client.DatabaseCallback<Event>() {
                @Override
                public void onSuccess(@Nullable Event event) {
                    if (event == null) {
                        Log.e("RegisteredEventsFragment", "Event not found for ID: " + eventId);
                        return;
                    }
                    List<String> waitlist = (List<String>) event.getWaitlist();
                    List<String> registered = (List<String>) event.getRegistered();
                    // adjust the waitlist
                    waitlist.remove(userID);
                    registered.remove(userID);

                    Map<String, Object> updates = new HashMap<>();
                    updates.put("waitlist", waitlist);
                    updates.put("registered", registered);
                    db.updateAll("Events", filters, updates, new DB_Client.DatabaseCallback<Void>() {});

                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(mContext, "Failed to get event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("RegisteredEventsFragment", "Error getting evnet", e);
                }
            }, Event.class);
        }

        // remove events which the deleted user has created
        Map<String, Object> deleteFilters = new HashMap<>();
        deleteFilters.put("organizerId", user.getAndroidId());

        db.deleteAll("Events", deleteFilters, new DB_Client.DatabaseCallback<Void>() {});

        // Set up the filter to find the user by its ID
        Map<String, Object> filters = new HashMap<>();
        filters.put("androidId", user.getAndroidId());

        // Call DB_Client's deleteOne method

        db.deleteOne("Users", filters, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "User deleted successfully", Toast.LENGTH_SHORT).show();
                users.remove(user); // Remove the event from the local list
                userArrayAdapter.notifyDataSetChanged(); // Update the adapter
                Log.d("RegisteredEventsFragment", "User deleted from Firestore and list updated.");
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(mContext, "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RegisteredEventsFragment", "Error deleting user", e);
            }
        });
    }
}