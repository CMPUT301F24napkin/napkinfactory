package com.example.napkinapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.MainActivity;
import com.example.napkinapp.R;

public class HeaderFragment extends Fragment {

    public interface OnHeaderButtonClick {
        void handleNotificationButtonClick();
        void handleHamburgerButtonClick();
    }

    private ImageButton notificationBtn;

    private OnHeaderButtonClick listener;
    private TextView headerTitle;

    public HeaderFragment(){
        // Required empty public constructor
    }

    public void setHeaderTitle(String title){
        headerTitle.setText(title);
    }

    public void updateNotificationIcon(){

        if (MainActivity.user == null || MainActivity.user.getNotifications().isEmpty()) {
            notificationBtn.setImageResource(R.drawable.notification_bell_empty);
        } else if (MainActivity.user.allNotificationsRead()){
            notificationBtn.setImageResource(R.drawable.notification_bell);
        } else {
            notificationBtn.setImageResource(R.drawable.notification_bell_active);
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof OnHeaderButtonClick) {
            listener = (OnHeaderButtonClick) context;
        }else{
            throw new RuntimeException(context + " must implement HeaderHandler");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_header, container, false);

        notificationBtn = view.findViewById(R.id.btn_notification);
        ImageButton hamburgerBtn = view.findViewById(R.id.btn_hamburger);
        headerTitle = view.findViewById(R.id.header_title);

        notificationBtn.setOnClickListener((v) -> {
            listener.handleNotificationButtonClick();
        });

        hamburgerBtn.setOnClickListener((v) -> {
            listener.handleHamburgerButtonClick();
        });

        updateNotificationIcon();

        return view;

    }
}
