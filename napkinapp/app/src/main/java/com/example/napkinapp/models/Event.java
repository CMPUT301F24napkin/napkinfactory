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
    private int entrantLimit;
    private int participantLimit;
    private boolean requireGeolocation;
    private Facility facility;

    private String QRHashCode;

    private List<Tag> tags;

    private ArrayList<String> waitlist;
    private ArrayList<String> chosen;
    private ArrayList<String> cancelled;
    private ArrayList<String> registered;

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
                 int entrantLimit, int participantLimit, boolean requireGeolocation, String QRHashCode,
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

        this.QRHashCode = QRHashCode;
        if(waitlist == null){
            this.waitlist = new ArrayList<>();
        }else{
            this.waitlist = (ArrayList<String>) waitlist;
        }

        if(chosen == null){
            this.chosen = new ArrayList<>();
        }else{
            this.chosen = (ArrayList<String>) chosen;
        }

        if(registered == null){
            this.registered = new ArrayList<>();
        }else{
            this.registered = (ArrayList<String>) registered;
        }

        if(cancelled == null){
            this.cancelled = new ArrayList<>();
        }else{
            this.cancelled = (ArrayList<String>) cancelled;
        }

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

    public void addUserToWaitlist(String userId){
        if(waitlist.contains(userId)){
            // Don't double add
            return;
        }
        waitlist.add(userId);
    }

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

    public void addUserToRegistered(String userId){
        if(registered.contains(userId)){
            // Don't double add
            return;
        }
        registered.add(userId);
    }

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

    public void addUserToCancelled(String userId){
        if(cancelled.contains(userId)){
            // Don't double add
            return;
        }
        cancelled.add(userId);
    }

    public void removeUserFromCancelled(String userId){
        if(cancelled == null)
            return;
        // Already checks for null userid
        cancelled.remove(userId);
    }

    public ArrayList<String> getCancelled(){
        if(cancelled == null)
            return new ArrayList<String>();
        return  cancelled;
    }
}
