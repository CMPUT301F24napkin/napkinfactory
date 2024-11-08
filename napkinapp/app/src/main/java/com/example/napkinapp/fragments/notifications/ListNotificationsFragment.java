package com.example.napkinapp.fragments.notifications;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.MainActivity;
import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.Notification;


public class ListNotificationsFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;

    public ListNotificationsFragment(){
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof TitleUpdateListener){
            titleUpdateListener = (TitleUpdateListener) context;
        }else{
            throw new RuntimeException(context + " needs to implement TitleUpdateListener");
        }

        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.notification_list, container, false);
        ListView notifications_list;
        NotificationArrayAdapter notificationArrayAdapter;

        notifications_list = view.findViewById(R.id.notifications_list_view);

        // Update title
        titleUpdateListener.updateTitle("Notifications");

        // Attach NotificationArrayAdapter to ListView
        notificationArrayAdapter = new NotificationArrayAdapter(mContext, MainActivity.user.getNotifications());
        notifications_list.setAdapter(notificationArrayAdapter);

        return view;

    }
}
