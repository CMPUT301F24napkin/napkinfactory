package com.example.napkinapp.fragments.viewevents;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.QRCodeUtils;

import java.util.HashMap;

public class ViewEventFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private final Event event;
    private Button btnToggleWaitlist;
    private final User user;

    public ViewEventFragment(Event event, User user){
        this.event = event;
        this.user = user;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof TitleUpdateListener){
            titleUpdateListener = (TitleUpdateListener) context;
        }else{
            throw new RuntimeException(context + " needs to implement TitleUpdateListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.view_events, container, false);
        DB_Client db = new DB_Client();

        // Update title
        titleUpdateListener.updateTitle("Event Details");

        TextView organizerName = view.findViewById(R.id.organizer_name);
        TextView organization = view.findViewById(R.id.organization);
        TextView eventName = view.findViewById(R.id.event_name);
        TextView eventDate = view.findViewById(R.id.event_date);
        TextView eventDetails = view.findViewById(R.id.event_details);

        ImageView eventImage = view.findViewById(R.id.event_image);
        ImageView organizerProfile = view.findViewById(R.id.organizer_profile);
        ImageView qrBitmap = view.findViewById(R.id.event_qr_code);

        // TODO: properly populate all data
        eventName.setText(event.getName());
        eventDate.setText(event.getEventDate().toString());
        eventDetails.setText(event.getDescription());
        if(event.getQrHashCode() != null) {
            qrBitmap.setImageBitmap(QRCodeUtils.generateQRCode(event.getQrHashCode(),150,150));
        } else {
            qrBitmap.setImageResource(R.mipmap.error);
        }

        // database queries
        HashMap<String,Object> filter = new HashMap<>();
        filter.put("androidId", event.getOrganizerId());
        db.findOne("Users", filter, new DB_Client.DatabaseCallback<User>() {

            @Override
            public void onSuccess(@Nullable User data) {
                if (data == null){
                    Log.e("Database Issue", "Organizer not found in Database for the specified event: " + filter);
                    return;
                }
                organizerName.setText(data.getName());
                organization.setText(data.getPhoneNumber());
            }
        }, User.class);




        btnToggleWaitlist = view.findViewById(R.id.toggle_waitlist);
        Button cancel = view.findViewById(R.id.event_cancel);
        Button moreOptions = view.findViewById(R.id.more_options);

        btnToggleWaitlist.setOnClickListener((v) -> {
            // TODO: logic for applying to event
            handleToggleWaitlist();
        });

        cancel.setOnClickListener((v) -> {
            if (getActivity() != null) {
                // TODO: there a is a bug hitting cancel after it loads with a qr code
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
        moreOptions.setOnClickListener((v) -> {
           // TODO: Options selection
            // i image these will change based on if admin or not
        });

        return view;
    }

    private void handleToggleWaitlist(){
        Toast.makeText(getContext(), "test " + btnToggleWaitlist.isSelected(), Toast.LENGTH_SHORT).show();
        if(btnToggleWaitlist.isSelected()){
            // Remove from waitlist
            removeEventFromWaitlist();
        }else{
            // Add to waitlist
            addEventToWaitlist();
        }
        updateButtons();
    }

    private void updateButtons() {
        if(event.getWaitlist().contains(user.getAndroidId())){
            // Waitlist
            btnToggleWaitlist.setText(R.string.remove_from_waitlist);
            btnToggleWaitlist.setSelected(true);
        }else{
            // Not
            btnToggleWaitlist.setText(R.string.add_to_waitlist);
            btnToggleWaitlist.setSelected(false);
        }
    }

    private void removeEventFromWaitlist(){
        event.removeUserFromWaitList(user.getAndroidId());
        user.removeEventFromWaitList(event.getId());

        DB_Client dbClient = new DB_Client();

        // Update event db
        dbClient.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(getContext(), "Removed user from event waitlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Remove Event Details", "Error removing user from event waitlist");
            }
        });

        // Update user db
        dbClient.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(getContext(), "Removed event from user waitlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Remove Event Details", "Error removing event from user waitlist");
            }
        });

    }

    private void addEventToWaitlist(){
        event.addUserToWaitlist(user.getAndroidId());
        user.addEventToWaitlist(event.getId());

        DB_Client dbClient = new DB_Client();

        // Update event db
        dbClient.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(getContext(), "Removed user from event waitlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Remove Event Details", "Error removing user from event waitlist");
            }
        });

        // Update user db
        dbClient.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Toast.makeText(getContext(), "Removed event from user waitlist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Remove Event Details", "Error removing event from user waitlist");
            }
        });
    }
}
