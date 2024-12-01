package com.example.napkinapp.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.napkinapp.models.Event;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

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
        default void onSuccess(@Nullable T data) {
            Log.i("DB", "Successfully ran query");
        }

        /**
         * Called when the query fails during execution.
         *
         * @param e The exception thrown during the query.
         */
        default void onFailure(Exception e) {
            Log.e("DB", Objects.requireNonNull(e.getMessage()));
            Log.e("DB", Arrays.toString(e.getStackTrace()));
        }
    }

    // suppress warnings because we are not specifying T here
    @SuppressWarnings("rawtypes")
    public static final DB_Client.DatabaseCallback IGNORE = new DB_Client.DatabaseCallback() {};


    private final FirebaseFirestore database;

    /**
     * Constructor for initializing the DB_Client instance with a Firestore reference.
     */
    public DB_Client() {
        database = FirebaseFirestore.getInstance();
    }

    /**
     * Executes a query and returns a single result based on the specified return type.
     *
     * @param collection The Firestore collection name.
     * @param conditions A list of query conditions.
     * @param callback A callback for handling success or failure of the query.
     * @param returnType The expected class type of the results. null if no results expected.
     */
    public <T> void executeQuery(
            String collection,
            List<Function<Query, Query>> conditions,
            DatabaseCallback<T> callback,
            Class<T> returnType
    ) {
        CollectionReference collectionRef = database.collection(collection);
        Query query = collectionRef;

        // Apply conditions to the query
        for (Function<Query, Query> condition : conditions) {
            query = condition.apply(query);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (returnType == Void.class) {
                    callback.onSuccess(null);
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
     * Executes a query and returns a list of results based on the specified return type.
     *
     * @param collection The Firestore collection name.
     * @param conditions A list of query conditions.
     * @param callback A callback for handling success or failure of the query.
     * @param elementType The expected class type of the elements in the list.
     */
    public <T> void executeQueryList(
            String collection,
            List<Function<Query, Query>> conditions,
            DatabaseCallback<List<T>> callback,
            Class<T> elementType
    ) {
        CollectionReference collectionRef = database.collection(collection);
        Query query = collectionRef;

        // Apply conditions to the query
        for (Function<Query, Query> condition : conditions) {
            query = condition.apply(query);
        }

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<T> resultList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    resultList.add(document.toObject(elementType));
                }
                callback.onSuccess(resultList);
            } else {
                callback.onFailure(Objects.requireNonNull(task.getException()));
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
     * Inserts data into the specified collection in Firestore with an autogenerated document ID.
     * Callback contains the id generated from Firestore
     *
     * @param collection The Firestore collection to insert into.
     * @param data The data to write.
     * @param callback A callback for handling success or failure of the query, including the generated document ID.
     */
    public void insertData(String collection, Object data, DatabaseCallback<String> callback) {
        database.collection(collection).add(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String generatedId = task.getResult().getId();

                        // Add the "id" field to the document
                        database.collection(collection).document(generatedId)
                                .update("id", generatedId)
                                .addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        callback.onSuccess(generatedId);
                                    } else {
                                        callback.onFailure(updateTask.getException());
                                    }
                                });
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
     * Finds all documents matching the specified whereIn statement in a Firestore collection.
     *
     * @param collection The Firestore collection to search.
     * @param field The field to check is in list.
     * @param list The list to check if field is in.
     * @param callback A callback for handling success or failure of the query.
     * @param returnType The expected class type of the results. null if no results expected
     * @param <T> The type of each document in the results.
     */
    public <T> void findAllIn(String collection, String field, @NonNull List<Object> list, DatabaseCallback<List<T>> callback, Class<T> returnType) {
        CollectionReference colRef = database.collection(collection);
        Query query = colRef.whereIn(field, list);

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

    /**
     * Adds a snapshot listener for a single document in Firestore.
     * This listener will be triggered every time the document changes in Firestore.
     *
     * @param collection The Firestore collection to listen to.
     * @param documentId The ID of the document to listen to.
     * @param callback The callback to be triggered when the document changes.
     * @param returnType The class type of the data in the document.
     */
    public <T> void addDocumentSnapshotListener(String collection, String documentId, DatabaseCallback<T> callback, Class<T> returnType) {
        database.collection(collection).document(documentId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        callback.onFailure(e);
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        T result = documentSnapshot.toObject(returnType);
                        callback.onSuccess(result);
                    } else {
                        callback.onSuccess(null);
                    }
                });
    }

    /**
     * Adds a snapshot listener for a collection in Firestore.
     * This listener will be triggered every time any document in the collection changes.
     *
     * @param collection The Firestore collection to listen to.
     * @param callback The callback to be triggered when any document in the collection changes.
     * @param returnType The class type of the data in the documents.
     */
    public <T> void addCollectionSnapshotListener(String collection, DatabaseCallback<List<T>> callback, Class<T> returnType) {
        database.collection(collection)
                .addSnapshotListener((querySnapshot, e) -> {
                    if (e != null) {
                        callback.onFailure(e);
                        return;
                    }
                    if (querySnapshot != null && !querySnapshot.isEmpty()) {
                        List<T> resultList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : querySnapshot) {
                            resultList.add(document.toObject(returnType));
                        }
                        callback.onSuccess(resultList);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                });
    }
}
