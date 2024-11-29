/**
 * View event screen for the organizers.
 */

package com.example.napkinapp.fragments.viewevents;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
import androidx.fragment.app.Fragment;

import com.example.napkinapp.fragments.EditTextPopupFragment;
import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.Notification;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.QRCodeUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FieldValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OrganizerViewEventFragment extends Fragment {
    private Context mContext;
    private Event event;
    private TitleUpdateListener titleUpdateListener;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    // this guy needs to be global so that the callback defined in onAttach can access it.
    ImageView eventImage;
    Uri eventImageURI;

    public OrganizerViewEventFragment(Event event) {
        this.event = event;
    }

    ;

    private void sendNotification(List<String> androidIds, Notification notification) {
        for (String androidId : androidIds) {
            DB_Client db = new DB_Client();
            HashMap<String, Object> filter = new HashMap<>();
            filter.put("androidId", androidId);
            db.findOne("Users", filter, new DB_Client.DatabaseCallback<User>() {
                @Override
                public void onSuccess(@Nullable User data) {
                    data.addNotification(notification);
                    db.writeData("Users", androidId, data, DB_Client.IGNORE);
                }
            }, User.class);
        }
        // Get the InputMethodManager system service
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        // If the keyboard is open, hide it
        imm.hideSoftInputFromWindow(getActivity().findViewById(R.id.content_fragmentcontainer).getWindowToken(), 0);
        Toast.makeText(mContext, String.format("Sent notification to %d users!", androidIds.size()), Toast.LENGTH_SHORT).show();
    }

    /**
     * Choses a user-specified number of users from the waitlist to move into the chosen list.
     * Sends notifications to the chosen users telling them they were chosen
     * Sends notifications to the un-chosen users telling them they were not
     */
    private void doLottery() {

        ArrayList<String> waitlistCopy = new ArrayList<>(event.getWaitlist());
        ArrayList<String> chosenListCopy = new ArrayList<>(event.getChosen()); // empty

        Collections.shuffle(waitlistCopy);

        // minimum between size of waitlist and space left in chosen list.
        int numUsersToMove = Math.min(waitlistCopy.size(), event.getParticipantLimit() - chosenListCopy.size());
        int i = numUsersToMove;
        Iterator<String> iterator = waitlistCopy.iterator();
        while (i > 0 && iterator.hasNext()) {
            String element = iterator.next();
            chosenListCopy.add(element);
            iterator.remove();
            i--;
        }
        event.setWaitlist(waitlistCopy);
        event.setChosen(chosenListCopy);

        DB_Client db = new DB_Client();

// Update the event's waitlist and chosen list
        Map<String, Object> eventUpdates = Map.of(
                "waitlist", waitlistCopy,
                "chosen", chosenListCopy
        );

        db.writeData("Events", event.getId(), eventUpdates, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onSuccess(Void data) {
                Log.d("UPDATING EVENT LIST", "Event waitlist and chosen lists updated");

                // Update users in the chosen list
                for (String userId : chosenListCopy) {
                    Map<String, Object> userUpdates = Map.of(
                            "chosen", FieldValue.arrayUnion(event.getId()),
                            "waitlist", FieldValue.arrayRemove(event.getId())
                    );

                    db.writeData("Users", userId, userUpdates, new DB_Client.DatabaseCallback<Void>() {
                        @Override
                        public void onSuccess(Void data) {
                            Log.d("LOTTERY USER", "User " + userId + " moved to chosen");
                        }

                        @Override
                        public void onFailure(Exception e) {
                            Log.e("LOTTERY USER", "Error updating user " + userId + ": " + e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.e("UPDATING EVENT LIST", "Error updating event lists: " + e.getMessage());
            }
        });

        // notify everyone in waitlist. notify everyone in chosenListCopy.
        sendNotification(waitlistCopy, new Notification(
                getText(R.string.notification_not_chosen_name).toString(),
                getText(R.string.notification_not_chosen_description).toString(), false, event.getId(), false));

        sendNotification(chosenListCopy, new Notification(
                getText(R.string.notification_chosen_name).toString(),
                getText(R.string.notification_chosen_description).toString(), false, event.getId(), false));


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
        ImageView qrCode = view.findViewById(R.id.qr_code);

        Button editEventName = view.findViewById(R.id.edit_event_name);
        Button editEventImage = view.findViewById(R.id.edit_event_image);
        Button editEventDetails = view.findViewById(R.id.edit_event_details);
        Button editEventDate = view.findViewById(R.id.edit_event_date);
        Button editLotteryDate = view.findViewById(R.id.edit_lottery_date);
        Button shareQRCode = view.findViewById(R.id.share_qr_code);
        SwitchCompat requireGeolocation = view.findViewById(R.id.require_geolocation);
        Button doLottery = view.findViewById(R.id.do_lottery);

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
                    organization.setText(data.getPhoneNumber());
                } else {
                    organizerName.setText("Unknown Organizer");
                    organization.setText("Unknown Organization");
                }

            }
        }, User.class);

        eventName.setText(event.getName());
        eventDetails.setText(event.getDescription());

        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        eventDate.setText(formatter.format(event.getEventDate()));
        lotteryDate.setText(formatter.format(event.getLotteryDate()));

        editEventName.setOnClickListener(v -> {
            EditTextPopupFragment popup = new EditTextPopupFragment("Edit Event Name", eventName.getText().toString(), text -> {
                event.setName(text);
                eventName.setText(text);
                db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
                });
            });
            popup.show(getActivity().getSupportFragmentManager(), "popup");
        });

        editEventImage.setOnClickListener(v -> {
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // Description
        editEventDetails.setOnClickListener(v -> {
            EditTextPopupFragment popup = new EditTextPopupFragment("Edit Event Details", eventDetails.getText().toString(), text -> {
                event.setDescription(text);
                eventDetails.setText(text);
                db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
                });
            });
            popup.show(getActivity().getSupportFragmentManager(), "popup");
        });

        // Event Date
        editEventDate.setOnClickListener(v -> {
            DatePickerDialog popup = new DatePickerDialog(mContext, (view1, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, dayOfMonth); // Month is 0-based
                Date date = calendar.getTime();
                eventDetails.setText(formatter.format(date));

                // update db
                event.setEventDate(date);
                db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
                });
            }, 2024, 9, 11);
            popup.show();
        });

        // Lottery Date
        editLotteryDate.setOnClickListener(v -> {
            DatePickerDialog popup = new DatePickerDialog(mContext, (view1, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, dayOfMonth); // Month is 0-based
                Date date = calendar.getTime();
                lotteryDate.setText(formatter.format(date));
                // update db
                event.setLotteryDate(date);
                db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
                });
            }, 2024, 9, 11);
            popup.show();
        });

        // call the member function.
        // its a function so that we can do unit tests on it!!!
        doLottery.setOnClickListener(v -> {
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

        requireGeolocation.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Toast.makeText(mContext, String.format("set Require Geolocation to %b", isChecked), Toast.LENGTH_SHORT).show();
            // update db
            event.setRequireGeolocation(isChecked);
            db.writeData("Events", event.getId(), event, new DB_Client.DatabaseCallback<Void>() {
            });
        });

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
                            getText(R.string.notification_custom_name).toString(),
                            messageTextField.getEditText().getText().toString(), false, event.getId(), false));

                } else if (chosenChip.isChecked()) {
                    sendNotification(event.getChosen(), new Notification(
                            getText(R.string.notification_custom_name).toString(),
                            messageTextField.getEditText().getText().toString(), false, event.getId(), false));

                } else if (cancelledChip.isChecked()) {
                    sendNotification(event.getCancelled(), new Notification(
                            getText(R.string.notification_custom_name).toString(),
                            messageTextField.getEditText().getText().toString(), false, event.getId(), false));

                } else if (registeredChip.isChecked()) {
                    sendNotification(event.getRegistered(), new Notification(
                            getText(R.string.notification_custom_name).toString(),
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

        return view;
    }
}
