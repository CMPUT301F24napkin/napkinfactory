package com.example.napkinapp.fragments.createevent;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CreateEventFragment extends Fragment {
    private EditText eventName, eventDate, lotteryDate, eventDescription, registeredEntrantLimit, participantLimit;
    private CheckBox participantLimitCheckbox;
    private ImageButton eventDatePickerButton, lotteryDatePickerButton;

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

        participantLimitCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            participantLimit.setEnabled(isChecked);
            participantLimit.setAlpha(isChecked ? 1.0f : 0.5f);
        });

        View.OnClickListener eventDateClickListener = v -> showDatePickerDialog(eventDate);
        eventDatePickerButton.setOnClickListener(eventDateClickListener);

        View.OnClickListener lotteryDateClickListener = v -> showDatePickerDialog(lotteryDate);
        lotteryDatePickerButton.setOnClickListener(lotteryDateClickListener);

        return view;
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
