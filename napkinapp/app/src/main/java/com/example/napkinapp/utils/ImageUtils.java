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
    private static final FirebaseStorage FirebaseInt = FirebaseStorage.getInstance();

    /**
     * Uploads images related to specific functionality of the app to firebase cloud
     *
     * @param image local image to upload to firebase
     * @return Task<Uri> that resolves once upload succeeds. onSuccess returns Uri from firebase.
     */
    public static Task<Uri> uploadImage(Uri image) {

        // Create a reference to the Firebase Storage location
        StorageReference folderRef = FirebaseInt.getReference().child("IMAGES");
        String fileName = Long.toString(System.currentTimeMillis());  // Generate unique file name

        // Start the upload task
        UploadTask uploadTask = folderRef.child(fileName).putFile(image);

        // Return the Task that will resolve with the download URL once successful
        return uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException()); // Throw the exception to be caught in the onFailureListener
            }
            // Once upload is successful, retrieve the download URL (Task<Uri>)
            return folderRef.getDownloadUrl();
        });
    }

    /**
     * Deletes an image from Firebase Storage.
     *
     * @param firebaseImage Uri of the image on Firebase to be deleted.
     * @return Task<Void> that resolves on successful deletion.
     */
    public static Task<Void> deleteImage(Uri firebaseImage) {
        StorageReference imageRef = FirebaseInt.getReferenceFromUrl(firebaseImage.toString());
        return imageRef.delete();
    }

    /**
     * Replaces a firebase image with a new image from local storage
     *
     * @param firebaseImage image on firebase that is going to be replaced
     * @param newImage new image on local storage
     * @return Task<Uri> that resolves once updates succeeds. onSuccess returns Uri from firebase.
     */
    public static Task<Uri> updateImage(Uri firebaseImage, Uri newImage) {
        return deleteImage(firebaseImage).continueWithTask(deleteTask -> {
            if (!deleteTask.isSuccessful()) {
                throw Objects.requireNonNull(deleteTask.getException()); // Throw if delete fails
            }
            // upload new image
            return uploadImage(newImage);
        });
    }
}
