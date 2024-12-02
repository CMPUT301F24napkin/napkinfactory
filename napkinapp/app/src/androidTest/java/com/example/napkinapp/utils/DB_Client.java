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

    public static final DB_Client.DatabaseCallback IGNORE = new DB_Client.DatabaseCallback() {};

    // Variables to store predefined data for methods
    private static List<Object> findOneDataList = new ArrayList<>();
    private static int findOneCallIndex = 0;

    private static List<List<?>> findAllDataList = new ArrayList<>();
    private static int findAllCallIndex = 0;

    private static List<List<?>> findAllInDataList = new ArrayList<>();
    private static int findAllInCallIndex = 0;

    private static Object executeQueryData;
    private static List<?> executeQueryListData;
    private static List<Object> insertedData = new ArrayList<>();
    private static List<Object> writtenData = new ArrayList<>();
    private static List<Object> deletedFilters = new ArrayList<>();
    private static List<Map<String, Object>> updatedFilters = new ArrayList<>();
    private static List<Map<String, Object>> updatedData = new ArrayList<>();
    private static Exception exceptionToThrow;

    // Listeners for snapshot methods
    private static List<DatabaseCallback<?>> documentSnapshotListeners = new ArrayList<>();
    private static List<DatabaseCallback<?>> collectionSnapshotListeners = new ArrayList<>();
    private static Object documentSnapshotData;
    private static List<Object> collectionSnapshotData;

        public static void addFindOneData(Object data) {
        findOneDataList.add(data);
    }

    public static void addFindAllData(List<?> data) {
        findAllDataList.add(data);
    }

    public static void addFindAllInData(List<?> data) {
        findAllInDataList.add(data);
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

    public static List<Object> getDeletedFilters() {
        return deletedFilters;
    }

    public static List<Map<String, Object>> getUpdatedFilters() {
        return updatedFilters;
    }

    public static List<Map<String, Object>> getUpdatedData() {
        return updatedData;
    }

    // Reset method to clear data between tests
    public static void reset() {
        findOneDataList.clear();
        findOneCallIndex = 0;

        findAllDataList.clear();
        findAllCallIndex = 0;

        findAllInDataList.clear();
        findAllInCallIndex = 0;

        executeQueryData = null;
        executeQueryListData = null;
        insertedData.clear();
        writtenData.clear();
        deletedFilters.clear();
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
        if (findOneCallIndex < findOneDataList.size()) {
            data = returnType.cast(findOneDataList.get(findOneCallIndex));
            findOneCallIndex++;
        } else {
            data = null;
        }

        callback.onSuccess(data);
    }

    public <T> void findAll(String collection, Map<String, Object> filters, DatabaseCallback<List<T>> callback, Class<T> returnType) {
        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        List<T> data = new ArrayList<>();
        if (findAllCallIndex < findAllDataList.size()) {
            for (Object obj : findAllDataList.get(findAllCallIndex)) {
                data.add(returnType.cast(obj));
            }
            findAllCallIndex++;
        }

        callback.onSuccess(data);
    }

    public <T> void findAllIn(String collection, String field, @NonNull List<Object> list, DatabaseCallback<List<T>> callback, Class<T> returnType) {
        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        List<T> data = new ArrayList<>();
        if (findAllInCallIndex < findAllInDataList.size()) {
            for (Object obj : findAllInDataList.get(findAllInCallIndex)) {
                data.add(returnType.cast(obj));
            }
            findAllInCallIndex++;
        }

        // Simulate asynchronous behavior
        callback.onSuccess(data);
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
       callback.onSuccess(data);
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
        callback.onSuccess(data);
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

        deletedFilters.add(filters);
        callback.onSuccess(null);
    }

    public void deleteAll(String collection, Map<String, Object> filters, DatabaseCallback<Void> callback) {        if (exceptionToThrow != null) {
            callback.onFailure(exceptionToThrow);
            return;
        }

        deletedFilters.add(filters);
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
