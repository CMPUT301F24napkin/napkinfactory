package com.example.napkinapp.models;


import java.util.ArrayList;

public class User {
    // Need to add some way to store and add photos later
    private String androidId;
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private Boolean enNotifications;
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
        enNotifications = false;
        isAdmin = false;
        notifications = new ArrayList<>();
        waitlist = new ArrayList<>();
        chosen = new ArrayList<>();
        registered = new ArrayList<>();
    }

    // New user
    public User (String androidId, String name, String phoneNumber, String email,
                 String address, Boolean enNotifications, Boolean isAdmin){
        this.androidId = androidId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.enNotifications = enNotifications;
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
                 ArrayList<String> cancelled, ArrayList<String> registered){
        this.androidId = androidId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.enNotifications = enNotifications;
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
        notifications.add(notification);
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

    public ArrayList<String> getRegistered() {
        return registered;
    }

    public ArrayList<String> getChosen() {
        return chosen;
    }

    public ArrayList<String> getWaitlist() {
        return waitlist;
    }

    public void addEventToWaitlist(Event event){
        if(waitlist.contains(event.getId())){
            return; // Don't double add
        }
        waitlist.add(event.getId());
    }

    public void addEventToChosen(Event event){
        if(chosen.contains(event.getId())){
            return; // Don't double add
        }
        chosen.add(event.getId());
    }

    public void addEventToRegistered(Event event){
        if(registered.contains(event.getId())){
            return; // Don't double add
        }
        registered.add(event.getId());
    }

    public void removeEventFromWaitlist(Event event){
        waitlist.remove(event.getId());
    }

    public void removeEventFromChosen(Event event){
        chosen.remove(event.getId());
    }

    public void removeEventFromRegistered(Event event){
        registered.remove(event.getId());
    }

    public void setWaitlist(ArrayList<String> waitlist) {
        this.waitlist = waitlist;
    }

    public void setChosen(ArrayList<String> chosen) {
        this.chosen = chosen;
    }

    public void setRegistered(ArrayList<String> registered) {
        this.registered = registered;
    }
}
