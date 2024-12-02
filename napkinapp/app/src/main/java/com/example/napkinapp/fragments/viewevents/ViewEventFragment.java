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
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
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
import com.example.napkinapp.utils.ImageGenUtils;
import com.example.napkinapp.utils.Location_Utils;
import com.example.napkinapp.utils.QRCodeUtils;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewEventFragment extends Fragment {
    private TitleUpdateListener titleUpdateListener;
    private final Event event;
    private Button btnToggleWaitlist;
    private final User user;
    private Context mContext;

    public ViewEventFragment(Event event, User user){
        this.event = event;
        this.user = user;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;

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

        eventName.setText(event.getName());
        eventDate.setText(event.getEventDate().toString());
        eventDetails.setText(event.getDescription());
        if(event.getQrHashCode() != null) {
            qrBitmap.setImageBitmap(QRCodeUtils.generateQRCode(event.getQrHashCode(),150,150));
        } else {
            qrBitmap.setImageResource(R.mipmap.error);
        }

        if(event.getEventImageUri() != null) {
            try {
                Glide.with(view).load(Uri.parse(event.getEventImageUri())).into(eventImage);
                Log.i("Event", "Loaded event image url: " + event.getEventImageUri());
            }
            catch (Exception e){
                Log.e("Event", "failed to load event image: ", e);
            }
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
                organization.setText(data.getEmail());
                if(data.getProfileImageUri() != null) {
                    try {
                        if(data.getProfileImageUri() != null){
                            Glide.with(view).load(Uri.parse(data.getProfileImageUri())).into(organizerProfile);
                            Log.i("Profile", "Loaded organizer profile url: " + data.getProfileImageUri());
                        }
                    }
                    catch (Exception e){
                        Log.e("Profile", "failed to load profile image: ", e);
                    }
                }
                else {
                    Bitmap genProfile = ImageGenUtils.genProfleBitmap(data);
                    organizerProfile.setImageBitmap(genProfile);
                    Log.i("Profile", "profile image generated");
                }
            }
        }, User.class);




        btnToggleWaitlist = view.findViewById(R.id.toggle_waitlist);
        Button decline = view.findViewById(R.id.declineButton);
        Button cancel = view.findViewById(R.id.event_cancel);
        TextView actionCompleteTextView = view.findViewById(R.id.textViewDone);

        initButtons(event, btnToggleWaitlist, decline, actionCompleteTextView);



        cancel.setOnClickListener((v) -> {
            if (getActivity() != null) {
                // TODO: there a is a bug hitting cancel after it loads with a qr code
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });


        return view;
    }

    private void initButtons(Event event, Button accept, Button decline, TextView actionCompleteTextView){
        if (event.getCancelled().contains(user.getAndroidId())){
            accept.setVisibility(View.GONE);
            decline.setVisibility(View.GONE);
            actionCompleteTextView.setVisibility(View.VISIBLE);
            actionCompleteTextView.setText("You declined this event");
        } else if (event.getRegistered().contains(user.getAndroidId())){
            accept.setVisibility(View.GONE);
            decline.setVisibility(View.GONE);
            actionCompleteTextView.setVisibility(View.VISIBLE);
            actionCompleteTextView.setText("You accepted this event");
        } else if (event.getChosen().contains(user.getAndroidId())){
            AcceptDeclineView(event, accept, decline, actionCompleteTextView);
        } else {
            accept.setVisibility(View.VISIBLE);
            decline.setVisibility(View.GONE);
            actionCompleteTextView.setVisibility(View.GONE);
            accept.setOnClickListener((v) -> {
                // TODO: logic for applying to event
                handleToggleWaitlist();
            });

            updateButtons();
        }
    }


    private void AcceptDeclineView(Event event, Button accept, Button decline, TextView txt) {
        accept.setText("Accept");
        accept.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_check_24, 0, 0, 0);
        accept.setOnClickListener(v -> {
            registerUser(event);
            initButtons(event, accept, decline, txt);
        });

        decline.setText("Decline");
        decline.setCompoundDrawablesWithIntrinsicBounds(R.drawable.baseline_close_24, 0, 0, 0);
        decline.setOnClickListener(v -> {
            declineEvent(event);
            initButtons(event, accept, decline, txt);
        });
        txt.setVisibility(View.GONE);
    }

    /**
     * helper function to register the currently logged in user in an event. Does it deeply.
     * @param event the event to register in
     */
    public void registerUser(Event event) {
        // move this event from Chosen to Registered
        // add to this user's copy
        user.addEventToRegistered(event.getId());
        user.removeEventFromChosen(event.getId());

        event.addUserToRegistered(user.getAndroidId());
        event.removeUserFromChosen(user.getAndroidId());

        DB_Client db = new DB_Client();
        db.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                DB_Client.DatabaseCallback.super.onSuccess(data);
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(mContext, "Accepted " + event.getName() + "!", Toast.LENGTH_SHORT).show();
                });
                Log.i("Register User", "Successfully registered user for " + event.getName());
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Register User", "Something went wrong registering user " + user.getName() + " " + user.getAndroidId() + " for event " + event.getName());
            }
        });

        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                DB_Client.DatabaseCallback.super.onSuccess(data);
                Log.i("Register User - Event", "Successfully registered user for " + event.getName());
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Register User - Event", "Something went wrong registering user " + user.getName() + " " + user.getAndroidId() + " for event " + event.getName());
            }
        });
    }

    /**
     * Make the currently logged in user decline this event. Does it deeply.
     * Works by moving the currently logged in user's androidId out of the chosen list into the cancelled list.
     * @param event the event to decline
     */
    public void declineEvent(Event event) {
        user.removeEventFromChosen(event.getId());
        event.addUserToCancelled(user.getAndroidId());
        event.removeUserFromChosen(user.getAndroidId());

        DB_Client db = new DB_Client();
        db.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                DB_Client.DatabaseCallback.super.onSuccess(data);
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(mContext, "Declined " + event.getName() + "!", Toast.LENGTH_SHORT).show();
                });
                Log.i("Cancelling User", "Successfully cancelled user for " + event.getName());
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Cancelling User", "Something went wrong cancelling user " + user.getName() + " " + user.getAndroidId() + " for event " + event.getName());
            }
        });

        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                DB_Client.DatabaseCallback.super.onSuccess(data);
                Log.i("Cancel User - Event", "Successfully cancelled user for " + event.getName());
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
                Log.e("Cancel User - Event", "Something went wrong cancelling user " + user.getName() + " " + user.getAndroidId() + " for event " + event.getName());
            }
        });
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
            btnToggleWaitlist.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorRemoveDark)));
            btnToggleWaitlist.setSelected(true);
        }else{
            // Not
            btnToggleWaitlist.setText(R.string.add_to_waitlist);
            btnToggleWaitlist.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.neutralGray)));
            btnToggleWaitlist.setSelected(false);
        }
    }

    /**
     * Removes an this currently logged in user from the event's waitlist.
     */
    public void removeEventFromWaitlist(){
        event.removeUserFromWaitList(user.getAndroidId());
        user.removeEventFromWaitList(event.getId());

        DB_Client dbClient = new DB_Client();

        // Update event db
        dbClient.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Log.d("ViewEventFragment", "Removed user from event waitlist");
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
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(mContext, "Left waitlist for " + event.getName() + "!", Toast.LENGTH_SHORT).show();
                });
                Log.d("ViewEventFragment", "Removed event from user waitlist");
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
    public void addEventToWaitlist() {
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
                            Toast.makeText(mContext, "Joined waitlist for " + event.getName() + "!", Toast.LENGTH_SHORT).show();
                            Log.d("ViewEventFragment", "Added event from user waitlist");
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
                            Log.d("ViewEventFragment", "Added event from user waitlist");
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
                    getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Joined the waitlist for " + event.getName(), Toast.LENGTH_SHORT).show();
                    });
                    user.addEventToWaitlist(event.getId());
                    // Update person
                    db.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
                        @Override
                        public void onSuccess(@Nullable Void data) {
                            getActivity().runOnUiThread(() -> {
                                Log.d( "Adding Event to waitlist", "Added event to users waitlist! " + user.getName());
                            });
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
