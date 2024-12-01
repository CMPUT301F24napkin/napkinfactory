package com.example.napkinapp.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationRequest.Builder;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

// How to implement
//
// Location_Utils locationUtils = new Location_Utils(this.getContext());
// locationUtils.getLastLocation(new Location_Utils.LocationCallbackInterface() {
//    @Override
//    public void onLocationRetrieved(Location location) {
//        // Handle location data
//        Log.d(TAG, "Location: " + location.getLatitude() + ", " + location.getLongitude());
//    }
//    @Override
//    public void onPermissionDenied() {
//        // Handle permission denial
//        Log.e(TAG, "Location permissions are denied!");
//    }
//    @Override
//    public void onError(Exception e) {
//        // Handle errors
//        Log.e(TAG, "Error retrieving location: " + e.getMessage());
//    }
// });



public class Location_Utils {

    private static final String TAG = "Location_Utils";
    private static final long LOCATION_REQUEST_INTERVAL = 10000L; // 10 seconds
    private static final long FASTEST_INTERVAL = 5000L;          // 5 seconds

    private final Context context;
    private final FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    public Location_Utils(Context context) {
        this.context = context;
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    /**
     * Check if location permissions are granted.
     */
    public boolean isLocationPermissionGranted() {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Get the last known location.
     */
    @SuppressLint("MissingPermission")
    public void getLastLocation(@NonNull LocationCallbackInterface callbackInterface) {
        if (!isLocationPermissionGranted()) {
            Log.e(TAG, "Location permissions are not granted!");
            callbackInterface.onPermissionDenied();
            return;
        }

        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callbackInterface.onLocationRetrieved(location);
                    } else {
                        Log.w(TAG, "Last location is null, requesting location updates...");
                        requestLocationUpdates(callbackInterface);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get last location: " + e.getMessage());
                    callbackInterface.onError(e);
                });
    }

    /**
     * Request location updates.
     */
    @SuppressLint("MissingPermission")
    public void requestLocationUpdates(@NonNull LocationCallbackInterface callbackInterface) {
        if (!isLocationPermissionGranted()) {
            Log.e(TAG, "Location permissions are not granted!");
            callbackInterface.onPermissionDenied();
            return;
        }

        LocationRequest locationRequest = new Builder(LOCATION_REQUEST_INTERVAL)
                .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
                .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null && !locationResult.getLocations().isEmpty()) {
                    for (Location location : locationResult.getLocations()) {
                        callbackInterface.onLocationRetrieved(location);
                    }
                    stopLocationUpdates(); // Stop updates after a successful result
                }
            }
        };

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    /**
     * Stop location updates.
     */
    public void stopLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
            locationCallback = null;
        }
    }

    /**
     * Callback interface to handle location updates.
     */
    public interface LocationCallbackInterface {
        void onLocationRetrieved(Location location);

        void onPermissionDenied();

        void onError(Exception e);
    }
}
