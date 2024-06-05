package com.animeguessinggame.animeguessinggame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.embed.swing.SwingNode;
import org.controlsfx.control.textfield.TextFields;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class GameApplication extends Application {
    private EmbeddedMediaPlayer mediaPlayer;
    private MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();

    @Override
    public void start(Stage stage) throws IOException {
        new NativeDiscovery().discover();

        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("game-view.fxml"));
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root, 800, 600);
        stage.setResizable(false);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        scene.getStylesheets().add(this.getClass().getResource("style.css").toExternalForm());

        // Auto-completion setup
        TextField answerBox = (TextField) root.lookup("#answerBox");

        List<String> possibleSuggestions = Arrays.asList(
                "C", "C#", "C++", "F#", "GoLang",
                "Dart", "Java", "JavaScript", "Kotlin", "PHP",
                "Python", "R", "Swift", "Visual Basic .NET"
        );

        TextFields.bindAutoCompletion(answerBox, possibleSuggestions);

        // VLCJ setup
        String webmUrl = "https://v.animethemes.moe/DragonBall-OP1.webm";
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        ImageView imageView = (ImageView) root.lookup("#imageView");
        mediaPlayer.videoSurface().set(new ImageViewVideoSurface(imageView));

        stage.show();

        mediaPlayer.media().play(webmUrl);
    }


    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}