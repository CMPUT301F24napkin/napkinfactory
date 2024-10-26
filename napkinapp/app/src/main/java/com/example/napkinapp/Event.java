package com.example.napkinapp;

import java.util.Date;

/**
 * This is a data class for an event.
 * The variable \id is the key of the document representing this event in Firestore
 * TODO add event description, location, etc
 */
public class Event {
    private String id; // the key of the event. must be string by Firestore
    private String name;
    private Date date;

    public Event(String id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
