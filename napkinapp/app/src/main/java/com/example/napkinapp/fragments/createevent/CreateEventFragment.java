/**
 * Fragment for the create event page.
 */

package com.example.napkinapp.fragments.createevent;

import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.example.napkinapp.models.Tag;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.DateUtils;
import com.example.napkinapp.utils.ImageUtils;
import com.example.napkinapp.utils.QRCodeUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CreateEventFragment extends Fragment {
    private EditText eventName, eventDate, lotteryDate, eventDescription, entrantLimit, participantLimit;
    private CheckBox participantLimitCheckbox;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private ImageButton eventDatePickerButton, lotteryDatePickerButton;
    private SwitchCompat geolocationSwitch;
    private Button createButton;
    private User loggedInUser;
    private AutoCompleteTextView tagAutoComplete;
    private ChipGroup tagChipGroup;
    private ArrayList<String> predefinedTags = new ArrayList<>();
    private ArrayList<String> selectedTags = new ArrayList<>();
    private ArrayAdapter<String> tagArrayAdapter;

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
        tagAutoComplete = view.findViewById(R.id.tag_autocomplete);
        tagChipGroup = view.findViewById(R.id.tag_chip_group);

        setUpTags();

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

    private void setUpTags(){
        tagArrayAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, predefinedTags);
        tagAutoComplete.setAdapter(tagArrayAdapter);
        tagAutoComplete.setThreshold(0); // Displays filtered results when 0+ characters entered

        // Onclick handles displaying anytime they click AFTER they've gained focus
        // Focus handles displaying when they initially click

        // When textview clicked shows dropdown
        tagAutoComplete.setOnClickListener(v -> {
            // Check if anything entered
            if(!tagAutoComplete.getText().toString().isEmpty()){
                tagArrayAdapter.getFilter().filter(tagAutoComplete.getText().toString());
            }else{
                tagAutoComplete.showDropDown();
            }
        });

        // When focused show drop down
        tagAutoComplete.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && tagAutoComplete.getText().toString().isEmpty()) {
                tagAutoComplete.showDropDown(); // Show all items if no input yet
            }
        });

        tagAutoComplete.setOnItemClickListener((parent, v, position, id) -> {
            String selectedTag = parent.getItemAtPosition(position).toString();
            Log.d("TAG SELECTED", "Tag selected: " + selectedTag);
            if (!selectedTags.contains(selectedTag)) {
                selectedTags.add(selectedTag);
                addChip(tagChipGroup, selectedTag);
                Log.d("TAG ADDED", "Tag added: " + selectedTags + " Remaining: " + predefinedTags);
            }
            tagAutoComplete.setText(""); // Clear input after selection
            // Re-displays the dropdown
            tagAutoComplete.postDelayed(tagAutoComplete::showDropDown, 200);
        });

        // Get the tags
        DB_Client db = new DB_Client();
        db.findAll("Tags", null, new DB_Client.DatabaseCallback<List<Tag>>() {
            @Override
            public void onSuccess(@Nullable List<Tag> data) {
                if(data == null)
                    return;

                data.forEach(tag -> predefinedTags.add(tag.getName()));
            }

            @Override
            public void onFailure(Exception e) {
                DB_Client.DatabaseCallback.super.onFailure(e);
            }
        }, Tag.class);
    }

    private void addChip(ChipGroup chipGroup, String tag) {
        Chip chip = new Chip(requireContext());
        chip.setText(tag);
        chip.setCloseIconVisible(true);
        chip.setOnCloseIconClickListener(v -> {
            selectedTags.remove(tag);
            chipGroup.removeView(chip);
            Log.d("TAG REMOVED", "Tags: " + selectedTags + " Remaining: " + predefinedTags);
        });
        chipGroup.addView(chip);
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
        String name = eventName.getText().toString().trim();
        if (name.isEmpty()) {
            eventName.setError("Event name cannot be empty");
            hasError = true;
        }

        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date date = null, lottery = null;

        // Validate event date
        try {
            date = dateFormat.parse(eventDate.getText().toString());
            if (date.getTime() > maxTimeMillis) {
                eventDate.setError("Invalid event date");
                hasError = true;
            }
            if (DateUtils.compareDates(date, new Date()) < 0) {
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
            if (lottery.getTime() > maxTimeMillis) {
                lotteryDate.setError("Invalid lottery date");
                hasError = true;
            }
            if (DateUtils.compareDates(lottery, new Date()) < 0) {
                lotteryDate.setError("Event date cannot be in the past");
                hasError = true;
            }
            if (DateUtils.compareDates(lottery, date) > 0) {
                lotteryDate.setError("Lottery date cannot be after the event date");
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

        String description = eventDescription.getText().toString().trim();
        boolean geolocationEnabled = geolocationSwitch.isChecked();

        // Pass validated data to createEvent
        createEvent(loggedInUser.getAndroidId(), name, date, lottery, description, entrantLimitValue, participantLimitValue, geolocationEnabled, eventImageUri);
    }

    public void createEvent(String organizerId, String name, Date eventDate, Date lotteryDate, String description,
                            int entrantLimit, int participantLimit, boolean geolocationEnabled, Uri imageUri) {
        // Create the Event object
        Event event = new Event(
                organizerId,
                name,
                eventDate,
                lotteryDate,
                description,
                entrantLimit,
                participantLimit,
                geolocationEnabled
        );
      
        event.setTags(selectedTags);
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

                if (imageUri != null) {
                    imageUtils.uploadImage(imageUri, eventId)
                            .addOnSuccessListener(uri -> db.updateAll("Events", Map.of("id", eventId), Map.of("eventImageUri", uri.toString()), new DB_Client.DatabaseCallback<Void>() {
                                @Override
                                public void onSuccess(@Nullable Void data) {
                                    getParentFragmentManager().popBackStack();
                                }
                            }))
                            .addOnFailureListener(e -> {
                                Log.e("UploadImage", "Failed to upload image: " + e.getMessage());
                                Toast.makeText(getContext(), "Failed uploading image! Please try again!", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    getParentFragmentManager().popBackStack();
                }
            }
        });
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
