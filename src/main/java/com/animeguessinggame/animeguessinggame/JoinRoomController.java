package com.animeguessinggame.animeguessinggame;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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
    }




}
