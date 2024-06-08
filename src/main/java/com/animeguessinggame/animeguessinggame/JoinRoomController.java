package com.animeguessinggame.animeguessinggame;

import javafx.concurrent.Task;
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
        Task<GameClient.ClientConnectionResult> task = new Task<GameClient.ClientConnectionResult>() {
            @Override
            protected GameClient.ClientConnectionResult call() throws Exception {
                return GameClient.connectClient(roomIP, roomPort, MALusername);
            }
            @Override
            protected void succeeded() {
                try {
                    GameClient.ClientConnectionResult result = getValue();
                    GameClient gameClient = result.getClient();
                    List<String> users = result.getUsernames();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FXML/waiting-room.fxml"));
                    Parent waitingRoomRoot = loader.load();
                    WaitingRoomController waitingRoomController = loader.getController();
                    waitingRoomController.initialize(gameClient); //pass gameclient so that game is able to start

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
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected void failed(){
                getException().printStackTrace();
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true); //lets the thread get terminated when app exits
        thread.start();


    }
}
