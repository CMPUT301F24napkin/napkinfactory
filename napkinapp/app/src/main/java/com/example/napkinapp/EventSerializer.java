package com.example.napkinapp;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import android.util.Log;

import com.google.firebase.Timestamp;

/**
 * Subclass of Serializer. This helper class will serialize Events for use in Firestore database.
 */
public class EventSerializer extends Serializer<Event> {

    public HashMap<String, Object> serialize(Event event) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", event.getName());
        data.put("date", new Timestamp(event.getDate()));
        return data;
    }

    public Event deserialize(QueryDocumentSnapshot documentSnapshot) {
        String id = documentSnapshot.getId();
        String name = documentSnapshot.getString("name");
        Date date = documentSnapshot.getDate("date");
//        Date date = new Date();
        Log.d("Firestore", String.format("Event(%s, %s, %s) fetched", id, name, date));
        return new Event(id, name, date);
    }
}
