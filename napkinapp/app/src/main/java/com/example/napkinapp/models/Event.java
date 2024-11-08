package com.example.napkinapp.models;

import java.util.Date;
import java.util.List;

public class Event {
    // From firestore
    private String id;

    // On creation
    private String organizerId;
    private String name;
    private Date eventDate;
    private Date lotteryDate;
    private Image poster_image;
    private String description;
    private int entrantLimit;
    private int participantLimit;
    private boolean requireGeolocation;
    private Facility facility;

    private String qrHashCode;

    private List<Tag> tags;

    private List<String> waitlist;
    private List<String> chosen;
    private List<String> cancelled;
    private List<String> registered;

    // New event being created
    public Event(String organizerId, String name, Date eventDate, Date lotteryDate, String description,
                 int entrantLimit, int participantLimit, boolean requireGeolocation) {
        this.organizerId = organizerId;
        this.name = name;
        this.eventDate = eventDate;
        this.lotteryDate = lotteryDate;
        this.description = description;
        this.entrantLimit = entrantLimit;
        this.participantLimit = participantLimit;
        this.requireGeolocation = requireGeolocation;
    }

    // Event from database
    public Event(String id, String organizerId, String name, Date eventDate, Date lotteryDate, String description,
                 int entrantLimit, int participantLimit, boolean requireGeolocation, String qrHashCode,
                 List<String> waitlist, List<String> chosen, List<String> cancelled, List<String> registered) {
        this.id = id;
        this.organizerId = organizerId;
        this.name = name;
        this.eventDate = eventDate;
        this.lotteryDate = lotteryDate;
        this.description = description;
        this.entrantLimit = entrantLimit;
        this.participantLimit = participantLimit;
        this.requireGeolocation = requireGeolocation;

        this.qrHashCode = qrHashCode;
        this.waitlist = waitlist;
        this.chosen = chosen;
        this.cancelled = cancelled;
        this.registered = registered;

    }

    public Event(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrganizerId(){
        return organizerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setOrganizerId(String organizerId){
        this.organizerId = organizerId;

    }

    public void setEntrantLimit(int entrantLimit) {
        this.entrantLimit = entrantLimit;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setParticipantLimit(int participantLimit){
        this.participantLimit = participantLimit;
    }

    public void setLotteryDate(Date date){
        this.lotteryDate = date;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    public String getQrHashCode() {
        return qrHashCode;
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

    public List<String> getWaitlist() {
        return waitlist;
    }

    public List<String> getChosen() {
        return chosen;
    }

    public List<String> getCancelled() {
        return cancelled;
    }

    public List<String> getRegistered() {
        return registered;
    }
}
