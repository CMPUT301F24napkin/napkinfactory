package com.example.napkinapp.fragments.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

        CheckBox isReadCheckBox = view.findViewById(R.id.isReadCheckBox);
        isReadCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            notification.setRead(isChecked);
            if (context instanceof MainActivity) {
                ((MainActivity) context).updateHeaderNotificationIcon();
            }
            notifyDataSetChanged();
        });

        ImageButton deleteButton = view.findViewById(R.id.deleteButton);
        deleteButton.setOnClickListener(v -> {
            notifications.remove(position);
            notifyDataSetChanged();
        });

        titleTextView.setText(notification.getTitle());
        messageTextView.setText(notification.getMessage());
        isReadCheckBox.setChecked(notification.getRead());

        return view;
    }
}
