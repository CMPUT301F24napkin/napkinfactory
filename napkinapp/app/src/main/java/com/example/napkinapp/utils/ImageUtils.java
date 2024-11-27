package com.example.napkinapp.utils;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageUtils {
    private static FirebaseStorage storage = FirebaseStorage.getInstance();

    // Constants for folders on Firebase
    public static final String PROFILE = "profiles";
    public static final String EVENT = "events";

    /**
     * Returns the relative path of a image on the firebase cloud
     * @param uri image on firebase
     * @return path to the image on firebase
     */
    public static String getFilePathFromUri(Uri uri) {
        // Get the full path in Firebase Storage from the URI
        StorageReference ref = storage.getReferenceFromUrl(uri.toString());
        return ref.getPath();  // This returns the file path, e.g., "/profiles/12345.jpg"
    }

    /**
     * uploads image to firebase cloud
     * @param image local image to upload to firebase
     * @param destinationFolder Folder where images should be uploaded based on types
     *                          (WE HAVE CONSTANTS IN THE UTIL CLASS FOR THIS!!!)
     * @return
     */
    // Upload image and return the Firebase path (relative path) to the image
    public static Task<Uri> uploadImage(Uri image, String destinationFolder) {
        // Create a reference to the Firebase Storage location
        StorageReference storageRef = storage.getReference().child(destinationFolder);
        String fileName = System.currentTimeMillis() + ".jpg";  // Generate unique file name
        StorageReference fileRef = storageRef.child(fileName);

        // Start the upload task
        UploadTask uploadTask = fileRef.putFile(image);

        // Return the Task that will resolve with the download URL once successful
        return uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw task.getException(); // Throw the exception to be caught in the onFailureListener
            }
            // Once upload is successful, retrieve the download URL (Task<Uri>)
            return fileRef.getDownloadUrl();
        });
    }

    // Get image Uri from Firebase Storage based on the image path
    public static Task<Uri> getImageUri(String imagePath) {
        // Create reference to the image file in Firebase Storage
        StorageReference storageRef = storage.getReference().child(imagePath);

        // Return the Task<Uri> directly
        return storageRef.getDownloadUrl();
    }
}
