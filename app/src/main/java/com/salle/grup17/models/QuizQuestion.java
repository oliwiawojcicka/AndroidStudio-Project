package com.salle.grup17.models;

import java.util.List;

public class QuizQuestion {
    private int id;
    private String type;
    private Boolean tipoA;
    private String characterName;
    private String image;
    private String episodeTitle;
    private String episodeCode;
    private List<QuizOption> options;

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Boolean getTipoA() {
        return tipoA;
    }

    public String getCharacterName() {
        return characterName;
    }

    public String getImage() {
        return image;
    }

    public String getEpisodeTitle() {
        return episodeTitle;
    }

    public String getEpisodeCode() {
        return episodeCode;
    }

    public List<QuizOption> getOptions() {
        return options;
    }

    public boolean isTypeA() {
        return "A".equalsIgnoreCase(type) || Boolean.TRUE.equals(tipoA);
    }
}