/**
 * Fragment of the profile screen. Allows all users to edit their profile.
 */

package com.example.napkinapp.fragments.profile;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.Location_Utils;

public class ProfileFragment extends Fragment {
    private final User user;
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

        Switch locationSwitch = view.findViewById(R.id.location_switch);
        locationSwitch.setChecked(user.getEnLocation());

        locationSwitch.setOnClickListener((v) -> {
                    user.setEnLocation(locationSwitch.isChecked());
                    updateUserInDB("Location: " + (user.getEnLocation() ? "Enabled" : "Disabled"));

                    if (user.getEnLocation() && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 102);
                    }
                });

        notificationSwitch.setOnClickListener((v) ->{
            user.setEnNotifications(notificationSwitch.isChecked());
            updateUserInDB("Notifications: " + (user.getEnNotifications() ? "Enabled" : "Disabled"));

            if (user.getEnNotifications() && ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        });

        Button confirmButton = view.findViewById(R.id.confirmButton);

        confirmButton.setOnClickListener((v) -> {
            user.setName(nameText.getText().toString());
            user.setEmail(emailText.getText().toString());
            user.setPhoneNumber(phoneText.getText().toString());
            user.setAddress(addressText.getText().toString());

            updateUserInDB("Successfully updated your profile!");
        });

        return view;
    }

    private void updateUserInDB(String successMessage){
        DB_Client db = new DB_Client();

        db.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onFailure(Exception e) {
                Log.e("User update/creation", "Something went wrong updating user");
                Toast.makeText(getContext(), "Error communication with database! Please try again!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(@Nullable Void data) {
                Log.i("User update/creation", "User updated/created");
                Toast.makeText(getContext(), successMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
