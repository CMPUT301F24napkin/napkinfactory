package com.example.napkinapp.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Model class of an Event.
 * it contains all raw the data of an event as well as lists of users (androidId) that are
 * waitlisted, chosen, cancelled, and registered in this event.
 */
public class Event {
    // From firestore
    private String id;

    // On creation
    private String organizerId;
    private String name;
    private Date eventDate;
    private Date lotteryDate;
    private String eventImageUri;
    private String description;
    private int entrantLimit;       // the number of users that can join the waitlist
    private int participantLimit;   // the number of users that are chosen out of the waitlist
    private boolean requireGeolocation;
    private Facility facility;



    private String qrHashCode;

    private ArrayList<String> tags;

    private ArrayList<String> waitlist;
    private ArrayList<String> chosen;
    private ArrayList<String> cancelled;
    private ArrayList<String> registered;
    private HashMap<String, ArrayList<Double>> entrantLocations;

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
                 ArrayList<String> waitlist, ArrayList<String> chosen, ArrayList<String> cancelled, ArrayList<String> registered,
                 HashMap<String, ArrayList<Double>> entrantLocations, String eventImageUri, ArrayList<String> tags) {
      
        init(); // provide sensible defaults

        this.id = id;
        this.organizerId = organizerId;
        this.name = name;
        this.eventDate = eventDate;
        this.lotteryDate = lotteryDate;
        this.eventImageUri = eventImageUri;
        this.description = description;
        this.entrantLimit = entrantLimit;
        this.participantLimit = participantLimit;
        this.requireGeolocation = requireGeolocation;

        this.qrHashCode = qrHashCode;
        this.waitlist = waitlist;
        this.chosen = chosen;
        this.cancelled = cancelled;
        this.registered = registered;
        this.entrantLocations = entrantLocations;

        this.tags = tags;
    }

    // provide sensible defaults for members to avoid bugs
    public void init() {
        this.id = "event_placeholder";
        this.organizerId = "placeholder";
        this.name = "name_placeholder";
        this.eventDate = new Date();
        this.lotteryDate = new Date();
        this.description = "description_placeholder";
        this.entrantLimit = 20;
        this.participantLimit = Integer.MAX_VALUE;
        this.requireGeolocation = false;

        this.facility = new Facility();
        this.qrHashCode = "qrHashCode_placeholder";
        this.tags = new ArrayList<>();
        this.waitlist = new ArrayList<>();
        this.chosen = new ArrayList<>();
        this.cancelled = new ArrayList<>();
        this.registered = new ArrayList<>();
        this.entrantLocations = new HashMap<>();
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

    /**
     * add user to waitlist if waitlist is not null
     * @param userId user to add
     */
    public void addUserToWaitlist(String userId){
        if(waitlist.contains(userId)){
            // Don't double add
            return;
        }
        waitlist.add(userId);
    }

    /**
     * remove user from waitlist if waitlist is not null
     * @param userId user to remove
     */
    public void removeUserFromWaitList(String userId){
        if(waitlist == null)
            return;
        // Already checks for null userid
        waitlist.remove(userId);
    }

    public ArrayList<String> getWaitlist(){
        if(waitlist == null)
            return new ArrayList<String>();
        return  waitlist;
    }

    /**
     * add user to chosen list. prevents double add.
     * @param userId user to add
     */
    public void addUserToChosen(String userId){
        if(chosen.contains(userId)) {
            // Don't double add
            return;
        }
        chosen.add(userId);
    }

    public void removeUserFromChosen(String userId){
        chosen.remove(userId);
    }

    public ArrayList<String> getChosen(){
        return chosen;
    }

    /**
     * adds a user to this event's cancelled list if registered is not null.
     * @param userId the user to add
     */
    public void addUserToRegistered(String userId){
        if(registered.contains(userId)){
            // Don't double add
            return;
        }
        registered.add(userId);
    }

    /**
     * removes a user from this event's registered list if registered is not null.
     * @param userId the user to remove
     */
    public void removeUserFromRegistered(String userId){
        if(registered == null)
            return;
        // Already checks for null userid
        registered.remove(userId);
    }

    public ArrayList<String> getRegistered(){
        if(registered == null)
            return new ArrayList<String>();
        return  registered;
    }

    /**
     * adds a user to this event's cancelled list if cancelled is not null.
     * @param userId the user to add
     */
    public void addUserToCancelled(String userId){
        if(cancelled.contains(userId)){
            // Don't double add
            return;
        }
        cancelled.add(userId);
    }

    /**
     * removes a user from this event's cancelled list if cancelled is not null.
     * @param userId the user to remove
     */
    public void removeUserFromCancelled(String userId){
        if(cancelled == null)
            return;
        // Already checks for null userid
        cancelled.remove(userId);
    }

    /**
     * Returns cancelled. If cancelled is null, construct a default one.
     * @return cancelled
     */
    public ArrayList<String> getCancelled(){
        if(cancelled == null)
            return new ArrayList<String>();
        return  cancelled;
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

    public void addEntrantLocation(String userId, ArrayList<Double> location){
        if (entrantLocations == null){
            entrantLocations = new HashMap<>();
        }
        entrantLocations.put(userId, location);
    }

    public void removeEntrantLocation(String userId){
        entrantLocations.remove(userId);
    }

    public HashMap<String, ArrayList<Double>> getEntrantLocations(){
        return this.entrantLocations;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }
  
    public void setEventImageUri(String eventImageUri){
        this.eventImageUri = eventImageUri;
    }

    public String getEventImageUri(){
        return eventImageUri;
    }
}
