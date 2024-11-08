package com.example.napkinapp.fragments.notifications;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.napkinapp.MainActivity;
import com.example.napkinapp.R;
import com.example.napkinapp.models.Notification;

import java.util.ArrayList;

public class NotificationArrayAdapter extends ArrayAdapter<Notification> {
    private ArrayList<Notification> notifications;
    private Context context;

    public NotificationArrayAdapter(@NonNull Context context, ArrayList<Notification> notifications) {
        super(context, 0, notifications);
        this.context = context;
        this.notifications = notifications;
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
        if (notification.getRead()){
            isReadButton.setImageResource(R.drawable.notification_bell_empty);
        } else {
            isReadButton.setImageResource(R.drawable.notification_bell_active);
        }
        isReadButton.setOnClickListener((buttonView) -> {
            notification.setRead(!notification.getRead());
            if (context instanceof MainActivity) {
                ((MainActivity) context).updateHeaderNotificationIcon();
            }
            if (notification.getRead()){
                isReadButton.setImageResource(R.drawable.notification_bell_empty);
            } else {
                isReadButton.setImageResource(R.drawable.notification_bell_active);
            }
            notifyDataSetChanged();
        });

        ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            notifications.remove(position);
            if (context instanceof MainActivity) {
                ((MainActivity) context).updateHeaderNotificationIcon();
            }
            notifyDataSetChanged();
        });

        titleTextView.setText(notification.getTitle());
        messageTextView.setText(notification.getMessage());

        // Still need to update this method so that it can update this in the future
        view.setOnClickListener(v -> {
            Toast.makeText(context, "Will launch this: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}