package com.animeguessinggame.animeguessinggame;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.katsute.mal4j.MyAnimeList;
import dev.katsute.mal4j.anime.Anime;
import dev.katsute.mal4j.anime.AnimeListStatus;
import dev.katsute.mal4j.anime.property.AnimeStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

import static dev.katsute.mal4j.property.ExperimentalFeature.OP_ED_THEMES;


public class LoadMALList {
    // Fetches the List of anime from MAL api
    public static List<Anime> importList(String cID, String name) {
        MyAnimeList mal = MyAnimeList.withClientID(cID);
        mal.enableExperimentalFeature(OP_ED_THEMES);
        int num = 0; // Count how many animes where imported

        //code taken from MyAnimeList forums
        List<Anime> animeList = new ArrayList<>();
        boolean check = true;
        while (check) {
            List<AnimeListStatus> query = mal.getUserAnimeListing(name).withStatus(AnimeStatus.Completed).withOffset(num).search();
            if (query.size() != 10) {
                query.forEach(entry -> animeList.add(entry.getAnime()));
                num += query.size();

                System.out.println("Importing Complete");
                check = false;
            }
            // Queues through all anime list and adds to animeList
            query.forEach(entry -> animeList.add(entry.getAnime()));
            num += 10;

            // Prints how many animes were imported
            System.out.println("Importing: " + num);
        }
        return animeList;
    }

    // Processes the openings with the anime IDs and adds it to openingList
    public static List<AnimeResponse> getAllOpenings(List<Anime> animeList) {
        ObjectMapper mapper = new ObjectMapper();

        // Creates a list to store the openings
        List<AnimeResponse> openingsList = new ArrayList<AnimeResponse>();
        Set<String> processedAnimeIds = new HashSet<>();

        // Iterates through the items in animeList
        for(Anime anime : animeList) {
            // Skip if this anime has already been processed
            if (processedAnimeIds.contains(anime.getID().toString())) continue;

            // Finds the id of the anime to search for its video url in MAL website
            String id = anime.getID().toString();
            String apiURL = "https://api.animethemes.moe/anime?filter%5Bhas%5D=resources&filter%5Bsite%5D=MyAnimeList&filter%5Bexternal_id%5D=" + id + "&include=animethemes.animethemeentries.videos%2Canimethemes.song&page%5Bnumber%5D=1";

            // Searches for the Video url
            try {
                // Accesses MAL website
                URL url = new URL(apiURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Gets the response and adds it to openingsList
                AnimeResponse response = mapper.readValue(conn.getInputStream(), AnimeResponse.class);
                openingsList.add(response);

                // Add the id to the processed id list
                processedAnimeIds.add(id);
                System.out.println("LoadMALList.getAllOpenings: " + anime.getTitle() + " added to list");
            } catch (IOException e) {
                System.out.println("Anime " + anime.getTitle() +  " has no opening(s) ");
            }

            // Implement 700ms delay between requests due to rate limiting set by the MAL API
            try {Thread.sleep(700);}

            // Returns error if thread stops between the delays
            catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted, failed to complete operation");
            }
        }

        // Returns all the openingLists to be used in GameController
        return openingsList;
    }
    public static String getOpeningURL(String id){
        ObjectMapper mapper = new ObjectMapper();
        String apiURL = "https://api.animethemes.moe/anime?filter%5Bhas%5D=resources&filter%5Bsite%5D=MyAnimeList&filter%5Bexternal_id%5D=" + id + "&include=animethemes.animethemeentries.videos%2Canimethemes.song&page%5Bnumber%5D=1";
        String openingURL;
        AnimeResponse a = new AnimeResponse();
        // Searches for the Video url
        try {
            // Accesses AnimeThemes website
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            String responseString = new BufferedReader(new InputStreamReader(conn.getInputStream()))
                    .lines().collect(Collectors.joining("\n"));
            System.out.println(responseString);


            // Gets the response and adds it to openingsList
            AnimeResponse response = mapper.readValue(responseString, AnimeResponse.class);
            //no opening video link
            if(!(responseString.contains("OP1.webm"))){
                return "null";
            }
            a = response;

            System.out.println("LoadMALList.getOpeningURL: " + id + " added to list");
        } catch (IOException e) {
            System.out.println("Anime " + id + " has no opening(s) ");
            return "null";
        }
        //get random opening url
        int rng;
        while(true){
        rng = (int)(Math.random()*a.getAnime().get(0).getAnimethemes().size());
            if(a.getAnime().get(0).getAnimethemes().get(rng).getAnimethemeentries().get(0).getVideos().get(0).getLink().contains("-OP")){
                openingURL = a.getAnime().get(0).getAnimethemes().get(rng).getAnimethemeentries().get(0).getVideos().get(0).getLink();
                break;
            }
        }
        return openingURL;
    }

    // Filters the anime that actually have openings/endings and sort them into the OpeningInfo class
    public static List<ImportantInfo> getAsOpenings(List<AnimeResponse> animeList){
        List<ImportantInfo> goodList = new ArrayList<ImportantInfo>();
        for(AnimeResponse ar : animeList){
            // Skip if ar or the anime is null
            if (ar == null || ar.getAnime(true) == null) continue;

            // Formats the anime response using
            ImportantInfo i = new ImportantInfo();
            i.animeTitle = ar.getAnime(true).getName();
            i.openingList = new ArrayList<OpeningInfo>();

            // Creates list of all anime themes to iterate
            List<AnimeTheme> animeThemes = ar.getAnime(true).getAnimethemes();
            if (animeThemes == null) continue; // Skip if animeThemes is null

            // Convert AnimeResponse information to OpeningInfo
            for (AnimeTheme at : animeThemes) {
                if (at.getType().equals("OP")) {
                    OpeningInfo oi = new OpeningInfo();
                    oi.openingURL = at.getAnimethemeentries().get(0).getVideos().get(0).getLink();
                    oi.openingTitle = at.getSong().getTitle();
                    i.openingList.add(oi); // Adds all the openings of an anime to the ImportantInfo (one per anime)
                }
            }

            // Adds all the anime (which include all their openings) in the filtered list
            goodList.add(i);
        }
        return goodList;
    }


    // Shuffles a set amount of anime taken from a user's MAL library
    public static List<ImportantInfo> RandomSelectOpenings(ArrayList<List<ImportantInfo>> listOfLists, int numberOfOpenings){
        List<ImportantInfo> randomized = new ArrayList<>();

        for (int i = 0; i < listOfLists.size(); i++){
        for (int j = 0; j < (numberOfOpenings/listOfLists.size()); j++){
            // Finds size of the anime library of the user
            int elementsInUserList = listOfLists.get(i).size();
            // Chooses a random anime amongst that library
            int rng = (int)(Math.random() * (elementsInUserList));

            // Check if chosen anime has OP links (if not then it is invalid and will be skipped)
            ImportantInfo chosenOP = listOfLists.get(i).get(rng);
            try {
                if (chosenOP.openingList.get(0).openingURL != null) randomized.add(chosenOP);
                else j--;
            }
            catch (IndexOutOfBoundsException e){ j--;}
        }}

        // Shuffles the openings collected from the user
        Collections.shuffle(randomized);
        return randomized;
    }
}