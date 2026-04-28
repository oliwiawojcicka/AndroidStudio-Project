package com.salle.grup17.models;

public class Character {
    private int id;
    private String name;
    private String status;
    private String species;
    private String type;
    private String gender;
    private String image;
    private Origin origin;
    private LocationInfo location;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getSpecies() {
        return species;
    }

    public String getType() {
        return type;
    }

    public String getGender() {
        return gender;
    }

    public String getImage() {
        return image;
    }

    public Origin getOrigin() {
        return origin;
    }

    public LocationInfo getLocation() {
        return location;
    }
}
