/**
 * Fragment for the notifications list. This screen allows the user to view and delete notifications.
 */

package com.example.napkinapp.fragments.notifications;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.MainActivity;
import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Notification;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;

import java.util.ArrayList;
import java.util.Map;


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

        ArrayList<Notification> finalNotifications = notifications;
        notifications_list.setOnItemClickListener((parent, view1, position, id) -> {
            String EventId = finalNotifications.get(position).getEventId();
            Toast.makeText(mContext, "AAAA" + EventId, Toast.LENGTH_SHORT).show();
            DB_Client db = new DB_Client();
            db.findOne("Event", Map.of("id", EventId), new DB_Client.DatabaseCallback<Event>() {
                @Override
                public void onSuccess(@Nullable Event event) {
                    if (event == null) {
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.content_fragmentcontainer, new ViewEventFragment(event, user)) // Use your actual container ID
                                .addToBackStack(null) // Allows user to go back to ListEventsFragment
                                .commit();

                    } else {
                        Toast.makeText(mContext, "Cannot find event in Database!", Toast.LENGTH_SHORT).show();
                    }
                }


                @Override
                public void onFailure(Exception e) {
                    Log.e("User Initialization", "Something went wrong initializing user. EventId: " + EventId);
                }
            }, Event.class);
            Log.d("ListEventsFragment", "Clicked an event at position " + position);

        });
        return view;
    }
}
