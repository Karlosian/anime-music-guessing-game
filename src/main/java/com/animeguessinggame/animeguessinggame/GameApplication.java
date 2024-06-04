package com.animeguessinggame.animeguessinggame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.controlsfx.control.textfield.TextFields;


public class GameApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("game-view.fxml"));
        Parent root = fxmlLoader.load();

        // Test
        TextField answerBox = (TextField) root.lookup("#answerBox");

        List<String> possibleSuggestions = Arrays.asList(
                "C", "C#", "C++", "F#", "GoLang",
                "Dart", "Java", "JavaScript", "Kotlin", "PHP",
                "Python", "R", "Swift", "Visual Basic .NET"
        );

        TextFields.bindAutoCompletion(answerBox, possibleSuggestions);

        String webmUrl = "https://v.animethemes.moe/DragonBall-OP1.webm";
        Media media = new Media(webmUrl);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = ((MediaView) root.lookup("#screenViewer"));
        mediaView.setMediaPlayer(mediaPlayer);

        Scene scene = new Scene(root, 800, 600);
        stage.setResizable(false);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        mediaPlayer.play();
    }

    public static void main(String[] args) {
        launch();
    }
}