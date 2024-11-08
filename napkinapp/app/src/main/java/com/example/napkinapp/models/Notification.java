package com.example.napkinapp.models;

import java.util.UUID;

public class Notification {
    private String id;
    private String title;
    private String message;
    private Boolean read;
    private String eventId;
    private boolean isOrganizerNotification;

    public Notification (String title, String message) {
        this.id =  UUID.randomUUID().toString();
        this.title = title;
        this.message = message;
        this.read = false;
        this.eventId = "";
        this.isOrganizerNotification = false;
    }

    public Notification (String title, String message, Boolean read) {
        this.id =  UUID.randomUUID().toString();
        this.title = title;
        this.message = message;
        this.read = read;
        this.eventId = "";
        this.isOrganizerNotification = false;
    }

    public Notification (String title, String message, Boolean read, String eventId, boolean isOrganizerNotification) {
        this.id =  UUID.randomUUID().toString();
        this.title = title;
        this.message = message;
        this.read = read;
        this.eventId = eventId;
        this.isOrganizerNotification = isOrganizerNotification;
    }

    public Notification(){}

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public Boolean getRead() {
        return read;
    }

    public String getEventId() {
        return eventId;
    }

    public boolean isOrganizerNotification() {
        return isOrganizerNotification;
    }
}
