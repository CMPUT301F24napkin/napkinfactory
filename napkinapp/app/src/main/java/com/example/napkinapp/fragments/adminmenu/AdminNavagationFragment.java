/**
 * Fragment for the Admin hamburger menu. Inflates a admin_navagation.
 */

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
import com.example.napkinapp.fragments.adminusersearch.AdminListUsersFragment;
import com.example.napkinapp.fragments.profile.ProfileFragment;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.User;
import com.example.napkinapp.fragments.admineventsearch.AdminListEventsFragment;

public class AdminNavagationFragment extends Fragment {

    private User user;

    public AdminNavagationFragment(User user) {
        this.user = user;
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

    /**
     * On creating the fragment, update the title, set on click listeners for all the search buttons.
     * Clicking a search button should open the fragment for that search function.
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.  The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given here.
     *
     * @return
     */
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

        Button editProfileButton = view.findViewById(R.id.editProfileButton);
        editProfileButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.blank_profile, 0, 0, 0);

        // Update header title
        titleUpdateListener.updateTitle("Admin Navigation");

        // Set up click listeners for each button
        userSearchButton.setOnClickListener(v -> {
            Log.d("AdminNavagationFragment", "User Search button clicked");

            Fragment currFrag = requireActivity().getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

            if (!(currFrag instanceof AdminListEventsFragment)) {
                // Switch to SearchEventFragment
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_fragmentcontainer, new AdminListUsersFragment())
                        .addToBackStack(null)
                        .commit();
            }
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

        editProfileButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_fragmentcontainer, new ProfileFragment(user))
                    .addToBackStack(null)
                    .commit();
        });

        Log.d("AdminNavagationFragment", "Navigation buttons initialized.");

        return view;
    }
}