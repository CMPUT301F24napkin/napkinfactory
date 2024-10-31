package com.example.napkinapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.napkinapp.fragments.FooterFragment;
import com.example.napkinapp.fragments.HeaderFragment;
import com.example.napkinapp.fragments.listevents.ListEventsFragment;
import com.example.napkinapp.fragments.profile.ProfileFragment;
import com.example.napkinapp.models.NapkinNotification;
import com.example.napkinapp.models.User;

public class MainActivity extends AppCompatActivity {

    public static final String CHANNEL_ID = "napkinapp_notifications";
    public static int numNotifications = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        createNotificationChannel();

        // Load header fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.header_fragmentcontainer, new HeaderFragment())
                .commit();

        // Load content fragment
        getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_fragmentcontainer, new ListEventsFragment())
                        .commit();

        // Load footer fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.footer_fragmentcontainer, new FooterFragment())
                .commit();

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}