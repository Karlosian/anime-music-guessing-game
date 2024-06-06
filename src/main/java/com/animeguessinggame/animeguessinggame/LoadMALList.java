package com.animeguessinggame.animeguessinggame;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.katsute.mal4j.MyAnimeList;
import dev.katsute.mal4j.anime.Anime;
import dev.katsute.mal4j.anime.AnimeListStatus;
import dev.katsute.mal4j.anime.property.AnimeStatus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.*;

import static dev.katsute.mal4j.property.ExperimentalFeature.OP_ED_THEMES;

public class LoadMALList {
    public static Scanner scanner = new Scanner(System.in);

    public static List<Anime> importList(String cID, String name) {
        MyAnimeList mal = MyAnimeList.withClientID(cID);
        int num = 0;
        mal.enableExperimentalFeature(OP_ED_THEMES);

        //code taken from MyAnimeList forums
        List<Anime> animeList = new ArrayList<Anime>();
        boolean check = true;
        while (check) {
            List<AnimeListStatus> query = mal.getUserAnimeListing(name).withStatus(AnimeStatus.Completed).withOffset(num).search();
            if (query.size() != 10) {
                num += query.size();
                query.forEach(entry -> animeList.add(entry.getAnime()));

                System.out.println("Importing Complete");
                check = false;
            }
            num += 10;
            query.forEach(entry -> animeList.add(entry.getAnime()));
            System.out.println("Importing: " + num);
        }
        return animeList;
    }

    public static List<AnimeResponse> getAllOpenings(List<Anime> animeList) {
        ObjectMapper mapper = new ObjectMapper();

        List<AnimeResponse> openingsList = new ArrayList<AnimeResponse>();
        Set<String> processedAnimeIds = new HashSet<>();

        for(Anime anime : animeList){
            if (processedAnimeIds.contains(anime.getID().toString())) {
                continue; // Skip if this anime has already been processed
            }
            String id = anime.getID().toString();
            String apiURL = "https://api.animethemes.moe/anime?filter%5Bhas%5D=resources&filter%5Bsite%5D=MyAnimeList&filter%5Bexternal_id%5D=" + id + "&include=animethemes.animethemeentries.videos%2Canimethemes.song&page%5Bnumber%5D=1";
            try{
                URL url = new URL(apiURL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                AnimeResponse response = mapper.readValue(conn.getInputStream(), AnimeResponse.class);
                openingsList.add(response);
                processedAnimeIds.add(id);
                System.out.println("LoadMALList.getAllOpenings: " + anime.getTitle() + " added to list");
            }catch (IOException e) {
                System.out.println("Anime " + anime.getTitle() +  " has no opening(s) ");
            }
            try {
                Thread.sleep(700); //implement 700ms delay between requests due to rate limiting
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Thread was interrupted, failed to complete operation");
            }
        }
        return openingsList;
    }

    public static List<ImportantInfo> getAsOpenings(List<AnimeResponse> animeList){
        List<ImportantInfo> goodList = new ArrayList<ImportantInfo>();
        for(AnimeResponse ar : animeList){
            if (ar == null || ar.getAnime(true) == null) {
                continue; // Skip if ar or the anime is null
            }
            ImportantInfo i = new ImportantInfo();
            i.animeTitle = ar.getAnime(true).getName();
            i.openingList = new ArrayList<OpeningInfo>();
            List<AnimeTheme> animeThemes = ar.getAnime(true).getAnimethemes();
            if (animeThemes == null) {
                continue; // Skip if animeThemes is null
            }
            for (AnimeTheme at : ar.getAnime(true).getAnimethemes()) {
                if (at.getType().equals("OP")) {
                    OpeningInfo oi = new OpeningInfo();
                    oi.openingURL = at.getAnimethemeentries().get(0).getVideos().get(0).getLink();
                    oi.openingTitle = at.getSong().getTitle();
                    i.openingList.add(oi);
                }
            }
            goodList.add(i);
        }
        return goodList;
    }

    public static List<ImportantInfo> RandomSelectOpenings(ArrayList<List<ImportantInfo>> listOfLists, int numberOfOpenings){
        List<ImportantInfo> randomized = new ArrayList<ImportantInfo>();
        for (int i = 0; i< listOfLists.size(); i++){
            for(int j = 0; j<(numberOfOpenings/listOfLists.size()); j++){
                int elementsInUserList = listOfLists.get(i).size();
                int rng = (int)(Math.random() * (elementsInUserList));
                ImportantInfo chosenOP = listOfLists.get(i).get(rng);
                //check if chosen anime has OP links (if not then it is invalid and will be skipped)
                try{if(chosenOP.openingList.get(0).openingURL != null){
                    randomized.add(chosenOP);
                }
                else{
                    j--;
                }}
                catch (IndexOutOfBoundsException e ){
                    j--;
                }
            }
        }
        Collections.shuffle(randomized);
        return randomized;
    }
}