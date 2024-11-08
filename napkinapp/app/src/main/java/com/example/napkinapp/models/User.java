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
    private ArrayList<String> cancelled;

    public User () {
        androidId = "";
        name = "";
        phoneNumber = "";
        email = "";
        address = "";
        enNotifications = false;
        isAdmin = false;
        notifications = new ArrayList<Notification>();
        waitlist = new ArrayList<String>();
        chosen = new ArrayList<String>();
        registered = new ArrayList<String>();
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
        this.notifications = new ArrayList<Notification>();
        this.waitlist = new ArrayList<String>();
        this.chosen = new ArrayList<String>();
        this.registered = new ArrayList<String>();
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

    public void setAndroidId(String id) { this.androidId = id; };

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

    public ArrayList<String> getCancelled() {
        return cancelled;
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
}
