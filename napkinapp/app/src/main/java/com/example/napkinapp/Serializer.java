package com.example.napkinapp;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashMap;

/**
 * This helper class will serialize anything that must be stored in the Firestore database.
 * To use it, create a subclass of it, where T is the type that you want to store in Firestore.
 */
public abstract class Serializer <T> {
    public abstract HashMap<String, Object> serialize(T t);
    public abstract T deserialize(QueryDocumentSnapshot documentSnapshot);
}
