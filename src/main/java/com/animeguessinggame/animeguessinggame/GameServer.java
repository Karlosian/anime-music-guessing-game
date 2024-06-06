package com.animeguessinggame.animeguessinggame;

import com.almasb.fxgl.net.ClientConfig;

import java.io.*;
import java.net.*;
import java.util.List;

public class GameServer {
    private ServerSocket serverSocket;
        public GameServer(int port) throws IOException {
                serverSocket = new ServerSocket(port);
                System.out.println("Server started on port " + port);
        }
        public void start(){
                while(true){
                        try{
                                Socket clientSocket = serverSocket.accept();
                                System.out.println("New client connected");
                              //  new ClientHandler(clientSocket).start();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                }
        }

        private static class ClientHandler extends Thread{
            private Socket clientSocket;
            private ObjectInputStream in;

            public ClientHandler(Socket socket) {
                this.clientSocket = socket;
            }
            public void run() {
                try {
                    //get username
                    in = new ObjectInputStream(clientSocket.getInputStream());
                    String userName = (String) in.readObject();
                    System.out.println("User Name: " + userName);
                    List<ImportantInfo> openingList = (List<ImportantInfo>) in.readObject();
                    System.out.println("User's Opening List: " + openingList);
                    in.close();
                    clientSocket.close();
                }
                catch(IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }

        }
    }

        public static void StartServer(int port){
            try{
                GameServer server = new GameServer(port);
                server.start();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
}
