package com.animeguessinggame.animeguessinggame;

import dev.katsute.mal4j.anime.Anime;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class GameController {

    public static Scanner s = new Scanner(System.in);
    /*@FXML
    public void initialize() {
        System.out.println("Enter your Client ID and the username you wish to import");
        String cid = s.nextLine();
        String name = s.nextLine();
        List<Anime> animelist = LoadMALList.importList(cid, name);
        //take anime list and make a vector of every opening and its link
        List<AnimeResponse> openingsList = LoadMALList.getAllOpenings(animelist);

        System.out.println(openingsList.get(0).getAnime(true).getAnimethemes().get(0).getAnimethemeentries().get(0).getVideos().get(0).getLink());
        List<ImportantInfo> animeMusic = LoadMALList.getAsOpenings(openingsList);
        System.out.println("Operation completed");
    }*/


}