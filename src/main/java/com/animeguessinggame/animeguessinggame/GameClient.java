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

    public GameClient(String ip, int port) throws IOException{
        clientSocket = new Socket(ip, port);
        out = new ObjectOutputStream(clientSocket.getOutputStream());
    }
    public void sendUserInfo(String userName, List<ImportantInfo> openingList) throws IOException {
        out.writeObject(userName);
        out.writeObject(openingList);
        out.flush();
    }
    public void close() throws IOException {
        out.close();
        clientSocket.close();
    }

    public static void connectClient(String serverIp, int serverPort, String userName) {
        try {
            GameClient client = new GameClient(serverIp, serverPort);

            // Create test info
            List<ImportantInfo> openingList = new ArrayList<>();

            // Send user info to the server
            client.sendUserInfo(userName, openingList);

            // Close the client connection
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }
