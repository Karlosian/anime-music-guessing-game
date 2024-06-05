package com.animeguessinggame.animeguessinggame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.media.MediaEventAdapter;
import uk.co.caprica.vlcj.media.MediaEventListener;
import uk.co.caprica.vlcj.player.base.MediaPlayerEventAdapter;


import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static com.animeguessinggame.animeguessinggame.GameApplication.window;

public class ClientInterface implements Initializable {
    public static List<String> possibleSuggestions;
    @FXML
    private TextField answerBox;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label hideVideo;

    @FXML
    private ImageView imageView;

    private Timeline timer;
    private double remainingTime;
    private EmbeddedMediaPlayer mediaPlayer;
    private MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();

    private int round = 0;
    private Scene scene;
    public static String answer;

    public void display() {
        window.setScene(scene);
        startVideo("https://v.animethemes.moe/Naruto-OP2.webm");
        startTimer(40);

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

    public void submitAnswer() {
        String clientAnswer = answerBox.getText();
    }

    public void revealAnswer() {

    }
    
    public void startVideo(String url) {
        // VLCJ setup
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        mediaPlayer.videoSurface().set(new ImageViewVideoSurface(imageView));
        //add event listener to find out when video starts playing
        mediaPlayer.events().addMediaPlayerEventListener(
            new MediaPlayerEventAdapter() {
                @Override
                public void playing(MediaPlayer mediaPlayer) {
                    System.out.println("Video has started playing!");
                }
            }
        );

        mediaPlayer.media().play(url);
    }

    private void startTimer(int timeremaining) {
        remainingTime = timeremaining;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
