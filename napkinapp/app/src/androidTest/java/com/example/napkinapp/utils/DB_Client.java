package com.example.napkinapp.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.core.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Mock version of DB_Client for testing purposes.
 */
public class DB_Client {

    /**
     * A generic interface for database operations that return any type.
     * @param <T> The type of data returned in the callback.
     */
    public interface DatabaseCallback<T> {
        default void onSuccess(@Nullable T data) {}
        default void onFailure(Exception e) {}
    }

    // Variables to store predefined data for methods
    private static Object findOneData;
    private static List<?> findAllData;
    private static List<?> findAllInData;

    private static Object executeQueryData;
    private static List<?> executeQueryListData;
    private static List<Object> insertedData = new ArrayList<>();
    private static List<Object> writtenData = new ArrayList<>();
    private static List<Object> deletedData = new ArrayList<>();
    private static List<Map<String, Object>> updatedFilters = new ArrayList<>();
    private static List<Map<String, Object>> updatedData = new ArrayList<>();
    private static Exception exceptionToThrow;

    // Listeners for snapshot methods
    private static List<DatabaseCallback<?>> documentSnapshotListeners = new ArrayList<>();
    private static List<DatabaseCallback<?>> collectionSnapshotListeners = new ArrayList<>();
    private static Object documentSnapshotData;
    private static List<Object> collectionSnapshotData;

    // Methods to set predefined data
    public static void setFindOneData(Object data) {
        findOneData = data;
    }
  
    public static void setFindAllData(List<Object> data) {
        findAllData = data;
    }

    public static void setFindAllInData(List<?> data) {
        findAllInData = data;
    }

    public static void setExecuteQueryData(Object data) {
        executeQueryData = data;
    }

    public static void setExecuteQueryListData(List<?> data) {
        executeQueryListData = data;
    }

    public static void setExceptionToThrow(Exception e) {
        exceptionToThrow = e;
    }

    public static void setDocumentSnapshotData(Object data) {
        documentSnapshotData = data;
    }

    public static void setCollectionSnapshotData(List<Object> data) {
        collectionSnapshotData = data;
    }

    // Methods to get inserted data
    public static List<Object> getInsertedData() {
        return insertedData;
    }

    public static List<Object> getWrittenData() {
        return writtenData;
    }

    public static List<Object> getDeletedData() {
        return deletedData;
    }

    public static List<Map<String, Object>> getUpdatedFilters() {
        return updatedFilters;
    }

    public static List<Map<String, Object>> getUpdatedData() {
        return updatedData;
    }

    // Reset method to clear data between tests
    public static void reset() {
        findOneData = null;
        findAllData = null;
        findAllInData = null;
        executeQueryData = null;
        executeQueryListData = null;
        insertedData.clear();
        writtenData.clear();
        deletedData.clear();
        updatedFilters.clear();
        updatedData.clear();
        exceptionToThrow = null;
        documentSnapshotListeners.clear();
        collectionSnapshotListeners.clear();
        documentSnapshotData = null;
        collectionSnapshotData = null;
    }

    // Mock methods
    public <T> void findOne(String collection, Map<String, Object> filters, DatabaseCallback<T> callback, Class<T> returnType) {
        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        T data;
        if (findOneData != null) {
            data = returnType.cast(findOneData);
        } else {
            data = null;
        }

        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(data));
    }

    public <T> void findAll(String collection, Map<String, Object> filters, DatabaseCallback<List<T>> callback, Class<T> returnType) {
        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        List<T> data = new ArrayList<>();
        if (findAllData != null) {
            for (Object obj : findAllData) {


                data.add(returnType.cast(obj));
            }
        }

        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(data));
    }
  
    public <T> void findAllIn(String collection, String field, @NonNull List<Object> list, DatabaseCallback<List<T>> callback, Class<T> returnType) {
        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        List<T> result = new ArrayList<>();
        if (findAllInData != null) {
            for (Object obj : findAllInData) {
                result.add(returnType.cast(obj));
            }
        }

        // Simulate asynchronous behavior
        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(result));
    }

    public <T> void executeQuery(
            String collection,
            List<Function<Query, Query>> conditions,
            DatabaseCallback<T> callback,
            Class<T> returnType
    ) {
        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        T data;

        // Simulate data based on the collection
        if (executeQueryData != null) {
            // Check if collection matches mock data (optional)

            data = returnType.cast(executeQueryData);

        } else {
            data = null;
        }

        // Simulate asynchronous behavior
        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(data));
    }


    public <T> void executeQueryList(
            String collection,
            List<Function<Query, Query>> conditions,
            DatabaseCallback<List<T>> callback,
            Class<T> elementType
    ) {
        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        List<T> data = new ArrayList<>();

        // Simulate data based on the collection
        if (executeQueryListData != null) {
            for (Object obj : executeQueryListData) {
                data.add(elementType.cast(obj));
            }
        }

        // Simulate asynchronous behavior
        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(data));
    }


    public void writeData(String collection, String documentName, Object data, DatabaseCallback<Void> callback) {
        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        writtenData.add(data);
        callback.onSuccess(null);
        triggerDocumentSnapshotListeners(data);
    }

    public void insertData(String collection, Object data, DatabaseCallback<String> callback) {
        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        insertedData.add(data);
        String generatedId = "test_generated_id";
        callback.onSuccess(generatedId);
        triggerCollectionSnapshotListeners();
    }

    public void updateAll(String collection, Map<String, Object> filters, Map<String, Object> updates, DatabaseCallback<Void> callback) {
        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        updatedFilters.add(filters);
        updatedData.add(updates);
        callback.onSuccess(null);
    }

    public void deleteOne(String collection, Map<String, Object> filters, DatabaseCallback<Void> callback) {
        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        deletedData.add(filters);
        callback.onSuccess(null);
    }

    public <T> void addDocumentSnapshotListener(String collection, String documentId, DatabaseCallback<T> callback, Class<T> returnType) {
        documentSnapshotListeners.add(callback);

        T data = documentSnapshotData != null ? returnType.cast(documentSnapshotData) : null;
        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(data));
    }

    public <T> void addCollectionSnapshotListener(String collection, DatabaseCallback<List<T>> callback, Class<T> returnType) {
        collectionSnapshotListeners.add(callback);

        List<T> data = new ArrayList<>();
        if (collectionSnapshotData != null) {
            for (Object obj : collectionSnapshotData) {
                data.add(returnType.cast(obj));
            }
        }

        new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(data));
    }

    // Helper methods
    @SuppressWarnings("unchecked")
    private void triggerDocumentSnapshotListeners(Object data) {
        for (DatabaseCallback<?> callback : documentSnapshotListeners) {
            ((DatabaseCallback<Object>) callback).onSuccess(data);
        }
    }

    @SuppressWarnings("unchecked")
    private void triggerCollectionSnapshotListeners() {
        for (DatabaseCallback<?> callback : collectionSnapshotListeners) {
            List<Object> data = collectionSnapshotData != null ? new ArrayList<>(collectionSnapshotData) : new ArrayList<>();
            ((DatabaseCallback<List<Object>>) callback).onSuccess(data);
        }
    }
}
