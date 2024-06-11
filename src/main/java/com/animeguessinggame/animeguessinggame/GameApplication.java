package com.animeguessinggame.animeguessinggame;

import dev.katsute.mal4j.anime.Anime;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Scanner;

public class GameApplication extends Application {
    public static Stage window;
    private static Scene scene;
    private static ClientInterface clientInterface;

    public static String roomIp;

    @Override
    public void start(Stage stage) throws IOException {
        // Sets up the FXML file for the homepage
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("FXML/home-page.fxml"));
        Parent root = fxmlLoader.load();
        scene = new Scene(root);

        // Sets up link to download the necessary VLC
        Hyperlink link = (Hyperlink) root.lookup("#downloadLink");
        link.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.videolan.org/vlc/"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Sets the main window
        window = stage;
        window.setScene(scene);
        window.show();
        window.setResizable(false);

        // Sets the icon and title of the game
        Image icon = new Image(getClass().getResource("Images/Icon.png").toExternalForm());
        window.getIcons().add(icon);
        window.setTitle("Anime Guessing Game");
    }

    public void startGame() throws IOException {
        clientInterface = new ClientInterface();
        clientInterface.display();
    }

    public void createRoom() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("FXML/create-room.fxml"));
        Parent root = fxmlLoader.load();

        Label roomCode = (Label) root.lookup("#roomCode");
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    roomIp = addr.getHostAddress();
                    roomCode.setText(roomIp);
                    // System.out.println(iface.getDisplayName() + " " + ip);
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }

        Hyperlink link = (Hyperlink) root.lookup("#linkToMAL");
        link.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://myanimelist.net/apiconfig"));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        scene = new Scene(root);
        window.setScene(scene);
    }

    public void joinRoom() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("FXML/join-room.fxml"));
        Parent root = fxmlLoader.load();

        scene = new Scene(root);
        window.setScene(scene);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }

}