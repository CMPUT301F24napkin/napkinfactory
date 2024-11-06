package com.example.napkinapp.models;

import java.util.Date;
import java.util.List;

public class Event {
    // From firestore
    private String id;

    // On creation
    private User organizer;
    private String name;
    private Date eventDate;
    private Date lotteryDate;
    private Image poster_image;
    private String description;
    private int entrantLimit;
    private int participantLimit;
    private boolean requireGeolocation;
    private Facility facility;



    private String QRHashCode;

    private List<Tag> tags;

    private List<User> waitlist;
    private List<User> chosen;
    private List<User> cancelled;
    private List<User> registered;

    // New event being created
    public Event(User organizer, String name, Date eventDate, Date lotteryDate, String description,
                 int entrantLimit, int participantLimit, boolean requireGeolocation) {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getOrganizer(){
        return organizer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getEventDate() {
        return eventDate;
    }

    public Date getLotteryDate() {
        return lotteryDate;
    }

    public String getDescription(){
        return  description;
    }

    public int getEntrantLimit() {
        return entrantLimit;
    }

    public int getParticipantLimit() {
        return participantLimit;
    }

    public boolean isRequireGeolocation(){
        return requireGeolocation;
    }

    public void setRequireGeolocation(boolean requireGeolocation) {
        this.requireGeolocation = requireGeolocation;
    }


}
