package com.animeguessinggame.animeguessinggame;

import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.*;
import java.util.Enumeration;

public class GameApplication extends Application {
    public static Stage window;
    private static Scene scene;
    private static ClientInterface clientInterface;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("home-page.fxml"));
        Parent root = fxmlLoader.load();

        scene = new Scene(root);

        Hyperlink link = (Hyperlink) root.lookup("#downloadLink");
        link.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI("https://www.videolan.org/vlc/"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        window = stage;
        window.setScene(scene);
        window.show();

        clientInterface = new ClientInterface();
    }

    public void startGame() {
        clientInterface.display();
    }

    public void createRoom() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("create-room.fxml"));
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
                    roomCode.setText(addr.getHostAddress());
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });

        scene = new Scene(root);
        window.setScene(scene);
    }

    public void joinRoom() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("join-room.fxml"));
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