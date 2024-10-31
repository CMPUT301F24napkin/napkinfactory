package com.example.napkinapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.fragments.FooterFragment;
import com.example.napkinapp.fragments.HeaderFragment;
import com.example.napkinapp.fragments.adminmenu.AdminNavagationFragment;
import com.example.napkinapp.fragments.createevent.CreateEventFragment;
import com.example.napkinapp.fragments.listevents.ListEventsFragment;
import com.example.napkinapp.fragments.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity implements HeaderFragment.OnHeaderButtonClick, TitleUpdateListener {

    private HeaderFragment header;
    private FooterFragment footer;

    @Override
    public void updateTitle(String title) {
        if(header != null){
            header.setHeaderTitle(title);
        }
    }

    @Override
    public void handleProfileButtonClick() {
        Fragment currFrag = getSupportFragmentManager().findFragmentById(R.id.content_fragmentcontainer);

        if(currFrag instanceof ProfileFragment){
            // do nothing if profile already opened
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_fragmentcontainer, new ProfileFragment())
                .addToBackStack(null)
                .commit();
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

        // Load footer fragment
        footer = new FooterFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.footer_fragmentcontainer, footer)
                .commit();
    }


}