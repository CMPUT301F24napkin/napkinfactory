package com.example.napkinapp.models;

public class User {
    // Need to add some way to store and add photos later
    private String name;
    private String phoneNumber;
    private String email;
    private String address;
    private Boolean enNotifications;


    public User () {
        name = "";
        phoneNumber = "";
        email = "";
        address = "";
        enNotifications = false;
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

    public Boolean wantsNotifications() {
        return enNotifications;
    }

    public void setEnNotifications(Boolean enNotifications) {
        this.enNotifications = enNotifications;
    }
}
