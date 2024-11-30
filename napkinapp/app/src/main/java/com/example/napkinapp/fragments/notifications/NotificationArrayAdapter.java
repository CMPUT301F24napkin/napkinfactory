/**
 * Array adapter for a list of notifications.
 * it sets the button callbacks to toggle the 'read' status and the x button to delete the notification.
 */

package com.example.napkinapp.fragments.notifications;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.example.napkinapp.MainActivity;
import com.example.napkinapp.R;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.Notification;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;

import java.util.ArrayList;

public class NotificationArrayAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> notifications;
    private Context context;
    private User user;

    public NotificationArrayAdapter(@NonNull Context context, ArrayList<Notification> notifications, User user) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
        this.user = user;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;

        if(convertView == null){
            view = LayoutInflater.from(context).inflate(R.layout.notification_card, parent,false);
        }else {
            view = convertView;
        }

        Notification notification = notifications.get(position);

        TextView titleTextView = view.findViewById(R.id.titleTextView);
        TextView messageTextView = view.findViewById(R.id.messageTextView);

        ImageButton isReadButton = view.findViewById(R.id.readButton);
        toggleIcon(isReadButton, notification);

        isReadButton.setOnClickListener((buttonView) -> {
            notification.setRead(!notification.getRead());
            if (context instanceof MainActivity) {
                ((MainActivity) context).updateHeaderNotificationIcon();
            }
            toggleIcon(isReadButton, notification);
            DB_Client db = new DB_Client();

            db.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
                @Override
                public void onFailure(Exception e) {
                    Log.e("User update/creation", "Something went wrong updating user");
                }

                @Override
                public void onSuccess(@Nullable Void data) {
                    Log.i("User update/creation", "User updated/created");
                }
            });

            notifyDataSetChanged();
        });

        ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            notifications.remove(position);
            if (context instanceof MainActivity) {
                ((MainActivity) context).updateHeaderNotificationIcon();
            }

            DB_Client db = new DB_Client();

            db.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
                @Override
                public void onFailure(Exception e) {
                    Log.e("User update/creation", "Something went wrong updating user");
                }

                @Override
                public void onSuccess(@Nullable Void data) {
                    Log.i("User update/creation", "User updated/created");
                }
            });

            notifyDataSetChanged();
        });

        titleTextView.setText(notification.getTitle());
        messageTextView.setText(notification.getMessage());

        return view;
    }

    private void toggleIcon(ImageButton isReadButton, Notification notification){
        if (notification.getRead()){
            isReadButton.setImageResource(R.drawable.notification_bell_empty);
            isReadButton.setTag(R.drawable.notification_bell_empty);
        } else {
            isReadButton.setImageResource(R.drawable.notification_bell_active);
            isReadButton.setTag(R.drawable.notification_bell_active);
        }
    }
}
