package com.salle.grup17.models;

import java.util.List;

public class Location {
    private int id;
    private String name;
    private String type;
    private String dimension;
    private List<String> residents;
    private String url;
    private String created;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getDimension() { return dimension; }
    public List<String> getResidents() { return residents; }
    public String getUrl() { return url; }
    public String getCreated() { return created; }
}