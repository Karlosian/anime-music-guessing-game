package com.animeguessinggame.animeguessinggame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import javafx.util.Duration;
import org.controlsfx.control.textfield.TextFields;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.javafx.videosurface.ImageViewVideoSurface;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static com.animeguessinggame.animeguessinggame.GameApplication.window;

public class ClientInterface {
    public static List<String> possibleSuggestions;
    private TextField answerBox;

    private ProgressBar progressBar;
    private Timeline timer;
    private double remainingTime;

    private Label hideVideo;
    private ImageView imageView;
    private EmbeddedMediaPlayer mediaPlayer;
    private MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();

    private int round = 0;
    private Scene scene;
    public static String answer;

    public void display() {
        window.setScene(scene);
        startVideo("https://v.animethemes.moe/Naruto-OP1.webm");
        startTimer();

        hideVideo.setVisible(true);
    }

    public ClientInterface() throws IOException {
        new NativeDiscovery().discover();

        FXMLLoader fxmlLoader = new FXMLLoader(GameApplication.class.getResource("game-view.fxml"));
        Parent root = fxmlLoader.load();

        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // Auto-completion setup
        answerBox = (TextField) root.lookup("#answerBox");

        possibleSuggestions = Arrays.asList(
                "C", "C#", "C++", "F#", "GoLang",
                "Dart", "Java", "JavaScript", "Kotlin", "PHP",
                "Python", "R", "Swift", "Visual Basic .NET"
        );

        TextFields.bindAutoCompletion(answerBox, possibleSuggestions);

        imageView = (ImageView) root.lookup("#imageView");
        progressBar = (ProgressBar) root.lookup("#timeLeft");
        hideVideo = (Label) root.lookup("#showVideo");

        round = 1;
    }

    public void startVideo(String url) {
        // VLCJ setup
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        mediaPlayer.videoSurface().set(new ImageViewVideoSurface(imageView));
        mediaPlayer.media().play(url);
    }

    private void startTimer() {
        remainingTime = 40;
        timer = new Timeline(new KeyFrame(Duration.seconds(0.01), event -> {
            remainingTime -= 0.01;
            progressBar.setProgress(remainingTime / 30);

            if (remainingTime <= 0) {
                hideVideo.setVisible(false);
                timer.stop();
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void checkAnswer() {

    }
}
