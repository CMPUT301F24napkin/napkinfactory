package com.example.napkinapp.models;

import java.util.ArrayList;

/**
 * Model class for a User. The user's id is its androidId
 * It contains members for user personal data, status bits for if the user is Admin or not, and a list of notifications.
 * It also contains a list of its waitlisted, chosen, registered Events (eventId) for easier access but it duplicates data so it makes it a little trickier later.
 */
public class User {
    // Need to add some way to store and add photos later
    private String androidId;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private Boolean enNotifications;
    private Boolean enLocation;
    private Boolean isAdmin;
    private ArrayList<Notification> notifications;

    private ArrayList<String> waitlist;
    private ArrayList<String> chosen;
    private ArrayList<String> registered;

    public User () {
        androidId = "";
        name = "";
        phoneNumber = "";
        email = "";
        address = "";
        enLocation = false;
        enNotifications = false;
        isAdmin = false;
        notifications = new ArrayList<>();
        waitlist = new ArrayList<>();
        chosen = new ArrayList<>();
        registered = new ArrayList<>();
    }

    // New user
    public User (String androidId, String name, String phoneNumber, String email,
                 String address, Boolean enNotifications, Boolean isAdmin, Boolean enLocation){
        this.androidId = androidId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.enNotifications = enNotifications;
        this.enLocation = enLocation;
        this.isAdmin = isAdmin;

        // Initialize the rest to defaults
        this.notifications = new ArrayList<>();
        this.waitlist = new ArrayList<>();
        this.chosen = new ArrayList<>();
        this.registered = new ArrayList<>();
    }

    // User from database
    public User (String androidId, String name, String phoneNumber, String email,
                 String address, Boolean enNotifications, Boolean isAdmin,
                 ArrayList<Notification> notifications, ArrayList<String> waitlist, ArrayList<String> chosen,
                 ArrayList<String> cancelled, ArrayList<String> registered, Boolean enLocation){
        this.androidId = androidId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.enNotifications = enNotifications;
        this.enLocation = enLocation;
        this.isAdmin = isAdmin;

        this.notifications = notifications;

        this.waitlist = waitlist;
        this.chosen = chosen;
        this.registered = registered;
    }

    public void setAndroidId(String id) { this.androidId = id; }

    public String getAndroidId() {
        return this.androidId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public Boolean getEnNotifications() {
        return enNotifications;
    }

    public void setEnNotifications(Boolean enNotifications) {
        this.enNotifications = enNotifications;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void addNotification(Notification notification) {
        notifications.add(0, notification);
    }

    public void deleteNotification(Notification notification) {
        notifications.remove(notification);
    }

    public void setNotifications(ArrayList<Notification> notifications) {
        this.notifications = notifications;
    }

    public ArrayList<Notification> getNotifications() {
        return notifications;
    }

    public Boolean allNotificationsRead(){
        for (Notification notification: this.notifications) {
            if (notification.getRead() == Boolean.FALSE) {
                return Boolean.FALSE;
            }
        }
        return Boolean.TRUE;
    }

    public void addEventToWaitlist(String eventId){
        if(waitlist.contains(eventId)){
            // Don't double add
            return;
        }
        waitlist.add(eventId);
    }

    public void removeEventFromWaitList(String eventId){
        // Already checks for null eventId
        waitlist.remove(eventId);
    }

    public ArrayList<String> getWaitlist(){
        return  waitlist;
    }

    public void addEventToChosen(String eventId){
        if(chosen.contains(eventId)){
            // Don't double add
            return;
        }
        chosen.add(eventId);
    }

    public void removeEventFromChosen(String eventId){
        if(chosen == null)
            return;
        chosen.remove(eventId);
    }

    public ArrayList<String> getChosen(){
        if(chosen == null)
            return new ArrayList<>();
        return chosen;
    }

    public void addEventToRegistered(String eventId){
        if(registered.contains(eventId)){
            // Don't double add
            return;
        }
        registered.add(eventId);
    }

    public void removeEventFromRegistered(String eventId){
        if(registered == null)
            return;
        registered.remove(eventId);
    }

    public ArrayList<String> getRegistered(){
        if(registered == null)
            return new ArrayList<>();
        return registered;
    }

    public void setEnLocation (Boolean enLocation) {
        this.enLocation = enLocation;
    }

    public boolean getEnLocation () {
        return this.enLocation;
    }
}
