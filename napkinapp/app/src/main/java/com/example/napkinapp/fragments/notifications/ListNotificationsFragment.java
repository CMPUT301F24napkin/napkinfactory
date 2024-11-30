/**
 * Fragment for the notifications list. This screen allows the user to view and delete notifications.
 */

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
import com.example.napkinapp.models.User;

import java.util.ArrayList;
import java.util.List;


public class ListNotificationsFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;
    private User user;

    public ListNotificationsFragment(User user){
        this.user = user;
    }

    public ListNotificationsFragment(){}

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
        // Retrieve notifications from the parent activity
        ArrayList<Notification> notifications = new ArrayList<>();
        notifications = user.getNotifications();


        // Attach NotificationArrayAdapter to ListView
        notificationArrayAdapter = new NotificationArrayAdapter(mContext, notifications, user);
        notifications_list.setAdapter(notificationArrayAdapter);

        return view;

    }
}
