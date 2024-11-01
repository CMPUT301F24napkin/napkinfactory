package com.example.napkinapp;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.fragments.FooterFragment;
import com.example.napkinapp.fragments.HeaderFragment;
import com.example.napkinapp.fragments.adminmenu.AdminNavagationFragment;
import com.example.napkinapp.fragments.listevents.ListEventsFragment;
import com.example.napkinapp.fragments.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity implements HeaderFragment.OnHeaderButtonClick,
        FooterFragment.FooterNavigationListener, TitleUpdateListener {

    private HeaderFragment header;
    private FooterFragment footer;

    @Override
    public void handleFooterButtonClick(int btnid) {
        Fragment selectedFragment = null;
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);
        switch(btnid) {
            // Not using actual button id's as apparently they're non final
            case 0:
                selectedFragment = new ListEventsFragment();
                break;
            case 1:
                // Registered events
                break;
            case 2:
                // map
                break;
            case 3:
                // QRscanner
                break;
            case 4:
                // Myevents
                break;
        }

        if(selectedFragment != null){
            if(currFrag != null && currFrag.getClass().equals(selectedFragment.getClass())){
                // Already on screen so do nothing
                return;
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_fragmentcontainer, selectedFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void updateTitle(String title) {
        if(header != null){
            header.setHeaderTitle(title);
        }
    }

    @Override
    public void handleProfileButtonClick() {
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);
        Log.d("handleProfileClick", String.valueOf(currFrag instanceof ProfileFragment));
        if(currFrag instanceof ProfileFragment){
            // do nothing if profile already opened
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragmentcontainer, new ProfileFragment())
                .addToBackStack(null)
                .commit();

        footer.resetButtons();
    }

    @Override
    public void handleHamburgerButtonClick() {
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

        if(currFrag instanceof AdminNavagationFragment){
            // do nothing if already in admin screen
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragmentcontainer, new AdminNavagationFragment())
                .addToBackStack(null)
                .commit();

        footer.resetButtons();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Load header fragment
        header = new HeaderFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.header_fragmentcontainer, header)
                .commit();

        // Load content fragment
        getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_fragmentcontainer, new ListEventsFragment())
                        .commit();

        // To properly update footer buttons on back button press
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

            // Update the selected button in footer based on the current fragment
            if (currentFragment instanceof ListEventsFragment) {
                footer.setSelectedButtonById(0);
            } /*else if (currentFragment instanceof RegisteredEventsFragment) {
                footer.setSelectedButtonById(1);
            } else if (currentFragment instanceof MapFragment) {
                footer.setSelectedButtonById(2);
            } else if (currentFragment instanceof QRScannerFragment) {
                footer.setSelectedButtonById(3);
            } else if (currentFragment instanceof MyEventsFragment) {
                footer.setSelectedButtonById(4);
            }*/
        });

        // Load footer fragment
        footer = new FooterFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.footer_fragmentcontainer, footer)
                .commit();
    }


}