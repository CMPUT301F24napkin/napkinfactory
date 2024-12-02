/**
 * Fragment for the create event page.
 */

package com.example.napkinapp.fragments.createevent;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.ImageUtils;
import com.example.napkinapp.utils.QRCodeUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class CreateEventFragment extends Fragment {
    private EditText eventName, eventDate, lotteryDate, eventDescription, entrantLimit, participantLimit;
    private CheckBox participantLimitCheckbox;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private ImageButton eventDatePickerButton, lotteryDatePickerButton;
    private SwitchCompat geolocationSwitch;
    private Button createButton;
    private User loggedInUser;

    private ImageUtils imageUtils = new ImageUtils(ImageUtils.EVENT);
    private Uri eventImageUri = null;
    private ImageView eventImage;


    public CreateEventFragment(){
        // Required null constructor
    }

    public CreateEventFragment(User user){
        this.loggedInUser = user;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        eventImageUri = result.getData().getData();
                        if (eventImageUri != null) {
                            eventImage.setImageURI(eventImageUri);
                            FloatingActionButton removeEventImage = getView().findViewById(R.id.delete_event_image_button);
                            removeEventImage.setVisibility(View.VISIBLE); // Make remove button visible
                        }
                    }
                }
        );
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event, container, false);

        eventName = view.findViewById(R.id.event_name);
        eventDate = view.findViewById(R.id.event_date);
        eventDatePickerButton = view.findViewById(R.id.event_date_picker);
        lotteryDate = view.findViewById(R.id.lottery_date);
        eventImage = view.findViewById(R.id.event_image);
        eventDescription = view.findViewById(R.id.event_description);
        lotteryDatePickerButton = view.findViewById(R.id.lottery_date_picker);
        entrantLimit = view.findViewById(R.id.entrant_limit);
        participantLimit = view.findViewById(R.id.participant_limit);
        participantLimitCheckbox = view.findViewById(R.id.participant_limit_checkbox);
        createButton = view.findViewById(R.id.create_button);
        geolocationSwitch = view.findViewById(R.id.geolocation_switch);

        participantLimitCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            participantLimit.setEnabled(isChecked);
            participantLimit.setAlpha(isChecked ? 1.0f : 0.5f);
        });

        View.OnClickListener eventDateClickListener = v -> showDatePickerDialog(eventDate);
        eventDatePickerButton.setOnClickListener(eventDateClickListener);

        View.OnClickListener lotteryDateClickListener = v -> showDatePickerDialog(lotteryDate);
        lotteryDatePickerButton.setOnClickListener(lotteryDateClickListener);

        FloatingActionButton removeEventImage = view.findViewById(R.id.delete_event_image_button);

        removeEventImage.setVisibility(View.GONE);

        removeEventImage.setOnClickListener((v) -> {
            String imageUri = eventImageUri.toString();
            eventImageUri = null;
            eventImage.setImageURI(null);
            try {
                new ImageUtils().deleteImage(imageUri);
            } catch (Exception e) {
                Log.e("ImageUtils", "Failed to delete the image, image may already be deleted", e);
            }
            removeEventImage.setVisibility(View.GONE);
        });

        FloatingActionButton editEventImage = view.findViewById(R.id.edit_event_image_button);
        editEventImage.setOnClickListener((v) -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        createButton.setOnClickListener(v -> onCreateButtonClick());

        return view;
    }

    private void onCreateButtonClick() {
        boolean hasError = false;

        // Reset errors
        eventName.setError(null);
        eventDate.setError(null);
        lotteryDate.setError(null);
        entrantLimit.setError(null);
        participantLimit.setError(null);

        // Define a reasonable timestamp range
        long currentTimeMillis = System.currentTimeMillis();
        long maxTimeMillis = currentTimeMillis + (100L * 365 * 24 * 60 * 60 * 1000);

        // Validate event name
        if (eventName.getText().toString().trim().isEmpty()) {
            eventName.setError("Event name cannot be empty");
            hasError = true;
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null, lottery = null;

        // Validate event date
        try {
            date = dateFormat.parse(eventDate.getText().toString());
            if (date.before(new Date()) || date.getTime() > maxTimeMillis) {
                eventDate.setError("Event date cannot be in the past");
                hasError = true;
            }
        } catch (ParseException e) {
            eventDate.setError("Invalid event date format");
            hasError = true;
        }

        // Validate lottery date
        try {
            lottery = dateFormat.parse(lotteryDate.getText().toString());
            if ((date != null && lottery.after(date)) || lottery.getTime() > maxTimeMillis) {
                lotteryDate.setError("Lottery date cannot be before the event date");
                hasError = true;
            }
        } catch (ParseException e) {
            lotteryDate.setError("Invalid lottery date format");
            hasError = true;
        }

        int entrantLimitValue = 0, participantLimitValue = Integer.MAX_VALUE;

        // Validate entrant limit
        try {
            entrantLimitValue = Integer.parseInt(entrantLimit.getText().toString());
            if (entrantLimitValue <= 0) {
                entrantLimit.setError("Entrant limit must be greater than 0");
                hasError = true;
            }
        } catch (NumberFormatException e) {
            entrantLimit.setError("Invalid entrant limit");
            hasError = true;
        }

        // Validate participant limit if enabled
        if (participantLimitCheckbox.isChecked()) {
            try {
                participantLimitValue = Integer.parseInt(participantLimit.getText().toString());
                if (participantLimitValue <= 0 || participantLimitValue > entrantLimitValue) {
                    participantLimit.setError("Participant limit must be greater than 0 and less than or equal to entrant limit");
                    hasError = true;
                }
            } catch (NumberFormatException e) {
                participantLimit.setError("Invalid participant limit");
                hasError = true;
            }
        }

        // If any validation errors exist, stop here
        if (hasError) {
            return;
        }

        // Create the Event object
        Event event = new Event(
                loggedInUser.getAndroidId(),
                eventName.getText().toString().trim(),
                date,
                lottery,
                eventDescription.getText().toString().trim(),
                entrantLimitValue,
                participantLimitValue,
                geolocationSwitch.isChecked()
        );

        // Insert the event into the database
        DB_Client db = new DB_Client();
        db.insertData("Events", event, new DB_Client.DatabaseCallback<String>() {
            @Override
            public void onSuccess(@Nullable String eventId) {
                if (eventId == null) {
                    Log.e("DB", "Failed to get event ID");
                    return;
                }

                String hash = QRCodeUtils.hashString(eventId);
                if (hash == null) {
                    Log.e("QR", "Failed to generate QR Hash code");
                    return;
                }

                db.updateAll("Events", Map.of("id", eventId), Map.of("qrHashCode", hash), new DB_Client.DatabaseCallback<Void>() {});

                if (eventImageUri != null) {
                    imageUtils.uploadImage(eventImageUri, eventId)
                            .addOnSuccessListener(uri -> db.updateAll("Events", Map.of("id", eventId), Map.of("eventImageUri", uri.toString()), new DB_Client.DatabaseCallback<Void>() {}))
                            .addOnFailureListener(e -> {
                                Log.e("UploadImage", "Failed to upload image: " + e.getMessage());
                                Toast.makeText(getContext(), "Failed uploading image! Please try again!", Toast.LENGTH_SHORT).show();
                            });
                }
            }
        });

        getParentFragmentManager().popBackStack();
    }




    private void showDatePickerDialog(EditText targetEditText) {
        // Get the current date to set as the default date in the DatePickerDialog
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show the DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Format the date and set it in the target EditText
                    String selectedDate = (selectedMonth + 1) + "/" + selectedDay + "/" + selectedYear;
                    targetEditText.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }
}
