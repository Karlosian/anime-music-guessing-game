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
        if(anime.size()>0){
        return anime.get(0);}
        else{
        AnimeJson l = new AnimeJson();
        return l;

        }
    }

    public void setAnime(List<AnimeJson> anime) {
        this.anime = anime;
    }    }
