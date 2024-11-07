package com.example.napkinapp.fragments.viewevents;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.EditTextPopupFragment;
import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.Event;
import com.example.napkinapp.models.User;
import com.google.android.material.chip.Chip;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class OrganizerViewEventFragment extends Fragment {
    private Context mContext;
    private Event event;
    private TitleUpdateListener titleUpdateListener;
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    // this guy needs to be global so that the callback defined in onAttach can access it.
    ImageView eventImage;


    public OrganizerViewEventFragment(Event event){
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

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
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

        Chip waitlistChip = view.findViewById(R.id.chip_waitlist);
        Chip chosenChip = view.findViewById(R.id.chip_chosen);
        Chip cancelledChip = view.findViewById(R.id.chip_cancelled);
        Chip registeredChip = view.findViewById(R.id.chip_registered);

        ListView entrantsListView = view.findViewById(R.id.entrants_list_view);

        TextInputLayout messageTextField = view.findViewById(R.id.message_text_field);

        Button sendMessage = view.findViewById(R.id.send_message);

        editEventName.setOnClickListener(v-> {
            EditTextPopupFragment popup = new EditTextPopupFragment("Edit Event Name",  eventName.getText().toString(), eventName::setText);
            popup.show(getActivity().getSupportFragmentManager(), "popup");
        });

        editEventImage.setOnClickListener(v->{
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // Description
        editEventDetails.setOnClickListener(v-> {
            EditTextPopupFragment popup = new EditTextPopupFragment("Edit Event Details",  eventDetails.getText().toString(), eventDetails::setText);
            popup.show(getActivity().getSupportFragmentManager(), "popup");
        });

        // Event Date
        editEventDate.setOnClickListener(v->{
            DatePickerDialog popup = new DatePickerDialog(mContext, (view1, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, dayOfMonth); // Month is 0-based
                Date date = calendar.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                eventDate.setText(formatter.format(date));
            }, 2024, 9, 11);
            popup.show();
        });

        // Lottery Date
        editLotteryDate.setOnClickListener(v->{
            DatePickerDialog popup = new DatePickerDialog(mContext, (view1, year, month, dayOfMonth) -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month - 1, dayOfMonth); // Month is 0-based
                Date date = calendar.getTime();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                lotteryDate.setText(formatter.format(date));
            }, 2024, 9, 11);
            popup.show();
        });

        return view;
    }
}
