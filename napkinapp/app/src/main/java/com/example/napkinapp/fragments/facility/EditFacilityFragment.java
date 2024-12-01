package com.example.napkinapp.fragments.facility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.napkinapp.R;
import com.example.napkinapp.TitleUpdateListener;
import com.example.napkinapp.fragments.EditTextPopupFragment;
import com.example.napkinapp.fragments.listevents.EventArrayAdapter;
import com.example.napkinapp.fragments.viewevents.ViewEventFragment;
import com.example.napkinapp.models.Facility;
import com.example.napkinapp.models.User;
import com.example.napkinapp.utils.AbstractMapFragment;
import com.example.napkinapp.utils.DB_Client;
import com.example.napkinapp.utils.ImageUtils;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Marker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditFacilityFragment extends AbstractMapFragment {
    private TitleUpdateListener titleUpdateListener;
    private Context mContext;
    private Facility facility;
    private User user;

    // image stuff
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private Uri facilityImageURI = null;
    private ImageView facilityImage;
    private ImageUtils imageUtils = new ImageUtils(ImageUtils.FACILITY);


    // map stuff
    Marker marker;

    public EditFacilityFragment(Facility facility, User user){
        this.facility = facility;
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
        mContext = context;
    }

    /**
     * Custom customizer for the button in the event card. Makes it open the event view screen.
     */
    EventArrayAdapter.EventListCustomizer customizer = (button, event) -> {
        button.setText("View");
        button.setOnClickListener(v->{
            Log.i("Button", String.format("My Events: Clicked on event %s\n", event.getName()));


            getParentFragmentManager().beginTransaction()
                    .replace(R.id.content_fragmentcontainer, new ViewEventFragment(event, user))
                    .addToBackStack(null)
                    .commit();
        });
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        facilityImageURI = result.getData().getData();
                        facilityImage.setImageURI(facilityImageURI);
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Update title
        titleUpdateListener.updateTitle("Edit Facility");

        View view = inflater.inflate(R.layout.facility_edit, container, false);
        TextView nameTextView = view.findViewById(R.id.facility_name);
        TextView descriptionTextView = view.findViewById(R.id.facility_description);

        facilityImage = view.findViewById(R.id.image);

        MapView map = view.findViewById(R.id.map);

        Button editName = view.findViewById(R.id.edit_facility_name);
        Button editImage = view.findViewById(R.id.edit_facility_image);
        Button editDescription = view.findViewById(R.id.edit_facility_description);
        Button savebutton = view.findViewById(R.id.button);

        nameTextView.setText(facility.getName());
        descriptionTextView.setText(facility.getDescription());

        // set image view
        if(facility.getImageUri() != null) {
            try {
                Glide.with(view).load(Uri.parse(facility.getImageUri())).into(facilityImage);
                Log.i("Edit Facility", "Loaded image url: " + facility.getImageUri());
            }
            catch (Exception e){
                Log.e("Edit Facility", "failed to load image: ", e);
            }
        }

        editName.setOnClickListener(v-> {
            EditTextPopupFragment popup = new EditTextPopupFragment("Edit Facility Name", nameTextView.getText().toString(), text -> {
                facility.setName(text);
                nameTextView.setText(text);
            });
            popup.show(getActivity().getSupportFragmentManager(), "popup");
        });

        editDescription.setOnClickListener(v-> {
            EditTextPopupFragment popup = new EditTextPopupFragment("Edit Facility Description", descriptionTextView.getText().toString(), text -> {
                facility.setDescription(text);
                descriptionTextView.setText(text);
            });
            popup.show(getActivity().getSupportFragmentManager(), "popup");
        });

        // edit
        editImage.setOnClickListener((v) -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });


        // do map
        Configuration.getInstance().setUserAgentValue("NapkinApp/1.0 (Android; OS Version; Device Model)");
        map.setTileSource(TileSourceFactory.MAPNIK);
        requestMapPermissions();
        map.setMultiTouchControls(true);
        IMapController mapController = map.getController();
        mapController.setZoom(15.0);

        List<Double> location = facility.getLocation();
        GeoPoint startPoint = new GeoPoint(location.get(0), location.get(1));
        mapController.setCenter(startPoint);

        GestureDetector gestureDetector = new GestureDetector(mContext, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                // Access MapView's projection to convert screen coordinates to GeoPoint
                Projection projection = map.getProjection();
                GeoPoint geoPoint = (GeoPoint) projection.fromPixels((int) e.getX(), (int) e.getY());

                double latitude = geoPoint.getLatitude();
                double longitude = geoPoint.getLongitude();

                // Handle single tap
                Log.i("Facility", "Clicked at: Lat: " + latitude + ", Lon: " + longitude);
                facility.setLocation(List.of(latitude, longitude));
                marker.setPosition(new GeoPoint(latitude, longitude));
                map.invalidate();
                return true; // Event consumed
            }
        });

        map.setOnTouchListener((v, event) -> {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return gestureDetector.onTouchEvent(event);
        });

        // populate map
        if(location.size() >= 2) {
            marker = addMarker(map, getDefaultIcon(), location.get(0), location.get(1));
        }

        savebutton.setOnClickListener(v-> {
            DB_Client db = new DB_Client();

            // guarantees to set the facility.getId() to non-null
            if(facilityImageURI != null) {
                if(facility.getId() != null) {
                    // ready to upload facility
                    uploadFacilityImage(db);
                } else {
                    db.insertData("Facilities", facility, new DB_Client.DatabaseCallback<String>() {
                        @Override
                        public void onSuccess(@Nullable String generatedId) {
                            facility.setId(generatedId);
                            uploadFacilityImage(db);
                        }
                    });
                }
            } else {
                // no new image uploaded, just save the rest of the screen
                saveFacilityAndUser(db);
            }
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    void uploadFacilityImage(DB_Client db) {
        imageUtils.uploadImage(facilityImageURI, facility.getId())
                .addOnSuccessListener(uri -> {
                    facility.setImageUri(uri.toString());
                    saveFacilityAndUser(db);
                })
                .addOnFailureListener(e -> {
                    Log.e("UploadImage", "Failed to upload image: " + e.getMessage());
                    Toast.makeText(getContext(), "Failed uploading image! Please try again!", Toast.LENGTH_SHORT).show();
                    saveFacilityAndUser(db);
                });
    }

    void saveFacilityAndUser(DB_Client db) {
        if(facility.getId() == null) {
            Log.w("Facility", "saving facility but id is null!");
        } else {
            Log.i("Facility", "saving facility id is " + facility.getId());
            db.writeData("Facilities", facility.getId(), facility, DB_Client.IGNORE);

            // set the user's facility to this facility
            user.setFacility(facility.getId());
            db.writeData("Users", user.getAndroidId(), user, DB_Client.IGNORE);
        }
    }

}
