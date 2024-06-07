package com.animeguessinggame.animeguessinggame;

import com.almasb.fxgl.net.ClientConfig;
import dev.katsute.mal4j.anime.Anime;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

import static com.animeguessinggame.animeguessinggame.LoadMALList.*;
import static java.lang.Integer.parseInt;

public class GameServer {
    private ArrayList<String> usernames = new ArrayList<>();
    private List<ClientHandler> clients = new ArrayList<>();
    private ServerSocket serverSocket;
    //AtomicInteger used to keep program thread safe
    private AtomicInteger playerCounter = new AtomicInteger(0);
    private AtomicInteger readyForNextRound = new AtomicInteger(0);
    // Lock for thread safety
    private final ReentrantLock lock = new ReentrantLock();

    private int finishedFirstRound = 0;
    public static Map<Integer, String> scoreUsers = new HashMap<Integer, String>();
    public static String apiKey = new String();
    public static ArrayList<List<ImportantInfo>> AllAnimeLists = new ArrayList<>();

    private static ImportantInfo opening;
    private static int currentRound = 0;
    private static int totalRounds = 20;

    public GameServer(int port) throws IOException {
            serverSocket = new ServerSocket(port);
            System.out.println("Server started on port " + port);
    }

    // Receives the clients that join the server
    public void start(){
        while(true){
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected");

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                clientHandler.start();
            }
            catch (IOException e) {throw new RuntimeException(e);}
        }
    }

    public static ArrayList<String> removeDuplicates(ArrayList<String> list)
    {
        ArrayList<String> newList = new ArrayList<String>();
        for (String element : list) {
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }
        return newList;
    }

    private void handleScoreReturn(String scoreReturn){
       String removeStart = scoreReturn.split(":", 2 )[1];
       int scoreNum = parseInt(removeStart.split(" ", 2)[0]);
       String userName = removeStart.split(" ", 2)[1];
       scoreUsers.put(scoreNum, userName);

       scoreUsers = sortLeaderboard(scoreUsers);
    }

    private class ClientHandler extends Thread {
        private Socket clientSocket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        //for sending score info
        private int score;
        private String username;

        public ClientHandler(Socket socket) {
                this.clientSocket = socket;
            }

        public void run() {
            try {
                // out = send info to the client, in = get info from the client
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                in = new ObjectInputStream(clientSocket.getInputStream());

                // Get username of the client that just joined
                String userName = (String) in.readObject();
                System.out.println("User Name: " + userName);

                //
                synchronized (usernames){
                    usernames.add(userName);
                }


                while (true) {
                    try{
                        // Command from client
                        String command = (String) in.readObject();

                        // Gets command from client and runs program accordingly
                        switch (command) {
                            case "GET_MAL_LIST" :
                                AllAnimeLists.add(getMalList(userName));
                                System.out.println("Server sends Operation Complete to client");
                                // out.writeObject("Operation Complete");
                                out.flush(); break;
                            case "START_GAME" :
                                startGame(); break;
                            case "GET_USERNAMES":
                                sendUsernames(); break;
                            case "GET_ANIME_NAMES":
                                sendAnimeNames(); break;
                            case "GET_SCORES_MAP":
                                broadcastScoresMap(); break;
                            default:
                                if (command.startsWith("NEXT_SONG")) {
                                    clientReadyForNextRound();
                                }
                                // Not included messages
                        }
                    }
                    catch(EOFException e){
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

        //send a list of all the anime names from all clients to all client
        private void sendAnimeNames() throws IOException{

            ArrayList<String> animeTitles = new ArrayList<>();
            for(List<ImportantInfo> lists: AllAnimeLists ){
                for(ImportantInfo i: lists){
                    animeTitles.add(i.animeTitle);
                }
            }
            //remove duplicates
            animeTitles = removeDuplicates(animeTitles);
            System.out.println("Server sends String ArrayList animeTitles to Client");
            out.writeObject(animeTitles);
            out.flush();
        }
    }

    private List<ImportantInfo> getMalList(String userName) {
        List<Anime> a = importList(GameServer.apiKey, userName);
        List<AnimeResponse> r = getAllOpenings(a);
        return getAsOpenings(r);
    }

    // Starts new game at round 0
    private void startGame() throws IOException {
        lock.lock();
        try {
            playerCounter.incrementAndGet();
            System.out.println("PlayerCounter: " + playerCounter);
            System.out.println("Needed size: " + AllAnimeLists.size());
            //If all players pressed start game, start the game
            if (playerCounter.get() == AllAnimeLists.size()) {
                currentRound = 0;
                nextRound();
            }
        }
        finally{
            lock.unlock();
        }
    }

    // Informs the other clients to start new round
    private void nextRound() throws IOException {
        lock.lock();
        try {
            readyForNextRound.set(0);
            //make sure all players want to go to the next round before going to the next round
                if (currentRound < totalRounds) {
                    opening = chooseRandomOpening();
                    broadcastOpening(opening);
                    currentRound++;
                } else {
                    endGame();
                }
        }
        finally{
            lock.unlock();
        }
    }

    private void clientReadyForNextRound() throws IOException {
        lock.lock();
        try {
            int readyCount = readyForNextRound.incrementAndGet();
            if (readyCount == clients.size()) {
                nextRound();
            }
        } finally {
            lock.unlock();
        }
    }

    // Selects a random opening using the random opening selector from LoadMALList.java
    private ImportantInfo chooseRandomOpening(){
        return LoadMALList.RandomSelectOpenings(AllAnimeLists, 10).get((int)(Math.random() * 10));
    }




    // Sends new opening for new rounds to the clients
    private void broadcastOpening(ImportantInfo opening) throws IOException {
        int rng = (int)(Math.random()*opening.openingList.size());
        for (ClientHandler client : clients) {
            System.out.println("Server sends String OpeningURL to client");
            client.out.writeObject("OPENING:" + opening.openingList.get(rng).openingURL + " " + opening.animeTitle);
            client.out.flush();
        }
    }
    private void broadcastScoresMap() throws IOException {
        finishedFirstRound++;
        if(finishedFirstRound == clients.size()){
        for (ClientHandler client : clients) {
            System.out.println("Server sends Map scoreUsers to client");
            client.out.writeObject(scoreUsers);
            client.out.flush();
            finishedFirstRound = 0;
        }
        scoreUsers.clear();
        }
    }

    // Sends end game message to the clients
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
