package com.example.napkinapp.fragments.adminimagesearch;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.napkinapp.R;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Image;
import com.example.napkinapp.utils.DB_Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class adminImageFragment extends Fragment {

    private RecyclerView recyclerView;
    private ImageAdapter imageAdapter;
    private List<String> imageUrls;
    private DB_Client db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.admin_list_images, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        imageUrls = new ArrayList<>();
        imageAdapter = new ImageAdapter(imageUrls);
        recyclerView.setAdapter(imageAdapter);

        db = new DB_Client(); // Initialize your DB_Client
        fetchImagesFromStorage();

        return view;
    }

    private void fetchImagesFromStorage() {
        Map<String, Object> filters = new HashMap<>(); // No filters

        db.findAll("Events", filters, new DB_Client.DatabaseCallback<List<Event>>() {
            @Override
            public void onSuccess(@Nullable List<Event> events) {
                if (events != null) {
                    for (Event event : events) {
                        Log.d("FindAllImages", "Event Images: ");
                        // Assuming Image class has a method getUrl() to fetch the image URL
                        imageUrls.add(image.getUrl());
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
        }, Image.class);
    }
}
