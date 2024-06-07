package com.animeguessinggame.animeguessinggame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class GameClient {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClientInterface clientInterface;

    public static String currentUserName = new String();


    public ArrayList<String> animeNames = new ArrayList<>();

    public static ArrayList<String> usernames = new ArrayList<>();

    public GameClient(String ip, int port, ClientInterface clientInterface) throws IOException{
        this.clientInterface = clientInterface;
        clientSocket = new Socket(ip, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        new Thread(this::listenForMessages).start();
    }
    public GameClient() {

    }

    public void startGame() throws IOException {
        out.writeObject("START_GAME");
        System.out.println("Client wrote START_GAME STRING");
        out.flush();
    }
    public void sendUserInfo(String userName) throws IOException {
        out.writeObject(userName);
        System.out.println("Client wrote userName String");
        out.flush();
    }
    public void getScoresMap() throws IOException{
        out.writeObject("GET_SCORES_MAP");
        System.out.println("Client tries to get the scores map");
        out.flush();
    }

    private void listenForMessages() {
        try {
            while (true) {
                Object message = in.readObject();
                if (message instanceof String) {
                    System.out.println("Client recieves String message: " + message);
                    String strMessage = (String) message;
                    clientInterface.handleServerMessage(strMessage);
                }
                else if (message instanceof ArrayList) {
                    animeNames = (ArrayList<String>) message;
                }
                else if (message instanceof Map) {
                    //append stuff to leaderboard
                    Map m = (Map<Integer, String>) message;
                    clientInterface.doLeaderboard(m);
                }
            }
        }
        catch (IOException | ClassNotFoundException e) {e.printStackTrace();}
    }

    public void sendGuess(String guess) {
        try {
            out.writeObject("GUESS:" + guess);
            System.out.println("Client wrote Guess String");

            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getAllAnimeTitles() throws IOException, ClassNotFoundException, InterruptedException {
        out.writeObject("GET_ANIME_NAMES");
        TimeUnit.SECONDS.sleep(1);

    }

    public void nextSong() throws IOException {
        out.writeObject("NEXT_SONG");
        out.flush();
    }
    public void sendScore(int score, String userName) throws IOException{
        out.writeObject("SCORE:" + score + " " + userName);
        out.flush();

    }

    public void requestMalList(String userName) throws IOException {
        out.writeObject("GET_MAL_LIST");
        System.out.println("Client wrote GET_MAL_LIST String");
        out.flush();
        //should send a response if operation is completed successfully
        System.out.println("Client tries to get 'Operation Completed Successfully' String");
    }

    // Leaves the server
    public void close() throws IOException {
        out.close();
        clientSocket.close();
    }

    // Class to manage the information given upon a client connecting to a server
    public static class ClientConnectionResult {
        private final GameClient client;
        private final List<String> usernames;

        public ClientConnectionResult(GameClient client, List<String> usernames) {
            this.client = client;
            this.usernames = usernames;
        }
        public GameClient getClient() {
            return client;
        }

        public List<String> getUsernames() {
            return usernames;
        }
    }

    // Connects the client to the server
    public static ClientConnectionResult connectClient(String serverIp, int serverPort, String userName) {
        try {
            // Attempts to connect to the hosting server
            GameClient client = new GameClient(serverIp, serverPort, new ClientInterface());
            client.sendUserInfo(userName);
            currentUserName = userName;

            // Gets the anime opening lists
            client.requestMalList(userName);
            //client.close();

            // Returns the anime list as well as the usernames of the clients already connected
            return new ClientConnectionResult(client, usernames);
        }
        catch (IOException e) {e.printStackTrace();}
        return null;
    }

    public ClientInterface getClientInterface() {
        return clientInterface;
    }
}
