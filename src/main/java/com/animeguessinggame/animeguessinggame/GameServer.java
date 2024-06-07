package com.animeguessinggame.animeguessinggame;

import com.almasb.fxgl.net.ClientConfig;
import dev.katsute.mal4j.anime.Anime;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import static com.animeguessinggame.animeguessinggame.LoadMALList.*;

public class GameServer {
    private ArrayList<String> usernames = new ArrayList<>();
    private List<ClientHandler> clients = new ArrayList<>();
    private ServerSocket serverSocket;
    public static String apiKey = new String();
    public static ArrayList<List<ImportantInfo>> AllAnimeLists = new ArrayList<>();

    private static ImportantInfo opening;
    private static int currentRound = 0;
    private static int totalRounds = 20;

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
                    }
                    while (true) {
                        try{
                        String command = (String) in.readObject();
                        if (command.equals("GET_MAL_LIST")) {
                            AllAnimeLists.add(getMalList(userName));
                            System.out.println("Server sends Operation Complete to client");
                           // out.writeObject("Operation Complete");
                            out.flush();
                        }
                        else if (command.equals("START_GAME")) {
                            startGame();
                        }else if (command.startsWith("GUESS")) {
                            String guess = command.split(":")[1];
                            validateGuess(userName, guess);
                        }
                        else if (command.equals("GET_USERNAMES")) {
                            sendUsernames();
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
            private void sendUsernames() throws IOException {
                synchronized (usernames) {
                    System.out.println("Server sends String ArrayList usernames to Client");
                    out.writeObject(usernames);
                    out.flush();
                }
            }
    }
    private List<ImportantInfo> getMalList(String userName) {
            List<Anime> a = importList(GameServer.apiKey, userName);
            List<AnimeResponse> r = getAllOpenings(a);
            return getAsOpenings(r);
    }
    private void startGame() throws IOException {
        currentRound = 0;
        nextRound();
    }
    private void nextRound() throws IOException {
        if (currentRound < totalRounds) {
            opening = chooseRandomOpening();
            broadcastOpening(opening);
            currentRound++;
        } else {
            endGame();
        }
    }

    private ImportantInfo chooseRandomOpening(){
            List<ImportantInfo> l = LoadMALList.RandomSelectOpenings(AllAnimeLists, 10);
            int rng = (int)(Math.random() * 10);
            return l.get(rng);
    }

    private void validateGuess(String userName, String guess) throws IOException {
        boolean isCorrect = guess.equalsIgnoreCase(opening.animeTitle);

        for (ClientHandler client : clients) {
            if (client.equals(this)) {
                System.out.println("Server sends String correct or wrong to client");
                client.out.writeObject(isCorrect ? "CORRECT" : "WRONG");
                client.out.flush();
            }
        }
        if (isCorrect) {
            nextRound();
        }
    }




    private void broadcastOpening(ImportantInfo opening) throws IOException {
        for (ClientHandler client : clients) {
            System.out.println("Server sends String OpeningURL to client");
            int rng = (int)(Math.random()*opening.openingList.size());
            client.out.writeObject("OPENING:" + opening.openingList.get(rng).openingURL);
            client.out.flush();
        }
    }
    private void endGame() throws IOException {
        for (ClientHandler client : clients) {
            System.out.println("Server sends String GAME_OVER to client");
            client.out.writeObject("GAME_OVER");
            client.out.flush();
        }
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
