package com.animeguessinggame.animeguessinggame;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
class AnimeResponse {
    private List<AnimeJson> anime;

    public List<AnimeJson> getAnime() {
        return anime;
    }
    public AnimeJson getAnime(boolean returnFirstAnimeJson){
        return anime.get(0);
    }

    public void setAnime(List<AnimeJson> anime) {
        this.anime = anime;
    }    }
