package com.example.napkinapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.napkinapp.fragments.FooterFragment;
import com.example.napkinapp.fragments.HeaderFragment;
import com.example.napkinapp.fragments.createevent.CreateEventFragment;
import com.example.napkinapp.fragments.listevents.ListEventsFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.utils.DB_Client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
                        .replace(R.id.content_fragmentcontainer, new CreateEventFragment())
                        .commit();

        // Load footer fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.footer_fragmentcontainer, new FooterFragment())
                .commit();
    }
}