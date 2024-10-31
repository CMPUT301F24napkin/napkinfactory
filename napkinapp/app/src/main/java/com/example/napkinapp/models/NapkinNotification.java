package com.example.napkinapp.models;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.napkinapp.MainActivity;
import com.example.napkinapp.R;

public class NapkinNotification {
    private String title;
    private String message;

    public NapkinNotification(String title, String message) {
        this.title = title;
        this.message = message;
    }

    public void sendTo(User user, Context context) {
        if (user != null && user.getEnNotifications()) {
            // Create an Intent to launch the MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MainActivity.CHANNEL_ID)
                    .setSmallIcon(R.drawable.event)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            MainActivity.numNotifications ++;
            notificationManager.notify(MainActivity.numNotifications, builder.build());
        }
    }
}
