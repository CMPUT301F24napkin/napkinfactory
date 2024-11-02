package com.example.napkinapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;

public class FooterFragment extends Fragment {
    // Interface is implemented in main activity to handle all clicks
    public interface FooterNavigationListener{
        void handleFooterButtonClick(int btnid);
    }

    private FooterNavigationListener footerNavigationListener;
    private Button selectedBtn;
    private Button event, register, map, QRScanner, myEvents;

    public FooterFragment(){
        // Required null constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof FooterNavigationListener){
            footerNavigationListener = (FooterNavigationListener) context;
        }else{
            throw new RuntimeException(context + " must implement FooterNavigationListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_footer, container, false);

        // Retrieve elements
        event = view.findViewById(R.id.btn_EventList);
        register = view.findViewById(R.id.btn_RegisteredEvents);
        map = view.findViewById(R.id.btn_Map);
        QRScanner = view.findViewById(R.id.btn_QRScanner);
        myEvents = view.findViewById(R.id.btn_MyEvents);

        // Set click listener
        setButtonListener(event, 0);
        setButtonListener(register, 1);
        setButtonListener(map, 2);
        setButtonListener(QRScanner, 3);
        setButtonListener(myEvents, 4);

        if(selectedBtn == null){
            updateSelectedButton(event);
        }

        return view;
    }

    private void setButtonListener(Button btn, int btnid){
        btn.setOnClickListener((v) -> {
            updateSelectedButton(btn);
            footerNavigationListener.handleFooterButtonClick(btnid);
        });
    }

    private void updateSelectedButton(Button btn){
        if (selectedBtn != null) {
            // Set previous button's drawable back to its unselected version
            if (selectedBtn == event) {
                selectedBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.event, 0, 0);
            } else if (selectedBtn == register) {
                selectedBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.registered_events, 0, 0);
            } else if (selectedBtn == map) {
                selectedBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.location, 0, 0);
            } else if (selectedBtn == QRScanner) {
                selectedBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.qr_code_scanner, 0, 0);
            } else if (selectedBtn == myEvents) {
                selectedBtn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.my_events, 0, 0);
            }
        }

        if (btn == event) {
            btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.event_selected, 0, 0);
        } else if (btn == register) {
            btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.registered_events_selected, 0, 0);
        } else if (btn == map) {
            btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.location_selected, 0, 0);
        } else if (btn == QRScanner) {
            btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.qr_code_scanner_selected, 0, 0);
        } else if (btn == myEvents) {
            btn.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.my_events_selected, 0, 0);
        }

        // Update the selectedButton reference
        selectedBtn = btn;
    }

    /**
     * Manually update a button state to selected
     * @param buttonId - an int corresponding to button id (0 is leftmost, 4 is rightmost)
     */
    public void setSelectedButtonById(int buttonId) {
        Button buttonToSelect = null;
        switch (buttonId) {
            case 0:
                buttonToSelect = event;
                break;
            case 1:
                buttonToSelect = register;
                break;
            case 2:
                buttonToSelect = map;
                break;
            case 3:
                buttonToSelect = QRScanner;
                break;
            case 4:
                buttonToSelect = myEvents;
                break;
        }

        if (buttonToSelect != null) {
            updateSelectedButton(buttonToSelect);
        }
    }

    /**
     * Reset all button states to not in use. Useful for when clicking header buttons
     */
    public void resetButtons(){
        event.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.event, 0, 0);
        register.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.registered_events, 0, 0);
        map.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.location, 0, 0);
        QRScanner.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.qr_code_scanner, 0, 0);
        myEvents.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.my_events, 0, 0);

        selectedBtn = null;
    }
}
