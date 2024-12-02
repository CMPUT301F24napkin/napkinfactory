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
import com.example.napkinapp.fragments.admineventsearch.AdminListEventsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
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

    private void deleteEvent(Event event) {
        if (event.getId() == null || event.getId().isEmpty()) {
            Log.e("RegisteredEventsFragment", "Event ID is null or empty. Cannot delete event.");
            return;
        }

        // remove from users waitlists first
        String eventId = event.getId();
        LinkedHashSet<String> userIds = new LinkedHashSet<>(event.getWaitlist());
        userIds.addAll(event.getRegistered());
        userIds.addAll(event.getChosen());

        Log.d("AdminUserSearchFragment", "User Ids: " + userIds);

        for (String userID : userIds) {
            Map<String, Object> filters = new HashMap<>();
            filters.put("androidId", userID);
            Log.d("AdminUserSearchFragment", "User ID: " + userID);

            db.findOne("Users", filters, new DB_Client.DatabaseCallback<User>() {
                @Override
                public void onSuccess(@Nullable User user) {
                    if (user != null) {
                        Log.d("AdminUserSearchFragment", "eventID" + eventId);

                        List<String> waitlist = (List<String>) user.getWaitlist();
                        Log.d("AdminUserSearchFragment", "waitlist" + waitlist);
                        List<String> registered = (List<String>) user.getRegistered();
                        Log.d("AdminUserSearchFragment", "regisered" + registered);
                        List<String> chosen = (List<String>) user.getChosen();


                        // adjust the waitlist
                        if(waitlist.remove(eventId)) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("waitlist", waitlist);
                            db.updateAll("Users", filters, updates, new DB_Client.DatabaseCallback<Void>() {});
                        }
                        Log.d("AdminUserSearchFragment", "removed waitlist" + waitlist);
                        if(registered.remove(eventId)) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("registered", registered);
                            db.updateAll("Users", filters, updates, new DB_Client.DatabaseCallback<Void>() {});
                        }
                        Log.d("AdminUserSearchFragment", "removed regisered" + registered);
                        if(chosen.remove(eventId)) {
                            Map<String, Object> updates = new HashMap<>();
                            updates.put("chosen", chosen);
                            db.updateAll("Users", filters, updates, new DB_Client.DatabaseCallback<Void>() {});
                        }

                        /*
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("waitlist", waitlist);
                        updates.put("registered", registered);
                        updates.put("chosen", chosen);
                        Log.d("AdminUserSearchFragment", "updates" + updates);
                        */


                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(mContext, "Failed to get event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("AdminUserSearchFragment", "Error getting evnet", e);
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
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(mContext, "Failed to delete event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("RegisteredEventsFragment", "Error deleting event", e);
            }
        });
    }

    private void deleteUserFromCancelledListsANDORGANIZEDEVENTS(User user) {
        Map<String, Object> filters = new HashMap<>(); // No filters
        db.findAll("Events", filters, new DB_Client.DatabaseCallback<List<Event>>() {
            @Override
            public void onSuccess(@Nullable List<Event> events) {
                if (events != null) {
                    for (Event event : events) {
                        Log.d("FindAllEvents", "Event: " + event.toString());
                        ArrayList<String> cancelledList = event.getCancelled();

                        if (event.getOrganizerId().equals(user.getAndroidId())) {
                            deleteEvent(event);
                        }

                        else if (cancelledList.contains(user.getAndroidId())) {
                            cancelledList.remove(user.getAndroidId());

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("cancelled", cancelledList);

                            Map<String, Object> filters = new HashMap<>();
                            filters.put("id", event.getId());

                            Log.d("RegisteredEventsFragment", "updates" + updates);
                            db.updateAll("Events", filters, updates, new DB_Client.DatabaseCallback<Void>() {});
                        }
                    }
                } else {
                    Log.d("FindAllEvents", "No events found.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("FindAllEvents", "Error fetching events", e);
            }
        }, Event.class);
    }

    private void deleteUser(User user) {
        if (user.getAndroidId() == null || user.getAndroidId().isEmpty()) {
            Log.e("RegisteredEventsFragment", "Event ID is null or empty. Cannot delete event.");
            return;
        }
        //remove from events waitlists
        String userID = user.getAndroidId();
        ArrayList<String> eventIds = user.getWaitlist();
        eventIds.addAll(user.getRegistered());
        eventIds.addAll(user.getChosen());

        deleteUserFromCancelledListsANDORGANIZEDEVENTS(user);

        for (String eventId : eventIds) {
            Map<String, Object> filters = new HashMap<>();
            filters.put("id", eventId); // Assuming the event ID is stored as 'id'
            Log.d("RegisteredEventsFragment", "Event ID: " + eventId);

            db.findOne("Events", filters, new DB_Client.DatabaseCallback<Event>() {
                @Override
                public void onSuccess(@Nullable Event event) {
                    if (event != null) {

                        List<String> waitlist = (List<String>) event.getWaitlist();
                        Log.d("RegisteredEventsFragment", "waitlist" + waitlist);
                        List<String> registered = (List<String>) event.getRegistered();
                        Log.d("RegisteredEventsFragment", "regisered" + registered);
                        List<String> chosen = (List<String>) event.getChosen();
                        // adjust the waitlist
                        waitlist.remove(userID);
                        Log.d("RegisteredEventsFragment", "removed waitlist" + waitlist);
                        registered.remove(userID);
                        Log.d("RegisteredEventsFragment", "removed regisered" + registered);
                        chosen.remove(userID);

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("waitlist", waitlist);
                        updates.put("registered", registered);
                        updates.put("chosen", chosen);

                        Log.d("RegisteredEventsFragment", "updates" + updates);
                        db.updateAll("Events", filters, updates, new DB_Client.DatabaseCallback<Void>() {
                        });

                    }
                }
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(mContext, "Failed to get event: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("RegisteredEventsFragment", "Error getting event", e);
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