package com.animeguessinggame.animeguessinggame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}