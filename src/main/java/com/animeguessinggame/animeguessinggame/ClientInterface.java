package com.animeguessinggame.animeguessinggame;

import com.almasb.fxgl.net.Client;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
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
import java.util.*;

import static com.animeguessinggame.animeguessinggame.GameApplication.window;

public class ClientInterface {
    // Client Interface FXML Scene
    private Scene scene;
    private FXMLLoader fxmlLoader;
    private Parent root;

    // UI Elements
    @FXML private TextField answerBox;
    @FXML private ProgressBar progressBar;
    @FXML private Label hideVideo;
    @FXML private Label roundNumber;
    @FXML private GridPane leaderBoard;
    private Button submitButton;

    // Video Player
    @FXML private ImageView imageView;
    public EmbeddedMediaPlayer mediaPlayer;
    public MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();

    // Game Mechanic Related
    private Timeline timer;
    private int round = 0, points = 0;
    private double remainingTime;
    public GameClient gameClient;

    // Correct answer per round
    public static String answer = "test"; // change this string to the name of the anime op
    // List of answers that pop up in the suggest box of the answerBox
    public static List<String> possibleSuggestions;

    // !!! Used to test a single round without server !!!
    public void display() {
        //keeping for demo purposes
        window.setScene(scene);
        startVideo("https://v.animethemes.moe/Naruto-OP2.webm");
        startTimer(40);

        hideVideo.setVisible(true);
    }

    // !!! Used to test a single round without server !!!
    public ClientInterface() throws IOException {
        // Finds VLC media player
        new NativeDiscovery().discover();

        // Sets up scene
        fxmlLoader = new FXMLLoader(GameApplication.class.getResource("game-view.fxml"));
        root = fxmlLoader.load();
        scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

        // References the FXML elements
        imageView = (ImageView) root.lookup("#imageView");
        progressBar = (ProgressBar) root.lookup("#timeLeft");
        hideVideo = (Label) root.lookup("#showVideo");
        submitButton = (Button) root.lookup("#submitButton");
        roundNumber = (Label) root.lookup("#roundNumber");
        leaderBoard = (GridPane) root.lookup("#leaderboard");

        // Links submitAnswer() to FXML submitButton
        submitButton.setOnAction(event -> {
            try {
                submitAnswer();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Reset Rounds
        round = 0; points = 0;
    }

    // Imports the list of possible answers for autocomplete
    public void setupAutoCompletion(){
        // Finds the answerBox in the FXML file
        answerBox = (TextField) root.lookup("#answerBox");
        answerBox.setDisable(false);

        // Binds the list to the textbox
        TextFields.bindAutoCompletion(answerBox, possibleSuggestions);
    }

    public void initializeMediaPlayer (){
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        mediaPlayer.videoSurface().set(new ImageViewVideoSurface(imageView));
    }

    public void submitAnswer() throws IOException {
        String clientAnswer = answerBox.getText();
        answerBox.setDisable(true);

        int pointAmassed = (int)(1000 * (remainingTime / 30));

        // Tally points to client score
        if (clientAnswer.equals(answer)) {
            points += pointAmassed;
        }

        // Debug
        System.out.println(clientAnswer);
        System.out.println(pointAmassed + " | Total : " + points);
        gameClient.sendScore(points, gameClient.currentUserName);
        gameClient.getScoresMap();
    }

    public void doLeaderboard(Map<Integer, String> sortedLeaderBoard){
        int counter = 0; leaderBoard.getChildren().clear();
        for (Map.Entry<Integer, String> entry : sortedLeaderBoard.entrySet()) {
            Label username = new Label(entry.getValue());
            Label score = new Label(String.valueOf(entry.getKey()));
            leaderBoard.add(username, 0, counter);
            leaderBoard.add(score, 1, counter);
            counter++;
        }

    }
    public void revealAnswer() {

    }

    public void handleServerMessage(String message) {
        // For thread stuff
        Platform.runLater(() -> {
            switch (message) {
                case "CORRECT": case "WRONG": case "GAME_OVER":
                    System.out.println(message.toLowerCase());
                default:
                    if (message.startsWith("OPENING:")) {
                        // Gets the video file url
                        String part1 = message.split(":", 2)[1];
                        String url = part1.split(" ", 2)[0];
                        String correctAnswer = part1.split(" ", 2)[1];

                        // Changes the correct answer
                        answer = correctAnswer;
                        System.out.println("URL: " + url);

                        // Plays new video
                        startVideo(url);
                        answerBox.setDisable(false);
                        answerBox.setText("");

                        // Make media player not visible
                        hideVideo.setVisible(true);
                        round++;
                        roundNumber.setText("Round " + round);
                    }
            }
        });
    }

    public Scene getScene() {
        return scene;
    }

    // Loads and displays the VCL video during the start of the rounds
    public void startVideo(String url) {
        //remove existing player
        disposeMediaPlayer();
        // Create a new media player
        initializeMediaPlayer();

        // Add event listener to find out when video starts playing
        mediaPlayer.events().addMediaPlayerEventListener(
            new MediaPlayerEventAdapter() {
                @Override
                public void playing(MediaPlayer mediaPlayer) {
                    System.out.println("Video has started playing!");
                    startTimer(20);
                }
            }
        );

        mediaPlayer.media().play(url);
    }

    private void disposeMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.controls().stop(); // Stop the media player
            mediaPlayer.release(); // Release the media player resources
            mediaPlayer = null; // Set the media player reference to null
        }
    }

    // Starts the timer right when the video is loaded
    private void startTimer(int timeremaining) {
        // Starts remainingTime at time allowed for players to guess
        remainingTime = timeremaining;
        //stop timer if already running
        if (timer != null) {
            timer.stop();
        }

        Platform.runLater(() -> {
            timer = new Timeline(new KeyFrame(Duration.seconds(0.01), event -> {
                // Decrements the remaining time
                remainingTime -= 0.01;

                // Calculates the percentage of the bar to fill
                double progress = remainingTime/timeremaining;
                progressBar.setProgress(progress);

                // Reveals the answer when the timer finishes
                if (remainingTime <= 0) {
                    hideVideo.setVisible(false);
                    revealAnswer();
                    timer.stop();
                    secondTimer(timeremaining);

                }
            }));
            timer.setCycleCount(Timeline.INDEFINITE);
            timer.play();
        });
    }

    // Gives time for the client to read the right answer and see the video
    private void secondTimer(int timeremaining){
        // Starts remainingTime at time allowed
        remainingTime = timeremaining;
       //stop timer if already running
        if (timer != null) {
            timer.stop();
        }

        Platform.runLater(() -> {
            timer = new Timeline(new KeyFrame(Duration.seconds(0.01), event -> {
                // Decrements the remaining time
                remainingTime -= 0.01;

                // Calculates the percentage of the bar to fill
                double progress = remainingTime/timeremaining;
                progressBar.setProgress(progress);

                if (remainingTime <= 0) {
                    hideVideo.setVisible(false);
                    timer.stop();

                    // Goes to next song
                    try {
                        gameClient.nextSong();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }
            }));
            timer.setCycleCount(Timeline.INDEFINITE);
            timer.play();
        });

    }
}
