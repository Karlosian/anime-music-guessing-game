package com.animeguessinggame.animeguessinggame;

import dev.katsute.mal4j.anime.Anime;
import javafx.fxml.FXML;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class GameController {

    public static Scanner s = new Scanner(System.in);
    @FXML
    public void initialize() {
        System.out.println("Enter your Client ID and the username you wish to import");
        String cid = s.nextLine();
        String name = s.nextLine();
        ArrayList<List<Anime>> animelist = new ArrayList<List<Anime>>();
        animelist.add(LoadMALList.importList(cid, name));
        //just as a test
        animelist.add(LoadMALList.importList(cid, "OrganicGregoreo"));
        //take anime list and make a vector of every opening and its link

        ArrayList<List<AnimeResponse>> openingsList = new ArrayList<List<AnimeResponse>>();

        openingsList.add(LoadMALList.getAllOpenings(animelist.get(0)));
        openingsList.add(LoadMALList.getAllOpenings(animelist.get(1)));

        //System.out.println(openingsList.get(0).getAnime(true).getAnimethemes().get(0).getAnimethemeentries().get(0).getVideos().get(0).getLink());
        ArrayList<List<ImportantInfo>> animeMusic = new ArrayList<List<ImportantInfo>>();
        animeMusic.add(LoadMALList.getAsOpenings(openingsList.get(0)));
        animeMusic.add(LoadMALList.getAsOpenings(openingsList.get(2)));
        List<ImportantInfo> randomizedOpenings = LoadMALList.RandomSelectOpenings(animeMusic, 20);
        System.out.println("Operation completed");
    }


}