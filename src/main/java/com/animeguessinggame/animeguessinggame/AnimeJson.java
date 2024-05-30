package com.animeguessinggame.animeguessinggame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)

public class AnimeJson {
    private String name;
    private List<AnimeTheme> animethemes;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<AnimeTheme> getAnimethemes() {
        return animethemes;
    }

    public void setAnimethemes(List<AnimeTheme> animethemes) {
        this.animethemes = animethemes;
    }
}
