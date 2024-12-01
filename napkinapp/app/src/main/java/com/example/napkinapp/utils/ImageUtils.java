package com.example.napkinapp.utils;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

/**
 * This utility class provides methods for managing images in relation to firebase storage
 */
public class ImageUtils {
    private FirebaseStorage FirebaseInt;
    private final String FOLDER;

    public static final String DEFAULT = "VARIOUS";
    public static final String EVENT = "EVENTS";
    public static final String PROFILE = "PROFILES";

    /**
     * This constructor will set directory to the default folder
     */
    public ImageUtils(){
        this.FOLDER = DEFAULT;
        FirebaseInt = FirebaseStorage.getInstance();
    }

    /**
     * Use this constructor for choosing a specific folder to upload to.
     * @param folder one of the predefined constant folders defined in ImageUtils
     */
    public ImageUtils(String folder){
        this.FOLDER = folder;
        FirebaseInt = FirebaseStorage.getInstance();
    }

    /**
     * Uploads images related to specific functionality of the app to firebase cloud
     *
     * @param image Local image to upload to firebase
     * @param filename Desired filename. Should typically be the id of the user/event.
     *                 This has to be unique, unless you intend to overwrite an existing image
     * @return Task<Uri> that resolves once upload succeeds. onSuccess returns Uri from firebase.
     */
    public Task<Uri> uploadImage(Uri image, String filename) {

        // Create a reference to the Firebase Storage location
        StorageReference folderRef = FirebaseInt.getReference().child("IMAGES/" + FOLDER);

        // Start the upload task
        StorageReference imageRef = folderRef.child(filename);
        UploadTask uploadTask = imageRef.putFile(image);

        // Return the Task that will resolve with the download URL once successful
        return uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException()); // Throw the exception to be caught in the onFailureListener
            }
            // Once upload is successful, retrieve the download URL (Task<Uri>)
            return imageRef.getDownloadUrl();
        });
    }

    /**
     * Deletes an image from Firebase Storage.
     *
     * @param firebaseImage Uri of the image on Firebase to be deleted.
     * @return Task<Void> that resolves on successful deletion.
     */
    public Task<Void> deleteImage(String firebaseImage) {
        StorageReference imageRef = FirebaseInt.getReferenceFromUrl(firebaseImage);
        return imageRef.delete();
    }

}
