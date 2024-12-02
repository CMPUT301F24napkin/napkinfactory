package com.example.napkinapp.models;

/**
 * Model class for a tag
 * A tag is an easy way to group events together
 */
public class Tag {
    private String name;

    public Tag(){}

    public Tag(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
