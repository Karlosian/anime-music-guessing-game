package com.animeguessinggame.animeguessinggame;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

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
    public void StartGame() throws IOException {
        MALusername = malUsernameField.getText();
        roomIP = RoomCodeField.getText();
        roomPort = parseInt(RoomPortField.getText());
        List<String> users = GameClient.connectClient(roomIP,roomPort,MALusername);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("waiting-room.fxml"));
        Parent waitingRoomRoot = loader.load();
        Scene waitingRoomScene = new Scene(waitingRoomRoot);

        // Get the current stage
        Stage stage = (Stage) RoomCodeField.getScene().getWindow();
        stage.setScene(waitingRoomScene);

        // Add players that joined in the Gridpane
        GridPane playerList = (GridPane) waitingRoomRoot.lookup("#playerList");
        assert users != null;
        if (users.isEmpty()) return;
        for (int i = 0; i < users.size(); i++) {
            playerList.add(new Label(String.valueOf(i + 1)), 0, i);
            playerList.add(new Label(users.get(i)), 1, i);
        }
    }
    }
