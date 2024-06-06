package com.animeguessinggame.animeguessinggame;

import com.almasb.fxgl.net.ClientConfig;
import dev.katsute.mal4j.anime.Anime;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static com.animeguessinggame.animeguessinggame.LoadMALList.*;

public class GameServer {
    private List<String> usernames = new ArrayList<>();
    private List<ClientHandler> clients = new ArrayList<>();
    private ServerSocket serverSocket;
    public static String apiKey = new String();
    public static ArrayList<List<ImportantInfo>> AllAnimeLists = new ArrayList<>();
        public GameServer(int port) throws IOException {
                serverSocket = new ServerSocket(port);
                System.out.println("Server started on port " + port);
        }
        public void start(){
                while(true){
                        try{
                                Socket clientSocket = serverSocket.accept();
                                System.out.println("New client connected");
                            ClientHandler clientHandler = new ClientHandler(clientSocket);
                            clients.add(clientHandler);
                            clientHandler.start();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                }
        }

        private class ClientHandler extends Thread{
            private Socket clientSocket;
            private ObjectInputStream in;
            private ObjectOutputStream out;

            public ClientHandler(Socket socket) {
                this.clientSocket = socket;
            }
            public void run() {
                try {
                    out = new ObjectOutputStream(clientSocket.getOutputStream());
                    in = new ObjectInputStream(clientSocket.getInputStream());
                    //get username
                    String userName = (String) in.readObject();
                    System.out.println("User Name: " + userName);
                    synchronized (usernames){
                        usernames.add(userName);
                        broadcastUsernames();
                    }
                    while (true) {
                        try{
                        String command = (String) in.readObject();
                        if (command.equals("GET_MAL_LIST")) {
                            AllAnimeLists.add(getMalList(userName));
                            out.writeObject("Operation Complete");
                            out.flush();
                        }
                        }catch(EOFException e){
                            //this means that no command was sent, its whatever ig, no need to print anything
                        }
                    }

                }
                catch(IOException | ClassNotFoundException e){
                    e.printStackTrace();
                }
        }
            private void broadcastUsernames() throws IOException {
                for (ClientHandler client : clients) {
                    client.out.writeObject(usernames);
                    client.out.flush();
                }
            }
    }
    private List<ImportantInfo> getMalList(String userName) {
            List<Anime> a = importList(GameServer.apiKey, userName);
            List<AnimeResponse> r = getAllOpenings(a);
            return getAsOpenings(r);
    }
        public void StartServer(int port){
            try{
                GameServer server = new GameServer(port);
                server.start();
            }
            catch(IOException e){
                e.printStackTrace();
            }
        }
}
