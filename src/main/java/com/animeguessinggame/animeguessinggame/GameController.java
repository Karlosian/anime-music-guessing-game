package com.animeguessinggame.animeguessinggame;

import com.kttdevelopment.mal4j.anime.Anime;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class GameController {
    @FXML
    private Label welcomeText;

    public static Scanner s = new Scanner(System.in);
    @FXML
    public void initialize() throws IOException {
        System.out.println("Enter your Client ID and the username you wish to import");
        String cid = s.nextLine();
        String name = s.nextLine();
        Vector<Anime> animelist = LoadMALList.importList(cid, name);
        //take anime list and make a vector of every opening and its link
        Vector<AnimeResponse> openingsList = LoadMALList.getAllOpenings(animelist);
        System.out.println(openingsList.elementAt(0).getAnime(true).getAnimethemes().get(0).getAnimethemeentries().get(0).getVideos().get(0));

    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}