package com.animeguessinggame.animeguessinggame;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameClient {
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ClientInterface clientInterface;

    private static ArrayList<String> usernames = new ArrayList<>();

    public GameClient(String ip, int port, ClientInterface clientInterface) throws IOException{
        this.clientInterface = clientInterface;
        clientSocket = new Socket(ip, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
        new Thread(this::listenForMessages).start();
    }
    public GameClient() {
    }
    public void sendUserInfo(String userName) throws IOException {
        out.writeObject(userName);
        System.out.println("Client wrote userName String");
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
                } else if (message instanceof ArrayList) {
                    ArrayList<String> u = (ArrayList<String>) message;
                    usernames = u;
                    System.out.println("Connected users: " + usernames);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public void requestMalList(String userName) throws IOException {
        out.writeObject("GET_MAL_LIST");
        System.out.println("Client wrote GET_MAL_LIST String");
        out.flush();
        //should send a response if operation is completed successfully
        System.out.println("Client tries to get 'Operation Completed Successfully' String");
    }

    public void close() throws IOException {
        out.close();
        clientSocket.close();
    }

    public static ClientConnectionResult connectClient(String serverIp, int serverPort, String userName) {
        try {
            GameClient client = new GameClient(serverIp, serverPort, new ClientInterface());
            client.sendUserInfo(userName);

            client.requestMalList(userName);
           //client.close();

            return new ClientConnectionResult(client, usernames);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void startGame() throws IOException {
        out.writeObject("START_GAME");
        System.out.println("Client wrote START_GAME STRING");
        out.flush();
    }

    public ClientInterface getClientInterface() {
        return clientInterface;
    }

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
}
