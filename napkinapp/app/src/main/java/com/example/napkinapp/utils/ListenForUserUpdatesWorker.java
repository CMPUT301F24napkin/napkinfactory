package com.example.napkinapp.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.napkinapp.MainActivity;
import com.example.napkinapp.models.Notification;
import com.example.napkinapp.models.User;

public class ListenForUserUpdatesWorker extends Worker {

    public ListenForUserUpdatesWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        listenForUserUpdates();
        return Result.success();
    }

    private void listenForUserUpdates() {
        DB_Client db = new DB_Client();
        String userID = MainActivity.user.getAndroidId();

        db.addDocumentSnapshotListener("Users", userID, new DB_Client.DatabaseCallback<User>() {
            @Override
            public void onSuccess(User updatedUser) {
                if (updatedUser != null) {
                    User currentUser = MainActivity.user;
                    if (currentUser.getNotifications().size() < updatedUser.getNotifications().size() &&
                            !currentUser.getNotifications().isEmpty()) {

                        Toast.makeText(getApplicationContext(), "New notification received", Toast.LENGTH_SHORT).show();

                        if (currentUser.getEnNotifications()) {
                            Notification latestNotification = updatedUser.getNotifications()
                                    .get(updatedUser.getNotifications().size() - 1);
                            MainActivity.sendPushNotification(
                                    getApplicationContext(),
                                    latestNotification.getTitle(),
                                    latestNotification.getMessage()
                            );
                        }
                    }
                    MainActivity.user = updatedUser;
                } else {
                    Log.w("User Update", "No changes detected or user data is null.");
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("User Listener", "Error listening for user updates: " + e.getMessage());
            }
        }, User.class);
    }
}

