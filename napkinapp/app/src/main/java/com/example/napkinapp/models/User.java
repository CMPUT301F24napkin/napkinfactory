package com.example.napkinapp.models;


import org.checkerframework.common.value.qual.ArrayLen;

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
    private Boolean isOrganizer;
    private ArrayList<Notification> notifications;

    private ArrayList<String> waitlist;
    private ArrayList<String> chosen;
    private ArrayList<String> registered;

    public User () {
        name = "";
        phoneNumber = "";
        email = "";
        address = "";
        enNotifications = false;
        isOrganizer = false;
        isAdmin = false;
        notifications = new ArrayList<Notification>();
    }

    // New user
    public User (String androidId, String name, String phoneNumber, String email,
                 String address, Boolean enNotifications, Boolean isAdmin, Boolean isOrganizer){
        this.androidId = androidId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.enNotifications = enNotifications;
        this.isAdmin = isAdmin;
        this.isOrganizer = isOrganizer;
    }

    // User from database
    public User (String androidId, String name, String phoneNumber, String email,
                 String address, Boolean enNotifications, Boolean isAdmin, Boolean isOrganizer,
                 ArrayList<Notification> notifications, ArrayList<String> waitlist, ArrayList<String> chosen,
                 ArrayList<String> cancelled, ArrayList<String> registered){
        this.androidId = androidId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.enNotifications = enNotifications;
        this.isAdmin = isAdmin;
        this.isOrganizer = isOrganizer;

        this.notifications = notifications;

        this.waitlist = waitlist;
        this.chosen = chosen;
        this.registered = registered;
    }

    public void setAndroidId(String id) { this.androidId = id; };

    public String getAndroidId() { return androidId; }

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

    public void setOrganizer(Boolean organizer) {
        isOrganizer = organizer;
    }

    public Boolean getIsOrganizer() {
        return isOrganizer;
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

}
