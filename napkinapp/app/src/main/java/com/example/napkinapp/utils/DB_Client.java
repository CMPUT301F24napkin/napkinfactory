package com.example.napkinapp.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A utility class for handling CRUD operations with Firebase Firestore.
 */
public class DB_Client {

    /**
     * A generic interface for database operations that return any type.
     * @param <T> The type of data returned in the callback.
     */
    public interface DatabaseCallback<T> {
        /**
         * Called when the query successfully executes.
         *
         * @param data The result of the query, or null if the return type is Void.
         */
        void onSuccess(@Nullable T data);

        /**
         * Called when the query fails during execution.
         *
         * @param e The exception thrown during the query.
         */
        void onFailure(Exception e);
    }

    private final FirebaseFirestore database;

    /**
     * Constructor for initializing the DB_Client instance with a Firestore reference.
     */
    public DB_Client() {
        database = FirebaseFirestore.getInstance();
    }

    /**
     * Executes a query and returns the results based on the specified return type.
     *
     * @param query The Firestore query to execute.
     * @param callback A callback for handling success or failure of the query.
     * @param returnType The expected class type of the results. null if no results expected
     */
    public <T> void executeQuery(Query query, DatabaseCallback<T> callback, Class<T> returnType) {
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (returnType == Void.class) {
                    callback.onSuccess(null);
                } else if (returnType == List.class) {
                    List<T> resultList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        resultList.add(document.toObject(returnType));
                    }
                    callback.onSuccess((T) resultList);
                } else {
                    if (!task.getResult().isEmpty()) {
                        T result = task.getResult().getDocuments().get(0).toObject(returnType);
                        callback.onSuccess(result);
                    } else {
                        callback.onSuccess(null);
                    }
                }
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Writes data to the specified collection and document in Firestore, overwriting it if the
     * document already exists.
     *
     * @param collection The Firestore collection to write to.
     * @param documentName The name of the document where the data will be stored.
     * @param data The data to write.
     * @param callback A callback for handling success or failure of the query.
     */
    public void writeData(String collection, String documentName, Object data, DatabaseCallback<Void> callback) {
        database.collection(collection).document(documentName).set(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(task.getException());
                    }
                });
    }

    /**
     * Finds a single document matching the specified filters in a Firestore collection.
     *
     * @param collection The Firestore collection to search.
     * @param filters The filters to apply to the search query.
     * @param callback A callback for handling success or failure of the query.
     * @param returnType The expected class type of the results. null if no results expected
     */
    public <T> void findOne(String collection, Map<String, Object> filters, DatabaseCallback<T> callback, Class<T> returnType) {
        CollectionReference colRef = database.collection(collection);
        Query query = applyFilters(colRef, filters);

        query.limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                T result = task.getResult().getDocuments().get(0).toObject(returnType);
                callback.onSuccess(result);
            } else if (task.isSuccessful()) {
                callback.onSuccess(null);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Finds all documents matching the specified filters in a Firestore collection.
     *
     * @param collection The Firestore collection to search.
     * @param filters The filters to apply to the search query.
     * @param callback A callback for handling success or failure of the query.
     * @param returnType The expected class type of the results. null if no results expected
     * @param <T> The type of each document in the results.
     */
    public <T> void findAll(String collection, Map<String, Object> filters, DatabaseCallback<List<T>> callback, Class<T> returnType) {
        CollectionReference colRef = database.collection(collection);
        Query query = applyFilters(colRef, filters);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<T> result = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    result.add(document.toObject(returnType));
                }
                callback.onSuccess(result);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Updates all documents matching the specified filters in a Firestore collection.
     *
     * @param collection The Firestore collection to update.
     * @param filters The filters to apply to the search query.
     * @param updates The fields and values to update in matching documents.
     * @param callback A callback for handling success or failure of the query.
     */
    public void updateAll(String collection, Map<String, Object> filters, Map<String, Object> updates, DatabaseCallback<Void> callback) {
        CollectionReference colRef = database.collection(collection);
        Query query = applyFilters(colRef, filters);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean anyMatch = false;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().update(updates);
                    anyMatch = true;
                }
                if (anyMatch) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(new Exception("No matching records found for update"));
                }
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Deletes a single document matching the specified filters in a Firestore collection.
     *
     * @param collection The Firestore collection to delete from.
     * @param filters The filters to apply to the search query.
     * @param callback A callback for handling success or failure of the query.
     */
    public void deleteOne(String collection, Map<String, Object> filters, DatabaseCallback<Void> callback) {
        CollectionReference colRef = database.collection(collection);
        Query query = applyFilters(colRef, filters);

        query.limit(1).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                document.getReference().delete()
                        .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                        .addOnFailureListener(callback::onFailure);
            } else {
                callback.onFailure(new Exception("No matching record found for deletion"));
            }
        });
    }

    /**
     * Deletes all documents matching the specified filters in a Firestore collection.
     *
     * @param collection The Firestore collection to delete from.
     * @param filters The filters to apply to the search query.
     * @param callback A callback for handling success or failure of the query.
     */
    public void deleteAll(String collection, Map<String, Object> filters, DatabaseCallback<Void> callback) {
        CollectionReference colRef = database.collection(collection);
        Query query = applyFilters(colRef, filters);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean anyMatch = false;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    document.getReference().delete();
                    anyMatch = true;
                }
                if (anyMatch) {
                    callback.onSuccess(null);
                } else {
                    callback.onFailure(new Exception("No matching records found for deletion"));
                }
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Counts all documents matching the specified filters in a Firestore collection.
     *
     * @param collection The Firestore collection to search.
     * @param filters The filters to apply to the search query.
     * @param callback A callback for handling success or failure of the query.
     */
    public void count(String collection, Map<String, Object> filters, DatabaseCallback<Integer> callback) {
        CollectionReference colRef = database.collection(collection);
        Query query = applyFilters(colRef, filters);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int count = task.getResult().size();
                callback.onSuccess(count);
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    /**
     * Helper method to apply filters to a Firestore query.
     *
     * @param colRef The Firestore collection reference to apply filters on.
     * @param filters The filters to apply to the query.
     * @return A Query object with the filters applied.
     */
    private Query applyFilters(CollectionReference colRef, @Nullable Map<String, Object> filters) {
        Query query = colRef;
        if (filters != null && !filters.isEmpty()) {
            for (Map.Entry<String, Object> filter : filters.entrySet()) {
                query = query.whereEqualTo(filter.getKey(), filter.getValue());
            }
        }
        return query;
    }
}