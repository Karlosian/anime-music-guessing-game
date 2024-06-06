package com.animeguessinggame.animeguessinggame;

import javafx.fxml.FXML;

import java.awt.*;
import java.io.IOException;

import javafx.scene.control.TextField;

import static java.lang.Integer.parseInt;

public class CreateRoomController {
    @FXML
    private TextField apiKeyField;
    @FXML
    private TextField serverPortField;
    private String apiKey;

    @FXML
    private void handleStartGame() throws IOException {
        String apiKey = apiKeyField.getText();
        int port = parseInt(serverPortField.getText());
        System.out.println("API Key: " + apiKey);
        System.out.println("Port: " + port);
        this.apiKey = apiKey;
        GameServer.StartServer(port);

    }


}
