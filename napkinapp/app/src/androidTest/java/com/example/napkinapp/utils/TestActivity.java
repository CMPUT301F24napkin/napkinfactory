package com.example.napkinapp.utils;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.fragments.FooterFragment;
import com.example.napkinapp.fragments.HeaderFragment;
import com.example.napkinapp.fragments.adminmenu.AdminNavigationFragment;
import com.example.napkinapp.fragments.listevents.ListEventsFragment;
import com.example.napkinapp.fragments.myevents.MyEventsFragment;
import com.example.napkinapp.fragments.notifications.ListNotificationsFragment;
import com.example.napkinapp.fragments.qrscanner.QRScannerFragment;
import com.example.napkinapp.fragments.registeredevents.RegisteredEventsFragment;
import com.example.napkinapp.models.User;

public class TestActivity extends AppCompatActivity implements TitleUpdateListener, HeaderFragment.OnHeaderButtonClick,
        FooterFragment.FooterNavigationListener {

    private HeaderFragment header;
    private FooterFragment footer;

    public static User user;
    public static String userID;

    @Override
    public void updateTitle(String title) {
        if(header != null){
            header.setHeaderTitle(title);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Reuse the same layout

        header = new HeaderFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.header_fragmentcontainer, header)
                .commit();
        // To properly update footer buttons on back button press
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

            // Update the selected button in footer based on the current fragment
            if (currentFragment instanceof ListEventsFragment) {
                footer.setSelectedButtonById(0);
            } else if (currentFragment instanceof RegisteredEventsFragment) {
                footer.setSelectedButtonById(1);
            } /*else if (currentFragment instanceof MapFragment) {
                footer.setSelectedButtonById(2);
            }*/ else if (currentFragment instanceof QRScannerFragment) {
                footer.setSelectedButtonById(3);
            }
            else if (currentFragment instanceof MyEventsFragment) {
                footer.setSelectedButtonById(4);
            }
        });

        // Load footer fragment
        footer = new FooterFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.footer_fragmentcontainer, footer)
                .commit();
    }

    @Override
    public void handleFooterButtonClick(int btnid) {
        // add functionality to get a user from the database or instantiate if this is the first log-on
        Fragment selectedFragment = null;
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);
        switch(btnid) {
            // Not using actual button id's as apparently they're non final
            case 0:
                selectedFragment = new ListEventsFragment(user);
                break;
            case 1:
                selectedFragment = new RegisteredEventsFragment(user);
                break;
            case 2:
                // map
                break;
            case 3:
                // QRscanner
                selectedFragment = new QRScannerFragment(user);
                break;
            case 4:
                // Myevents
                selectedFragment = new MyEventsFragment(user);
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
    public void handleNotificationButtonClick() {
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

        if(currFrag instanceof ListNotificationsFragment){
            // do nothing if notifications already opened
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragmentcontainer, new ListNotificationsFragment(user))
                .addToBackStack(null)
                .commit();

        footer.resetButtons();
    }

    @Override
    public void handleHamburgerButtonClick() {
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

        if(currFrag instanceof AdminNavigationFragment){
            // do nothing if already in admin screen
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragmentcontainer, new AdminNavigationFragment(user))
                .addToBackStack(null)
                .commit();

        footer.resetButtons();
    }
}
