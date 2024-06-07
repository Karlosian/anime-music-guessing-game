package com.animeguessinggame.animeguessinggame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class WaitingRoomController {
    @FXML
    private Button StartButton;
    private GameClient gameClient;
    private ClientInterface clientInterface;
    public void initialize(GameClient gameClient) {
        this.gameClient = gameClient;
    }
    @FXML
    private void StartGame() throws IOException {
        gameClient.startGame();
        Stage stage = (Stage) StartButton.getScene().getWindow();
        clientInterface = new ClientInterface(stage);
    }

}
