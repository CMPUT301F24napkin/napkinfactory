package com.example.napkinapp.utils;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.Manifest;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.napkinapp.R;
import com.example.napkinapp.fragments.map.CustomInfoWindow;

import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class AbstractMapFragment extends Fragment {

    protected final static String[] permissionsToRequest = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    protected static final int defaultIconResourceId = R.drawable.baseline_location_on_72;

    // this is a fucntion because if you call getResources() on the fragment
    // before it gets connected to a context it crashes
    protected Drawable getDefaultIcon() {
        return ResourcesCompat.getDrawable(getResources(), defaultIconResourceId, null);
    }

    protected void requestMapPermissions() {
        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher, as an instance variable.
        ActivityResultLauncher<String[]> requestMapPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if (isGranted.values().stream().allMatch(granted -> granted == Boolean.TRUE)) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Log.i("Map", "all permissions granted");
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.i("Map", "some permissions not granted");
            }
        });

        requestMapPermissionLauncher.launch(permissionsToRequest);
    }

    public static Marker addMarker(MapView map, Drawable icon, double longitude, double latitude) {
        icon.setTint(Color.parseColor("#FF007AFF"));

        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(longitude, latitude));
        // its null, turn off clicking
        marker.setOnMarkerClickListener((marker1, mapView) -> {return false;}); // 2
        marker.setIcon(icon);

        map.getOverlays().add(marker);
        return marker;
    }

    public static Marker addMarker(MapView map, Drawable icon, String title, String description, double longitude, double latitude, CustomInfoWindow customInfoWindow, Object markerUserData) {
        icon.setTint(Color.parseColor("#FF007AFF"));

        Marker marker = new Marker(map);
        marker.setPosition(new GeoPoint(longitude, latitude));
        marker.setTitle(title);
        marker.setSnippet(description);
        marker.setInfoWindow(customInfoWindow);
        marker.setRelatedObject(markerUserData);

        marker.setIcon(icon);

        map.getOverlays().add(marker);
        return marker;
    }
}
