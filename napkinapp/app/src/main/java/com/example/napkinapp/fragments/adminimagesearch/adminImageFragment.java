package com.example.napkinapp.fragments.adminimagesearch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.napkinapp.R;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Facility;
import com.example.napkinapp.models.Image;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;

public class adminImageFragment extends Fragment implements ImageAdapter.OnButtonClickListener {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imageUrls;
    private List<String> imageUrlType;
    private List<String> urlCollection;
    private List<String> imageIdType;
    private List<String> imageID;
    private DB_Client db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_list_images, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        imageUrlType = new ArrayList<>();
        imageUrls = new ArrayList<>();
        urlCollection = new ArrayList<>();
        imageIdType = new ArrayList<>();
        imageID = new ArrayList<>();

        imageAdapter = new ImageAdapter(imageUrls, this); // Pass the listener here

        recyclerView.setAdapter(imageAdapter);

        db = new DB_Client(); // Initialize your DB_Client
        fetchImagesFromStorage();

        return view;
    }

    // function for deleting item


    private void fetchImagesFromStorage() {
        Map<String, Object> filters = new HashMap<>(); // No filters
        // event images
        db.findAll("Events", filters, new DB_Client.DatabaseCallback<List<Event>>() {
            @Override
            public void onSuccess(@Nullable List<Event> events) {
                if (events != null) {
                    for (Event event : events) {
                        Log.d("FindAllImages", "Event Images: ");
                        // Assuming Image class has a method getUrl() to fetch the image URL
                        if (event.getEventImageUri() != null) {
                            imageUrlType.add("eventImageUri");
                            imageUrls.add(event.getEventImageUri());
                            urlCollection.add("Events");
                            imageIdType.add("id");
                            imageID.add(event.getId());
                        }
                    }
                    imageAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
                } else {
                    Log.d("FindAllImages", "No images found.");
                }
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("FindAllImages", "Error fetching images", e);
            }
        }, Event.class);

        // user images
        db.findAll("Users", filters, new DB_Client.DatabaseCallback<List<User>>() {
            @Override
            public void onSuccess(@Nullable List<User> users) {
                if (users != null) {
                    for (User user : users) {
                        Log.d("FindAllImages", "Event Images: ");
                        // Assuming Image class has a method getUrl() to fetch the image URL
                        if (user.getProfileImageUri() != null) {
                            imageUrlType.add("profileImageUri");
                            imageUrls.add(user.getProfileImageUri());
                            urlCollection.add("Users");
                            imageIdType.add("androidId");
                            imageID.add(user.getAndroidId());
                        }
                    }
                    imageAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
                } else {
                    Log.d("FindAllImages", "No images found.");
                }
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("FindAllImages", "Error fetching images", e);
            }
        }, User.class);

        // facilities images
        db.findAll("Facilities", filters, new DB_Client.DatabaseCallback<List<Facility>>() {
            @Override
            public void onSuccess(@Nullable List<Facility> facilities) {
                if (facilities != null) {
                    for (Facility facility : facilities) {
                        Log.d("FindAllImages", "Event Images: ");
                        // Assuming Image class has a method getUrl() to fetch the image URL
                        if (facility.getImageUri() != null) {
                            imageUrlType.add("imageUri");
                            imageUrls.add(facility.getImageUri());
                            urlCollection.add("Facilities");
                            imageIdType.add("id");
                            imageID.add(facility.getId());
                        }
                    }
                    imageAdapter.notifyDataSetChanged(); // Refresh the RecyclerView
                } else {
                    Log.d("FindAllImages", "No images found.");
                }
            }
            @Override
            public void onFailure(Exception e) {
                Log.e("FindAllImages", "Error fetching images", e);
            }
        }, Facility.class);


    }
    @Override
    public void onButtonClick(int position) {
        // Log the clicked position
        Log.d("ButtonClicked", "Button clicked at position: " + position);

        // Log the values for debugging
        Log.d("delete", imageUrls.get(position));
        Log.d("delete", urlCollection.get(position));
        Log.d("delete", imageID.get(position));

        // Create filters for DB update
        Map<String, Object> filters = new HashMap<>();
        filters.put(imageIdType.get(position), imageID.get(position));

        // Prepare the update to set the image URL to null
        Map<String, Object> updates = new HashMap<>();
        updates.put(imageUrlType.get(position), null);

        // Perform the DB update
        db.updateAll(urlCollection.get(position), filters, updates, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                // Log success
                Log.d("UpdateEvent", "Image URL removed from event.");

                // Remove the item from the lists and notify the adapter
                imageUrlType.remove(position);
                imageUrls.remove(position);
                urlCollection.remove(position);
                imageIdType.remove(position);
                imageID.remove(position);

                // Notify adapter about item removal at the correct position
                imageAdapter.notifyItemRemoved(position);

                imageAdapter.notifyDataSetChanged();
                // Adjust the adapter's position if necessary (if multiple items are removed)
                // imageAdapter.notifyDataSetChanged(); // Optionally use this if you need a full refresh
            }

            @Override
            public void onFailure(Exception e) {
                // Log failure
                Log.e("UpdateEvent", "Error removing image URL from event", e);
            }
        });
    }

}
