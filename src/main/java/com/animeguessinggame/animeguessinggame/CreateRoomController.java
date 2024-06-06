package com.animeguessinggame.animeguessinggame;

import javafx.fxml.FXML;

import java.awt.*;
import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static java.lang.Integer.parseInt;

public class CreateRoomController {
    @FXML
    private TextField apiKeyField;
    @FXML
    private TextField serverPortField;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("waiting-room.fxml"));
            Parent waitingRoomRoot = loader.load();
            Scene waitingRoomScene = new Scene(waitingRoomRoot);

            // Get the current stage and set the new scene
            Stage stage = (Stage) apiKeyField.getScene().getWindow();
            stage.setScene(waitingRoomScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }



