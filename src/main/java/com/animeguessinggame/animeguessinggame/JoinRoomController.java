package com.animeguessinggame.animeguessinggame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

import static java.lang.Integer.parseInt;

public class JoinRoomController {
    @FXML
    private TextField RoomCodeField;
    @FXML
    private TextField malUsernameField;
    @FXML
    private TextField RoomPortField;

    private String MALusername = new String();
    private String roomIP = new String();
    private int roomPort;


    @FXML
    public void StartGame(){
        MALusername = malUsernameField.getText();
        roomIP = RoomCodeField.getText();
        roomPort = parseInt(RoomPortField.getText());
        GameClient.connectClient(roomIP,roomPort,MALusername);
        // Load the waiting-room.fxml
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("waiting-room.fxml"));
            Parent waitingRoomRoot = loader.load();
            Scene waitingRoomScene = new Scene(waitingRoomRoot);

            // Get the current stage
            Stage stage = (Stage) RoomCodeField.getScene().getWindow();
            stage.setScene(waitingRoomScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
