package com.example.napkinapp.fragments.adminmenu;

import android.content.Context;
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
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.fragments.admineventsearch.AdminListEventsFragment;

public class AdminNavagationFragment extends Fragment {

    public AdminNavagationFragment() {
        // Required empty constructor
    }

    private TitleUpdateListener titleUpdateListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof TitleUpdateListener){
            titleUpdateListener = (TitleUpdateListener) context;
        }else{
            throw new RuntimeException(context + " needs to implement TitleUpdateListener");
        }
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

        // Update header title
        titleUpdateListener.updateTitle("Admin Navigation");

        // Set up click listeners for each button
        userSearchButton.setOnClickListener(v -> {
            Log.d("AdminNavagationFragment", "User Search button clicked");
        });

        eventSearchButton.setOnClickListener(v -> {
            Log.d("AdminNavagationFragment", "Event Search button clicked");
            // Begin the transaction
            Fragment currFrag = requireActivity().getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

            if (!(currFrag instanceof AdminListEventsFragment)) {
                // Switch to SearchEventFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_fragmentcontainer, new AdminListEventsFragment())
                        .addToBackStack(null)
                        .commit();
            }
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