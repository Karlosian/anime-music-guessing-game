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

    public GameClient(String ip, int port) throws IOException{
        clientSocket = new Socket(ip, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
        in = new ObjectInputStream(clientSocket.getInputStream());
    }
    public void sendUserInfo(String userName, List<ImportantInfo> openingList) throws IOException {
        out.writeObject(userName);
        out.flush();
    }
    public void requestMalList(String userName) throws IOException {
        out.writeObject("GET_MAL_LIST");
        out.flush();
        try {
            //should send a response if operation is completed successfully
            String response = (String) in.readObject();
            System.out.println(response);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public List<String> getUsernames() throws IOException, ClassNotFoundException {
        return (List<String>) in.readObject();
    }
    public void close() throws IOException {
        out.close();
        clientSocket.close();
    }

    public static List<String> connectClient(String serverIp, int serverPort, String userName) {
        try {
            GameClient client = new GameClient(serverIp, serverPort);
            List<ImportantInfo> openingList = new ArrayList<>();
            client.sendUserInfo(userName, openingList);

            List<String> usernames = client.getUsernames();
            System.out.println("Connected users: " + usernames);

            client.requestMalList(userName);
            // close the client connection
            client.close();

            return usernames;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    }
