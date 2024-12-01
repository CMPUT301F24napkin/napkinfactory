package com.example.napkinapp.models;

import java.util.List;

/**
 * model class for the facility. not implemented yet
 */
public class Facility {

    String id;

    List<Double> location;
    String name;
    String description;

    public Facility(){

    }

    public Facility(String name, String description, List<Double> location) {
        init(); // set sensible defaults

        this.location = location;
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void init() {
        // create reasonable defaults
        this.name = "placeholder_name";
        this.description = "placeholder_description";
        this.id = "placeholder_facility";
        this.location = List.of(53.527309714453466, -113.52931950296305);

    }
}
