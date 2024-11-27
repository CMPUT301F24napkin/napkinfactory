package com.example.napkinapp.utils;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class ImageUtils {
    private static FirebaseStorage FirebaseInt = FirebaseStorage.getInstance();

    // Constants for folders on Firebase (MIGHT DELETE LATER)
    public static final String PROFILE = "profiles";
    public static final String EVENT = "events";

    /**
     * Uploads images related to specific functionality of the app to firebase cloud
     *
     * @param image local image to upload to firebase
     * @return Task<Uri> that once resolved will return the Uri of the upload onSuccess
     */
    public static Task<Uri> uploadImage(Uri image) {
        // grab file extension
        String path = image.getPath();
        String ext;
        if (path != null && path.contains(".")) {
            ext = path.substring(path.lastIndexOf(".") + 1);
        }
        else {
            return null;
        }

        // Create a reference to the Firebase Storage location
        StorageReference folderRef = FirebaseInt.getReference().child("IMAGES");
        String fileName = System.currentTimeMillis() + ext;  // Generate unique file name
        StorageReference fileRef = folderRef.child(fileName);

        // Start the upload task
        UploadTask uploadTask = fileRef.putFile(image);

        // Return the Task that will resolve with the download URL once successful
        return uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException()); // Throw the exception to be caught in the onFailureListener
            }
            // Once upload is successful, retrieve the download URL (Task<Uri>)
            return fileRef.getDownloadUrl();
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
     * @return Task<Uri> that once resolved will return the Uri of the new image onSuccess
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
