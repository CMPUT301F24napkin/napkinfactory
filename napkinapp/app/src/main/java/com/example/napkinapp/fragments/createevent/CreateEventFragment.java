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
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.fragments.listevents.EventArrayAdapter;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.utils.DB_Client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class CreateEventFragment extends Fragment {
    private EditText eventName, eventDate, lotteryDate, eventDescription, registeredEntrantLimit, participantLimit;
    private CheckBox participantLimitCheckbox;
    private ImageButton eventDatePickerButton, lotteryDatePickerButton;
    private Button createButton;

    public CreateEventFragment(){
        // Required null constructor
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
        Date date = null;
        try {
            date = dateFormat.parse(eventDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Create the Event object with the Date
        Event event = new Event(eventName.getText().toString(), date);

        db.count("Events", null, new DB_Client.DatabaseCallback<Integer>() {
            @Override
            public void onSuccess(@Nullable Integer data) {
                assert data != null;

                event.setId(String.valueOf(data));
                db.writeData("Events", String.valueOf(data), event, new DB_Client.DatabaseCallback<Void>() {
                    @Override
                    public void onSuccess(@Nullable Void data) {
                        Log.i("TAG", "Successfully made event");
                    }

                    @Override
                    public void onFailure(Exception e) {
                        Log.e("TAG", Objects.requireNonNull(e.getMessage()));
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {

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
