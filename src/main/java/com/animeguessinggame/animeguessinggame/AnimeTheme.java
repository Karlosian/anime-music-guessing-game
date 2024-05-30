package com.animeguessinggame.animeguessinggame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)

public class AnimeTheme {
    private String type;
    private Song song;
    private List<AnimeThemeEntry> animethemeentries;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Song getSong() {
        return song;
    }

    public void setSong(Song song) {
        this.song = song;
    }

    public List<AnimeThemeEntry> getAnimethemeentries() {
        return animethemeentries;
    }

    public void setAnimethemeentries(List<AnimeThemeEntry> animethemeentries) {
        this.animethemeentries = animethemeentries;
    }
}
