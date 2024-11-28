package com.example.napkinapp.utils;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;

public class TestActivity extends AppCompatActivity implements TitleUpdateListener {

    @Override
    public void updateTitle(String title) {
        // Mock implementation for testing
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Reuse the same layout
    }
}
