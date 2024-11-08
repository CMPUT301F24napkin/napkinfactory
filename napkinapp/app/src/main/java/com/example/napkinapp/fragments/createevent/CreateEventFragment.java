package com.example.napkinapp.fragments.createevent;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.QRCodeUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class CreateEventFragment extends Fragment {
    private EditText eventName, eventDate, lotteryDate, eventDescription, registeredEntrantLimit, participantLimit;
    private CheckBox participantLimitCheckbox;
    private ImageButton eventDatePickerButton, lotteryDatePickerButton;
    private SwitchCompat geolocationSwitch;
    private Button createButton;
    private User loggedInUser;

    public CreateEventFragment(){
        // Required null constructor
    }

    public CreateEventFragment(User user){
        this.loggedInUser = user;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_event, container, false);

        eventName = view.findViewById(R.id.event_name);
        eventDate = view.findViewById(R.id.event_date);
        eventDatePickerButton = view.findViewById(R.id.event_date_picker);
        lotteryDate = view.findViewById(R.id.lottery_date);
        eventDescription = view.findViewById(R.id.event_description);
        lotteryDatePickerButton = view.findViewById(R.id.lottery_date_picker);
        registeredEntrantLimit = view.findViewById(R.id.registered_entrant_limit);
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

        createButton.setOnClickListener(v -> onCreateButtonClick());

        return view;
    }

    private void onCreateButtonClick() {
        DB_Client db = new DB_Client();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        // Parse the date string into a Date object
        Date date = new Date();
        Date lottery = new Date();
        try {
            date = dateFormat.parse(eventDate.getText().toString());
            lottery = dateFormat.parse(lotteryDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int participantLimitValue = -1;
        int registeredLimitValue = 20;
        try{
            participantLimitValue = (participantLimitCheckbox.isChecked()) ? Integer.parseInt(participantLimit.getText().toString()) : -1;
            registeredLimitValue = Integer.parseInt(registeredEntrantLimit.getText().toString());
        }
        catch (NumberFormatException e) {
            // TODO what do we want to do?? maybe ask the user to enter it again.
        }


        // Create the Event object with the Date
        Event event = new Event(loggedInUser.getAndroidId(), eventName.getText().toString(), date, lottery, eventDescription.getText().toString(),
                registeredLimitValue, participantLimitValue, geolocationSwitch.isChecked());

        db.insertData("Events", event, new DB_Client.DatabaseCallback<String>() {
            @Override
            public void onSuccess(@Nullable String data) {
                if (data == null){
                    Log.e("DB", "Failed to get event ID");
                    return;
                }
                String hash = QRCodeUtils.hashString(data);

                if (hash == null){
                    Log.e("QR", "Failed to generate QR Hash code");
                    return;
                }
                db.updateAll("Events", Map.of(
                        "id", data
                ), Map.of(
                        "qrHashCode", hash
                ), new DB_Client.DatabaseCallback<Void>() {});
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
