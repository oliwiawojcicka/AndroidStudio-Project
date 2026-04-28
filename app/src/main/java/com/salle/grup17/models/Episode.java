package com.salle.grup17.models;

import java.util.List;

public class Episode {
    private int id;
    private String name;
    private String air_date;
    private String episode;
    private List<String> characters;
    private String url;
    private String created;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getAirDate() { return air_date; }
    public String getEpisode() { return episode; }
    public List<String> getCharacters() { return characters; }
    public String getUrl() { return url; }
    public String getCreated() { return created; }
}
