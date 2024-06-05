package com.animeguessinggame.animeguessinggame;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.embed.swing.SwingNode;
import org.controlsfx.control.textfield.TextFields;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

public class GameApplication extends Application {
    private EmbeddedMediaPlayerComponent mediaPlayerComponent;

    @Override
    public void start(Stage stage) throws IOException, URISyntaxException {
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
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        MediaPlayer mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();

        SwingNode swingNode = new SwingNode();
        createSwingContent(swingNode);

        HBox container = (HBox) root.lookup("#videoBox");
        container.getChildren().add(swingNode);

        stage.show();

        mediaPlayer.media().play(webmUrl);
        mediaPlayer.events().addMediaPlayerEventListener(mediaPlayerComponent);
    }

    private void createSwingContent(SwingNode swingNode) {
        SwingUtilities.invokeLater(() -> {
            swingNode.setContent(mediaPlayerComponent);
        });
    }

    @Override
    public void stop() throws Exception {
        mediaPlayerComponent.release();
        super.stop();
    }

    public static void main(String[] args) {
        launch();
    }
}