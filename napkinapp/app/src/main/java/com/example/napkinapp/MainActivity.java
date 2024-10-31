package com.example.napkinapp;

import android.os.Bundle;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.example.napkinapp.fragments.FooterFragment;
import com.example.napkinapp.fragments.HeaderFragment;
import com.example.napkinapp.fragments.listevents.ListEventsFragment;
import com.example.napkinapp.fragments.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Load header fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.header_fragmentcontainer, new HeaderFragment())
                .commit();

        // Load content fragment
        getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_fragmentcontainer, new ProfileFragment())
                        .commit();

        // Load footer fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.footer_fragmentcontainer, new FooterFragment())
                .commit();
    }
}