package com.example.napkinapp.models;

import java.util.List;

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

    private List<Notification> notifications;

    private List<String> waitlist;
    private List<String> chosen;
    private List<String> cancelled;
    private List<String> registered;

    public User () {
        name = "";
        phoneNumber = "";
        email = "";
        address = "";
        enNotifications = false;
        isOrganizer = false;
        isAdmin = false;
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
                 List<Notification> notifications, List<String> waitlist, List<String> chosen,
                 List<String> cancelled, List<String> registered){
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
        this.cancelled = cancelled;
        this.registered = registered;
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

    public void setOrganizer(Boolean organizer) {
        isOrganizer = organizer;
    }

    public Boolean getIsOrganizer() {
        return isOrganizer;
    }


}
