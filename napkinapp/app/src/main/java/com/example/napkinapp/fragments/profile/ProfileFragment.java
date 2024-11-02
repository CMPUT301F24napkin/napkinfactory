package com.example.napkinapp.fragments.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.User;

public class ProfileFragment extends Fragment {
    private User user;
    private TitleUpdateListener titleUpdateListener;

    public ProfileFragment(){
        this.user = new User();
    }

    public ProfileFragment(User user){
        this.user = user;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof TitleUpdateListener){
            titleUpdateListener = (TitleUpdateListener) context;
        }else{
            throw new RuntimeException(context + " needs to implement TitleUpdateListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.user_edit, container, false);

        // Update title
        titleUpdateListener.updateTitle("Profile Settings");


        TextView nameText = view.findViewById(R.id.editTextName);
        TextView emailText = view.findViewById(R.id.editTextEmailAddress);
        TextView phoneText = view.findViewById(R.id.editTextPhone);
        TextView addressText = view.findViewById(R.id.editTextAddress);

        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
        phoneText.setText(user.getPhoneNumber());
        addressText.setText(user.getAddress());

        Switch notificationSwitch = view.findViewById(R.id.notification_switch);
        notificationSwitch.setChecked(user.getEnNotifications());

        Button confirmButton = view.findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener((v) -> {
            user.setName(nameText.getText().toString());
            user.setEmail(emailText.getText().toString());
            user.setPhoneNumber(phoneText.getText().toString());
            user.setAddress(addressText.getText().toString());
            user.setEnNotifications(notificationSwitch.isChecked());
        });

        return view;
    }

}