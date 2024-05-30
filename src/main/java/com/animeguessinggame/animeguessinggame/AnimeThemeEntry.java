package com.animeguessinggame.animeguessinggame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class AnimeThemeEntry {
    private List<Video> videos;
    public List<Video> getVideos() {
        return videos;
    }
    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

}

