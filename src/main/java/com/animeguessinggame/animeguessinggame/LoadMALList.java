package com.animeguessinggame.animeguessinggame;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.katsute.mal4j.MyAnimeList;
import dev.katsute.mal4j.anime.Anime;
import dev.katsute.mal4j.anime.AnimeListStatus;
import dev.katsute.mal4j.anime.property.AnimeStatus;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import java.util.List;
import java.util.Vector;

import static dev.katsute.mal4j.property.ExperimentalFeature.OP_ED_THEMES;


public class LoadMALList {
    public static Scanner scanner = new Scanner(System.in);
    public static Vector<Anime> importList(String cID, String name) {
        MyAnimeList mal = MyAnimeList.withClientID(cID);
        int num = 0;
        mal.enableExperimentalFeature(OP_ED_THEMES);

        //code taken from MyAnimeList forums
        Vector<Anime> animeList = new Vector<Anime>();
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
    public static Vector<AnimeResponse> getAllOpenings(Vector<Anime> animeList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        Vector<AnimeResponse> openingsList = new Vector<AnimeResponse>();
        for(Anime anime : animeList){
            String id = anime.getID().toString();
            String apiURL = "https://api.animethemes.moe/anime?filter%5Bhas%5D=resources&filter%5Bsite%5D=MyAnimeList&filter%5Bexternal_id%5D=" + id + "&include=animethemes.animethemeentries.videos%2Canimethemes.song&page%5Bnumber%5D=1";
            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            AnimeResponse response = mapper.readValue(conn.getInputStream(), AnimeResponse.class);
            openingsList.add(response);
            System.out.println("LoadMALList.getAllOpenings: " + anime.getTitle() + " added to list");
        }
        return openingsList;
    }

}
