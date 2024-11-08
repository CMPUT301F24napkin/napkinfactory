package com.example.napkinapp.models;

import java.util.ArrayList;
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
    private int entrantLimit;       // the number of users that can join the waitlist
    private int participantLimit;   // the number of users that are chosen out of the waitlist
    private boolean requireGeolocation;
    private Facility facility;

    private String qrHashCode;

    private List<Tag> tags;

    private ArrayList<String> waitlist;
    private ArrayList<String> chosen;
    private ArrayList<String> cancelled;
    private ArrayList<String> registered;

    // New event being created
    public Event(String organizerId, String name, Date eventDate, Date lotteryDate, String description,
                 int entrantLimit, int participantLimit, boolean requireGeolocation) {
        init(); // provide sensible defaults

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
                 ArrayList<String> waitlist, ArrayList<String> chosen, ArrayList<String> cancelled, ArrayList<String> registered) {
        init(); // provide sensible defaults

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

    // provide sensible defaults for members to avoid bugs
    private void init() {
        this.id = "event_placeholder";
        this.organizerId = "placeholder";
        this.name = "name_placeholder";
        this.eventDate = new Date();
        this.lotteryDate = new Date();
        this.description = "description_placeholder";
        this.entrantLimit = 20;
        this.participantLimit = Integer.MAX_VALUE;
        this.requireGeolocation = false;

        this.poster_image = new Image();
        this.facility = new Facility();
        this.qrHashCode = "qrHashCode_placeholder";
        this.tags = new ArrayList<>();
        this.waitlist = new ArrayList<>();
        this.chosen = new ArrayList<>();
        this.cancelled = new ArrayList<>();
        this.registered = new ArrayList<>();
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

    public ArrayList<String> getWaitlist() {
        return waitlist;
    }

    public ArrayList<String> getChosen() {
        return chosen;
    }

    public ArrayList<String> getCancelled() {
        return cancelled;
    }

    public ArrayList<String> getRegistered() {
        return registered;
    }

    public void setWaitlist(ArrayList<String> waitlist) {
        this.waitlist = waitlist;
    }

    public void setChosen(ArrayList<String> chosen) {
        this.chosen = chosen;
    }

    public void setCancelled(ArrayList<String> cancelled) {
        this.cancelled = cancelled;
    }

    public void setRegistered(ArrayList<String> registered) {
        this.registered = registered;
    }

    public void addUserToWaitlist(User user){
        if(waitlist.contains(user.getAndroidId())){
            return; // Don't double add
        }
        waitlist.add(user.getAndroidId());
    }

    public void addUserToChosen(User user){
        if(chosen.contains(user.getAndroidId())){
            return; // Don't double add
        }
        chosen.add(user.getAndroidId());
    }

    public void addUserToRegistered(User user){
        if(registered.contains(user.getAndroidId())){
            return; // Don't double add
        }
        registered.add(user.getAndroidId());
    }

    public void addUserToCancelled(User user){
        if(cancelled.contains(user.getAndroidId())){
            return; // Don't double add
        }
        cancelled.add(user.getAndroidId());
    }

    public void removeUserFromWaitlist(User user){
        waitlist.remove(user.getAndroidId());
    }

    public void removeUserFromChosen(User user){
        chosen.remove(user.getAndroidId());
    }

    public void removeUserFromRegistered(User user){
        registered.remove(user.getAndroidId());
    }

    public void removeUserFromCancelled(User user){
        cancelled.remove(user.getAndroidId());
    }

}
