package com.animeguessinggame.animeguessinggame;

import javafx.fxml.FXML;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import static java.lang.Integer.parseInt;

public class CreateRoomController {
    @FXML private TextField apiKeyField;
    @FXML private TextField serverPortField;
    @FXML private GridPane playerList;
    private static GridPane leaderBoard;
    private GameClient gameClient;
    public static CreateRoomController instance;

    @FXML
    private void handleStartGame() throws IOException {
        String apiKey = apiKeyField.getText();
        int port = parseInt(serverPortField.getText());
        System.out.println("API Key: " + apiKey);
        System.out.println("Port: " + port);
        GameServer.apiKey = apiKey;
        new Thread(() -> {
            try {
                GameServer gameServer = new GameServer(port);
                gameServer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/server-room.fxml"));
            Parent waitingRoomRoot = loader.load();
            //WaitingRoomController waitingRoomController = loader.getController();
            //waitingRoomController.initialize(gameClient);
            Label serverPort = (Label) waitingRoomRoot.lookup("#serverPort");
            serverPort.setText("Server Ip : " + GameApplication.roomIp);

            Label roomCode = (Label) waitingRoomRoot.lookup("#roomCode");
            roomCode.setText("Server Port : " + port);

            Scene waitingRoomScene = new Scene(waitingRoomRoot);

            instance = loader.getController();

            // Get the current stage and set the new scene
            Stage stage = (Stage) apiKeyField.getScene().getWindow();
            stage.setScene(waitingRoomScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateLeaderBoard(Map<Integer, String> leaderBoardMap) {
        if (leaderBoardMap.isEmpty()) return;
        synchronized (leaderBoardMap) {
            // Create a copy of the map to fix concurrency issue
            Map<Integer, String> leaderBoardCopy = new HashMap<>(leaderBoardMap);
            Platform.runLater(() -> {
                int counter = 0;
                playerList.getChildren().clear();
                for (Map.Entry<Integer, String> player : leaderBoardCopy.entrySet()) {
                    playerList.add(new Label(String.valueOf(player.getValue())), 0, counter);
                    playerList.add(new Label(String.valueOf(player.getKey())), 1, counter);
                    counter++;
                }
            });
        }
    }
}



