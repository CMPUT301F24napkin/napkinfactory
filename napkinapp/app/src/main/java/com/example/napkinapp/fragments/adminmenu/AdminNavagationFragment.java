package com.example.napkinapp.fragments.adminmenu;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;

public class AdminNavagationFragment extends Fragment {

    public AdminNavagationFragment() {
        // Required empty constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the new layout with buttons
        View view = inflater.inflate(R.layout.admin_navagation, container, false);

        // Initialize buttons
        Button userSearchButton = view.findViewById(R.id.button_user_search);
        Button eventSearchButton = view.findViewById(R.id.button_event_search);
        Button facilitiesMapButton = view.findViewById(R.id.button_facilities_map);
        Button browseImagesButton = view.findViewById(R.id.button_browse_images);

        // Set up click listeners for each button
        userSearchButton.setOnClickListener(v -> {
            Log.d("AdminNavagationFragment", "User Search button clicked");
        });

        eventSearchButton.setOnClickListener(v -> {
            Log.d("AdminNavagationFragment", "Event Search button clicked");
        });

        facilitiesMapButton.setOnClickListener(v -> {
            Log.d("AdminNavagationFragment", "Facilities Map button clicked");
        });

        browseImagesButton.setOnClickListener(v -> {
            Log.d("AdminNavagationFragment", "Browse Images button clicked");
        });

        Log.d("AdminNavagationFragment", "Navigation buttons initialized.");
        return view;
    }
}