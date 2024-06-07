package com.animeguessinggame.animeguessinggame;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.media.MediaEventAdapter;
import uk.co.caprica.vlcj.media.MediaEventListener;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;

public class WaitingRoomController {
    @FXML
    private Button StartButton;
    private GameClient gameClient;
    private ClientInterface clientInterface;
    public void initialize(GameClient gameClient) {
        this.gameClient = gameClient;
    }
    @FXML
    private void StartGame() throws IOException, ClassNotFoundException, InterruptedException {
        gameClient.getAllAnimeTitles();
        clientInterface = gameClient.getClientInterface();
        clientInterface.initializeMediaPlayer();
        List<String> animeTitles = new ArrayList<>(gameClient.animeNames);
        gameClient.startGame();
        Stage stage = (Stage) StartButton.getScene().getWindow();

        clientInterface.gameClient = gameClient;

        clientInterface.possibleSuggestions = animeTitles;
        System.out.println("animeTitles " + animeTitles);
        clientInterface.setupAutoCompletion();
        stage.setScene(clientInterface.getScene());


    }

}
