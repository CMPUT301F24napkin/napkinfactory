/**
 * Fragment for viewing events as the user. This is used whenever the user views an event it is
 * not the organizer of.
 */

package com.example.napkinapp.fragments.viewevents;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.Location_Utils;
import com.example.napkinapp.utils.QRCodeUtils;

import java.util.ArrayList;
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
                if(data.getProfileImageUri() != null) {
                    try {
                        Glide.with(view).load(Uri.parse(data.getProfileImageUri())).into(organizerProfile);
                        Log.i("Profile", "Loaded organizer profile url: " + data.getProfileImageUri());
                    }
                    catch (Exception e){
                        Log.e("Profile", "failed to load profile image: ", e);
                    }
                }
            }
        }, User.class);




        btnToggleWaitlist = view.findViewById(R.id.toggle_waitlist);
        Button cancel = view.findViewById(R.id.event_cancel);

        btnToggleWaitlist.setOnClickListener((v) -> {
            // TODO: logic for applying to event
            handleToggleWaitlist();
        });

        updateButtons();

        cancel.setOnClickListener((v) -> {
            if (getActivity() != null) {
                // TODO: there a is a bug hitting cancel after it loads with a qr code
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

    /**
     * Add or remove the event from the waitlist based on the state of btnToggleWaitlist.
     */
    private void handleToggleWaitlist(){
        if(btnToggleWaitlist.isSelected()){
            // Remove from waitlist
            removeEventFromWaitlist();
        }else{
            // Add to waitlist
            addEventToWaitlist();
        }
        updateButtons();
    }

    /**
     * Update the text of the buttons based on whether the user is on the events waitlist or not.
     */
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

    /**
     * Removes an this currently logged in user from the event's waitlist.
     */
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

    /**
     * Adds this currently logged in user to the event's waitlist.
     */
    private void addEventToWaitlist() {
        DB_Client db = new DB_Client();
        event.addUserToWaitlist(user.getAndroidId());

        if (user.getEnLocation()) {
            Location_Utils locationUtils = new Location_Utils(this.getContext());
            locationUtils.getLastLocation(new Location_Utils.LocationCallbackInterface() {
                @Override
                public void onLocationRetrieved(Location location) {
                    // Handle location data
                    Log.d("ListEventsFragment", "Location: " + location.getLatitude() + ", " + location.getLongitude());
                    ArrayList<Double> coordinates = new ArrayList<>();
                    coordinates.add(location.getLatitude());
                    coordinates.add(location.getLongitude());
                    event.addEntrantLocation(user.getAndroidId(), coordinates);
                    // Update events
                    db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
                        @Override
                        public void onSuccess(@Nullable Void data) {
                            Toast.makeText(getContext(), "Added event to waitlist! " + event.getName(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            DB_Client.DatabaseCallback.super.onFailure(e);
                            Log.e("Adding Event to waitlist", "Something went wrong! " + e);
                        }
                    });
                    user.addEventToWaitlist(event.getId());
                    // Update person
                    db.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
                        @Override
                        public void onSuccess(@Nullable Void data) {
                            Toast.makeText(getContext(), "Added event to users waitlist! " + user.getName(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            DB_Client.DatabaseCallback.super.onFailure(e);
                            Log.e("Adding Event to waitlist", "Something went wrong! " + e);
                        }
                    });
                }

                @Override
                public void onPermissionDenied() {
                    // Handle permission denial
                    Log.e("ListEventsFragment", "Location permissions are denied!");
                    Toast.makeText(getContext(), "Location permissions are denied!", Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 102);
                }

                @Override
                public void onError(Exception e) {
                    // Handle errors
                    Log.e("ListEventsFragment", "Error retrieving location: " + e.getMessage());
                }
            });
        } else if (event.isRequireGeolocation()){
            showGeolocationPopup(user);
        } else {
            db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
                @Override
                public void onSuccess(@Nullable Void data) {
                    Toast.makeText(getContext(), "Added event to waitlist! " + event.getName(), Toast.LENGTH_SHORT).show();
                    user.addEventToWaitlist(event.getId());
                    // Update person
                    db.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
                        @Override
                        public void onSuccess(@Nullable Void data) {
                            Toast.makeText(getContext(), "Added event to users waitlist! " + user.getName(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            DB_Client.DatabaseCallback.super.onFailure(e);
                            Log.e("Adding Event to waitlist", "Something went wrong! " + e);
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    DB_Client.DatabaseCallback.super.onFailure(e);
                    Log.e("Adding Event to waitlist", "Something went wrong! " + e);
                }
            });
        }
    }

    public void showGeolocationPopup(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Geolocation Required")
                .setMessage("This event requires geolocation to proceed.")
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 102);
                        }

                        user.setEnLocation(true);
                        DB_Client db = new DB_Client();
                        db.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
                            @Override
                            public void onFailure(Exception e) {
                                Log.e("User update/creation", "Something went wrong updating user");
                                Toast.makeText(getContext(), "Error communication with database! Please try again!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onSuccess(@Nullable Void data) {
                                Log.i("User update/creation", "User updated/created");
                                Toast.makeText(getContext(), "Enabled Geolocation", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

}
