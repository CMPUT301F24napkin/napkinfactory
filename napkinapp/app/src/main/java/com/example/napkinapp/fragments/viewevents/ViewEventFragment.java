package com.example.napkinapp.fragments.viewevents;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.QRCodeUtils;

import java.util.HashMap;

public class ViewEventFragment extends Fragment {
    private Event event;
    private TitleUpdateListener titleUpdateListener;

    public ViewEventFragment(Event event){
        this.event = event;
    };

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
        qrBitmap.setImageBitmap(QRCodeUtils.generateQRCode(event.getQRHashCode(),150,150));

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




        Button apply = view.findViewById(R.id.event_apply);
        Button cancel = view.findViewById(R.id.event_cancel);
        Button moreOptions = view.findViewById(R.id.more_options);

        apply.setOnClickListener((v) -> {
            // TODO: logic for applying to event
        });

        cancel.setOnClickListener((v) -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        moreOptions.setOnClickListener((v) -> {
           // TODO: Options selection
            // i image these will change based on if admin or not
        });

        return view;
    }


}
