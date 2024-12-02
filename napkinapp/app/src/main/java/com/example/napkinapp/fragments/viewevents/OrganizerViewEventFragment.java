/**
 * View event screen for the organizers.
 */

package com.example.napkinapp.fragments.viewevents;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.fragments.EditTextPopupFragment;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Notification;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractMapFragment;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.ImageGenUtils;
import com.example.napkinapp.utils.ImageUtils;
import com.example.napkinapp.utils.QRCodeUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrganizerViewEventFragment extends AbstractMapFragment {
    private Context mContext;
    private Event event;
    private TitleUpdateListener titleUpdateListener;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    // this guy needs to be global so that the callback defined in onAttach can access it.
    ImageView eventImage;
    Uri eventImageURI;
    private ImageUtils imageUtils = new ImageUtils(ImageUtils.EVENT);

    public OrganizerViewEventFragment(Event event) {
        this.event = event;
    }

    public void sendNotification(@NonNull List<String> androidIds, Notification notification) {
        for (String androidId : androidIds) {
            DB_Client db = new DB_Client();
            HashMap<String, Object> filter = new HashMap<>();
            filter.put("androidId", androidId);
            db.findOne("Users", filter, new DB_Client.DatabaseCallback<User>() {
                @Override
                public void onSuccess(@Nullable User data) {
                    if(data != null) {
                        data.addNotification(notification);
                        db.writeData("Users", androidId, data, DB_Client.IGNORE);
                    }
                }
            }, User.class);
        }
    }

    /**
     * Choses a user-specified number of users from the waitlist to move into the chosen list.
     * Sends notifications to the chosen users telling them they were chosen
     * Sends notifications to the un-chosen users telling them they were not
     */
    public void doLottery() {

        ArrayList<String> shuffledWaitlist = new ArrayList<>(event.getWaitlist());
        Collections.shuffle(shuffledWaitlist);

        ArrayList<String> newlyChosen = new ArrayList<>(); // on first call, will be empty, otherwise, could have elements

        // minimum between size of waitlist and space left in chosen list.
        // FIX THIS SO THAT IT DOESN'T GRAB 0 DUDES
        // ALSO CHECK IF 0 DUDES AND BLOCK
        // DO SOME THING TO CHECK IF LOTTERY BEEN CALLED AND SOTP?
        int numUsersToMove = Math.min(shuffledWaitlist.size(), event.getEntrantLimit() - event.getChosen().size() - event.getRegistered().size());
        int i = numUsersToMove;
        Iterator<String> iterator = shuffledWaitlist.iterator();
        while (i > 0 && iterator.hasNext()) {
            String element = iterator.next();
            newlyChosen.add(element);
            iterator.remove();
            i--;
        }
        ArrayList<String> chosen = new ArrayList<>(event.getChosen());
        chosen.addAll(newlyChosen);

        event.setWaitlist(shuffledWaitlist);
        event.setChosen(chosen);

        DB_Client db = new DB_Client();

        // Update the event's waitlist and chosen list
        Map<String, Object> eventUpdates = Map.of(
                "waitlist", shuffledWaitlist,
                "chosen", chosen
        );

        db.updateAll("Events", Map.of("id", event.getId()), eventUpdates, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                Log.d("UPDATING EVENT LIST", "Event waitlist and chosen lists updated");

                // Update newly chosen users' waitlists and chosen lists
                for (String userId : newlyChosen) {
                    // Retrieve a newly chosen user
                    db.findOne("Users", Map.of("androidId", userId), new DB_Client.DatabaseCallback<User>() {
                        @Override
                        public void onSuccess(@Nullable User data) {
                            if(data == null){
                                Log.e("LOTTERY USER", "User does not exist " + userId);
                                return;
                            }

                            // Update newly chosen user's lists
                            data.removeEventFromWaitList(event.getId());
                            data.addEventToChosen(event.getId());

                            Map<String, Object> userListUpdates = Map.of(
                                    "waitlist", data.getWaitlist(),
                                    "chosen", data.getChosen()
                            );

                            // Push to database
                            db.updateAll("Users", Map.of("androidId", userId), userListUpdates, new DB_Client.DatabaseCallback<Void>() {
                                @Override
                                public void onSuccess(@Nullable Void data) {
                                    Log.d("LOTTERY USER", "User " + userId + " moved to chosen");
                                }

                                @Override
                                public void onFailure(Exception e) {
                                    Log.e("LOTTERY USER", "Error updating user " + userId + ": " + e.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("LOTTERY USER", "Error retrieving user " + userId + ": " + e.getMessage());
                        }
                    }, User.class);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UPDATING EVENT LIST", "Error updating event lists: " + e.getMessage());
            }
        });

        // notify everyone in still in waitlist (not chosen)
        sendNotification(shuffledWaitlist, new Notification(
                getText(R.string.notification_not_chosen_name) + event.getName(),
                getText(R.string.notification_not_chosen_description) + event.getName(), false, event.getId(), false));

        // notify everyone who was newly chosen
        sendNotification(newlyChosen, new Notification(
                getText(R.string.notification_chosen_name) + event.getName(),
                getText(R.string.notification_chosen_description) + event.getName(), false, event.getId(), false));

        getActivity().runOnUiThread(() -> {
            Toast.makeText(mContext, String.format(Locale.CANADA, "Sent notification to %d users", shuffledWaitlist.size() + newlyChosen.size()), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof TitleUpdateListener) {
            titleUpdateListener = (TitleUpdateListener) context;
        } else {
            throw new RuntimeException(context + " needs to implement TitleUpdateListener");
        }

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            if (uri != null) {
                eventImageURI = uri;
                eventImage.setImageURI(uri);
                Log.d("PhotoPicker", "Selected URI: " + uri);
            } else {
                Log.d("PhotoPicker", "No media selected");
            }
        });

        this.mContext = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        eventImageURI = result.getData().getData();
                        eventImage.setImageURI(eventImageURI);
                        DB_Client db = new DB_Client();

                        imageUtils.uploadImage(eventImageURI, event.getId())
                                .addOnSuccessListener(uri -> db.updateAll("Events", Map.of("id", event.getId()), Map.of("eventImageUri", uri.toString()), new DB_Client.DatabaseCallback<Void>() {}))
                                .addOnFailureListener(e -> {
                                    Log.e("UploadImage", "Failed to upload image: " + e.getMessage());
                                    Toast.makeText(getContext(), "Failed uploading image! Please try again!", Toast.LENGTH_SHORT).show();
                                });
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.organizer_view_event, container, false);

        DB_Client db = new DB_Client();

        // Update title
        titleUpdateListener.updateTitle("Event Details");

        TextView organizerName = view.findViewById(R.id.organizer_name);
        TextView organization = view.findViewById(R.id.organization);
        TextView eventName = view.findViewById(R.id.event_name);
        TextView eventDate = view.findViewById(R.id.event_date);
        TextView lotteryDate = view.findViewById(R.id.lottery_date);
        TextView eventDetails = view.findViewById(R.id.event_details);

        ImageView organizerProfile = view.findViewById(R.id.organizer_profile);

        eventImage = view.findViewById(R.id.event_image);
        if(event.getEventImageUri() != null) {
            try {
                Glide.with(view).load(Uri.parse(event.getEventImageUri())).into(eventImage);
                Log.i("Event", "Loaded event image url: " + event.getEventImageUri());
            }
            catch (Exception e){
                Log.e("Event", "failed to load event image: ", e);
            }
        }

        ImageView qrCode = view.findViewById(R.id.qr_code);

        Button editEventName = view.findViewById(R.id.edit_event_name);
        Button editEventImage = view.findViewById(R.id.edit_event_image);
        Button editEventDetails = view.findViewById(R.id.edit_event_details);
        Button editEventDate = view.findViewById(R.id.edit_event_date);
        Button editLotteryDate = view.findViewById(R.id.edit_lottery_date);
        Button shareQRCode = view.findViewById(R.id.share_qr_code);
        Button doLottery = view.findViewById(R.id.do_lottery);
        SwitchCompat requireGeolocation = view.findViewById(R.id.require_geolocation);

        MapView map = view.findViewById(R.id.map);

        Chip waitlistChip = view.findViewById(R.id.chip_waitlist);
        Chip chosenChip = view.findViewById(R.id.chip_chosen);
        Chip cancelledChip = view.findViewById(R.id.chip_cancelled);
        Chip registeredChip = view.findViewById(R.id.chip_registered);
        ChipGroup chipGroup = view.findViewById(R.id.chip_group);

        ListView entrantsListView = view.findViewById(R.id.entrants_list_view);
        TextInputLayout messageTextField = view.findViewById(R.id.message_text_field);
        Button sendMessage = view.findViewById(R.id.send_message);

        HashMap<String, Object> organizerFilter = new HashMap<>();
        organizerFilter.put("androidId", event.getOrganizerId());
        db.findOne("Users", organizerFilter, new DB_Client.DatabaseCallback<User>() {

            @Override
            public void onSuccess(@Nullable User data) {
                if (data != null) {
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

            }
        }, User.class);

        eventName.setText(event.getName());
        eventDetails.setText(event.getDescription());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        eventDate.setText(formatter.format(event.getEventDate()));
        lotteryDate.setText(formatter.format(event.getLotteryDate()));
        requireGeolocation.setChecked(event.isRequireGeolocation());

        editEventName.setOnClickListener(v -> {
            EditTextPopupFragment popup = new EditTextPopupFragment(
                    "Edit Event Name",
                    eventName.getText().toString(),
                    newName -> {
                        eventName.setText(newName);
                        updateEventName(newName);
                    }
            );
            popup.show(getActivity().getSupportFragmentManager(), "popup");
        });

        editEventImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        // Description
        editEventDetails.setOnClickListener(v -> {
            EditTextPopupFragment popup = new EditTextPopupFragment(
                    "Edit Event Details",
                    eventDetails.getText().toString(),
                    newDescription -> {
                        eventDetails.setText(newDescription);
                        updateEventDescription(newDescription);
                    }
            );
            popup.show(getActivity().getSupportFragmentManager(), "popup");
        });

        // Event Date
        editEventDate.setOnClickListener(v -> {
            DatePickerDialog popup = new DatePickerDialog(
                    mContext,
                    (view1, year, month, dayOfMonth) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        Date newDate = calendar.getTime();
                        eventDate.setText(formatter.format(newDate));
                        updateEventDate(newDate);
                    },
                    2024, 9, 11
            );
            popup.show();
        });

        // Lottery Date
        editLotteryDate.setOnClickListener(v -> {
            DatePickerDialog popup = new DatePickerDialog(
                    mContext,
                    (view1, year, month, dayOfMonth) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        Date newDate = calendar.getTime();
                        lotteryDate.setText(formatter.format(newDate));
                        updateLotteryDate(newDate);
                    },
                    2024, 9, 11
            );
            popup.show();
        });

        // call the member function.
        // its a function so that we can do unit tests on it!!!
        doLottery.setOnClickListener(v -> {

            // Get the InputMethodManager system service
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            // If the keyboard is open, hide it
            imm.hideSoftInputFromWindow(getActivity().findViewById(R.id.content_fragmentcontainer).getWindowToken(), 0);

            // do lottery
            doLottery();
        });

        if (event.getQrHashCode() != null) {
            qrCode.setImageBitmap(QRCodeUtils.generateQRCode(event.getQrHashCode(), 150, 150));
        } else {
            qrCode.setImageResource(R.mipmap.error);
        }

        shareQRCode.setOnClickListener(v -> {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
// Example: content://com.google.android.apps.photos.contentprovider/...
            shareIntent.putExtra(Intent.EXTRA_STREAM, eventImageURI);
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, null));
        });

        requireGeolocation.setOnCheckedChangeListener((buttonView, isChecked) ->
                updateRequireGeolocation(isChecked));


        // Do chips
        ArrayList<User> users = new ArrayList<>();
        UserArrayAdapter userArrayAdapter = new UserArrayAdapter(mContext, users);
        entrantsListView.setAdapter(userArrayAdapter);

        ChipGroup.OnCheckedStateChangeListener listener = ((group, checkedIds) -> {
            ArrayList<String> arrayList = new ArrayList<>();

            if (checkedIds.isEmpty()) {
                messageTextField.setHint("Message to all");
                arrayList.addAll(event.getWaitlist());
                arrayList.addAll(event.getChosen());
                arrayList.addAll(event.getCancelled());
                arrayList.addAll(event.getRegistered());
            } else if (checkedIds.size() == 1) {
                // get first selected chip, with app:singleSelection="true" set in XML size should be 1
                if (checkedIds.get(0) == R.id.chip_waitlist) {
                    messageTextField.setHint("Message to waitlisters");
                    arrayList.addAll(event.getWaitlist());
                } else if (checkedIds.get(0) == R.id.chip_chosen) {
                    messageTextField.setHint("Message to chosen");
                    arrayList.addAll(event.getChosen());
                } else if (checkedIds.get(0) == R.id.chip_cancelled) {
                    messageTextField.setHint("Message to cancelled");
                    arrayList.addAll(event.getCancelled());
                } else if (checkedIds.get(0) == R.id.chip_registered) {
                    messageTextField.setHint("Message to registered");
                    arrayList.addAll(event.getRegistered());
                } else {
                    Log.e("chipGroup.setOnCheckedStateChangeListener", String.format("unknown chip id %d", checkedIds.get(0)));
                }
            }

            // update db
            if (!arrayList.isEmpty()) {
                db.findAllIn("Users", "androidId", new ArrayList<Object>(arrayList), new DB_Client.DatabaseCallback<List<User>>() {
                    @Override
                    public void onSuccess(@Nullable List<User> data) {
                        users.clear();
                        users.addAll(data);
                        userArrayAdapter.notifyDataSetChanged();
                    }
                }, User.class);
            } else {
                users.clear();
                userArrayAdapter.notifyDataSetChanged();
            }
        });

        chipGroup.setOnCheckedStateChangeListener(listener); // cal the listener one time to initially populate the data
        listener.onCheckedChanged(chipGroup, new ArrayList<Integer>());

        sendMessage.setOnClickListener(v -> {
            if (!users.isEmpty()) {
                if (waitlistChip.isChecked()) {
                    sendNotification(event.getWaitlist(), new Notification(
                            getText(R.string.notification_custom_name).toString() + event.getName(),
                            messageTextField.getEditText().getText().toString(), false, event.getId(), false));

                } else if (chosenChip.isChecked()) {
                    sendNotification(event.getChosen(), new Notification(
                            getText(R.string.notification_custom_name).toString() + event.getName(),
                            messageTextField.getEditText().getText().toString(), false, event.getId(), false));

                } else if (cancelledChip.isChecked()) {
                    sendNotification(event.getCancelled(), new Notification(
                            getText(R.string.notification_custom_name).toString() + event.getName(),
                            messageTextField.getEditText().getText().toString(), false, event.getId(), false));

                } else if (registeredChip.isChecked()) {
                    sendNotification(event.getRegistered(), new Notification(
                            getText(R.string.notification_custom_name).toString() + event.getName(),
                            messageTextField.getEditText().getText().toString(), false, event.getId(), false));
                } else {
                    ArrayList<String> arrayList = new ArrayList<>(event.getWaitlist());
                    arrayList.addAll(event.getChosen());
                    arrayList.addAll(event.getRegistered());
                    arrayList.addAll(event.getCancelled());

                    sendNotification(arrayList, new Notification(
                            getText(R.string.notification_custom_name).toString(),
                            messageTextField.getEditText().getText().toString(), false, event.getId(), false));
                }
            } else {
                Toast.makeText(mContext, "No users to send message to!", Toast.LENGTH_SHORT).show();
            }
        });

        if(event.isRequireGeolocation()) {
            map.setTileSource(TileSourceFactory.MAPNIK);
            requestMapPermissions();
            map.setMultiTouchControls(true);
            IMapController mapController = map.getController();
            mapController.setZoom(15.0);
            GeoPoint startPoint = new GeoPoint(53.527309714453466, -113.52931950296305);
            mapController.setCenter(startPoint);
            map.setOnTouchListener((v, event) -> {
                // Request parent to not intercept touch events when MapView is touched
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;  // Let the MapView handle the touch event
            });

            // populate map
            for(ArrayList<Double> location : event.getEntrantLocations().values()) {
                if(location.size() < 2) {
                    Log.e("Map", "Event Entrant Locations coordinate has less than 2 elements! expecting 2, one for longitude and latitude.");
                }
                addMarker(map, getDefaultIcon(), location.get(0), location.get(1));
            }
        } else {
            map.setVisibility(View.GONE);
        }

        return view;
    }

    public void updateEventName(String newName) {
        DB_Client db = new DB_Client();
        event.setName(newName);
        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Log.i("UpdateEventName", "Event name updated successfully");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UpdateEventName", "Failed to update event name", e);
            }
        });
    }

    public void updateEventDescription(String newDescription) {
        DB_Client db = new DB_Client();
        event.setDescription(newDescription);
        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Log.i("UpdateEventDescription", "Event description updated successfully");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UpdateEventDescription", "Failed to update event description", e);
            }
        });
    }

    public void updateEventDate(Date newDate) {
        DB_Client db = new DB_Client();
        event.setEventDate(newDate);
        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Log.i("UpdateEventDate", "Event date updated successfully");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UpdateEventDate", "Failed to update event date", e);
            }
        });
    }

    public void updateLotteryDate(Date newDate) {
        DB_Client db = new DB_Client();
        event.setLotteryDate(newDate);
        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Log.i("UpdateLotteryDate", "Lottery date updated successfully");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UpdateLotteryDate", "Failed to update lottery date", e);
            }
        });
    }

    public void updateRequireGeolocation(boolean isChecked) {
        DB_Client db = new DB_Client();
        getActivity().runOnUiThread(() -> {
            Toast.makeText(mContext, "Require Geolocation " + (isChecked ? "Enabled" : "Disabled"), Toast.LENGTH_SHORT).show();
        });
        event.setRequireGeolocation(isChecked);
        db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(@Nullable Void data) {
                Log.i("UpdateGeolocation", "Require Geolocation updated successfully");
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UpdateGeolocation", "Failed to update Require Geolocation", e);
            }
        });
    }



}
