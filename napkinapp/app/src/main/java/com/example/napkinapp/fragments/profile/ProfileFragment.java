/**
 * Fragment of the profile screen. Allows all users to edit their profile.
 */

package com.example.napkinapp.fragments.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.ImageUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ProfileFragment extends Fragment {
    private final User user;
    private TitleUpdateListener titleUpdateListener;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    private TextView nameText;
    private TextView emailText;
    private TextView phoneText;
    private TextView addressText;
    Switch notificationSwitch;

    private ImageUtils imageUtils = new ImageUtils(ImageUtils.PROFILE);

    private Uri profileImageUri = null;
    private ImageView profileImage;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        profileImageUri = result.getData().getData();
                        profileImage.setImageURI(profileImageUri);
                    }
                }
        );
    }

    private void updateUserInfo(User user) {
        user.setName(nameText.getText().toString());
        user.setEmail(emailText.getText().toString());
        user.setPhoneNumber(phoneText.getText().toString());
        user.setAddress(addressText.getText().toString());

        DB_Client db = new DB_Client();
        db.writeData("Users", user.getAndroidId(), user, new DB_Client.DatabaseCallback<Void>() {
            @Override
            public void onFailure(Exception e) {
                Log.e("User update/creation", "Something went wrong updating user");
                Toast.makeText(getContext(), "Error updating/creating user! Please try again!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(@Nullable Void data) {
                Log.i("User update/creation", "User updated/created");
                Toast.makeText(getContext(), "Successfully updated your profile!", Toast.LENGTH_SHORT).show();
            }
        });
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
        nameText = view.findViewById(R.id.editTextName);
        emailText = view.findViewById(R.id.editTextEmailAddress);
        phoneText = view.findViewById(R.id.editTextPhone);
        addressText = view.findViewById(R.id.editTextAddress);
        profileImage = view.findViewById(R.id.image);


        nameText.setText(user.getName());
        emailText.setText(user.getEmail());
        phoneText.setText(user.getPhoneNumber());
        addressText.setText(user.getAddress());

        // load profile pick
        if(user.getProfileImageUri() != null) {
            try {
                Glide.with(view).load(Uri.parse(user.getProfileImageUri())).into(profileImage);
                Log.i("Profile", "Loaded user profile url: " + user.getProfileImageUri());
            }
            catch (Exception e){
                Log.e("Profile", "failed to load profile image: ", e);
            }
        }

        notificationSwitch = view.findViewById(R.id.notification_switch);
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


        FloatingActionButton removeProfileImage = view.findViewById(R.id.deleteProfileImageButton);
        if (user.getProfileImageUri() == null) {
            removeProfileImage.setVisibility(View.GONE);
        } else {
            removeProfileImage.setVisibility(View.VISIBLE);
        }
        removeProfileImage.setOnClickListener((v) -> {
            String imageUri = user.getProfileImageUri();
            profileImageUri = null;
            user.setProfileImageUri(null);
            updateUserInDB("Removed Profile Picture");
            profileImage.setImageURI(profileImageUri);
            try {
                new ImageUtils().deleteImage(imageUri);
                if (user.getProfileImageUri() == null) {
                    removeProfileImage.setVisibility(View.GONE);
                } else {
                    removeProfileImage.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
                Log.e("ImageUtils", "Failed to delete the image, image may already be deleted", e);
            }

            removeProfileImage.setVisibility(View.GONE);
        });

        FloatingActionButton editProfileImage = view.findViewById(R.id.editProfileImageButton);
        editProfileImage.setOnClickListener((v) -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
                });

        Button confirmButton = view.findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener((v) -> {
            if(profileImageUri != null) {
                imageUtils.uploadImage(profileImageUri, user.getAndroidId())
                        .addOnSuccessListener(uri -> {
                            user.setProfileImageUri(uri.toString());
                            updateUserInfo(user);
                            if (user.getProfileImageUri() == null) {
                                removeProfileImage.setVisibility(View.GONE);
                            } else {
                                removeProfileImage.setVisibility(View.VISIBLE);
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e("UploadImage", "Failed to upload image: " + e.getMessage());
                            Toast.makeText(getContext(), "Failed uploading image! Please try again!", Toast.LENGTH_SHORT).show();
                        });
            }
            else{
                updateUserInfo(user);
            }
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
