package tools;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTubeRequestInitializer;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Duration;
import java.util.ArrayList;

public final class YTTool {
    private static final String APPLICATION_NAME = "2E3C Bot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static String API_KEY;
    private static YouTube yt;

    // sets api key and yt object
    public static void setAPIKey(String key) {
        API_KEY = key;

        // Set YouTube from api key.
        try {
            final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            yt = new YouTube.Builder(httpTransport, JSON_FACTORY, new HttpRequestInitializer() {
                @Override
                public void initialize(HttpRequest httpRequest) throws IOException {
                }
            }).setYouTubeRequestInitializer(new YouTubeRequestInitializer(API_KEY))
                    .setApplicationName(APPLICATION_NAME).build();
        } catch (Exception e) {
            System.out.println("Failed to set yt");
            System.out.println(e.getStackTrace());
        }
    }

    // Searches for 5 videos based on search query
    public static ArrayList<ArrayList<String>> search(String query) {
        try {
            // Define and execute the API request
            YouTube.Search.List request = yt.search()
                    .list("snippet");
            SearchListResponse response = request.setMaxResults(5L)
                    .setQ(query)
                    .setType("video")
                    .execute();

            // filter search response for video IDs
            String videoIDs = filterSearchJSON(response.toString());

            // define and execute another request for more video info
            String vidInfo = getinfo(videoIDs);


            // filter video info response for title, duration, and video ID
            ArrayList<ArrayList<String>> out = filterVideoJSON(vidInfo);

            return out;

        } catch (Exception e) {
            return null;
        }
    }

    // Searches for 5 video infos based on video ids
    public static String getinfo(String vidIDs) {
        try {
            // Define and execute the API request
            YouTube.Videos.List request = yt.videos()
                    .list("snippet,contentDetails");
            VideoListResponse response = request.setId(vidIDs).execute();

            return response.toString();

        } catch (Exception e) {
            return null;
        }
    }

    // gets video ids from search
    public static ArrayList<String> getVideoIDs(String s) {
        try {
            ArrayList<String> out = new ArrayList<>();
            String videoID;

            // Get JSON Object
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(s);

            JSONArray ja = (JSONArray) jo.get("items");

            for (Object o : ja) {
                ArrayList<String> vidInfo = new ArrayList<>();

                JSONObject vid = (JSONObject) o;

                // get id then videoid
                JSONObject id = (JSONObject) vid.get("id");
                videoID = id.get("videoId").toString();

                out.add(videoID);
            }

            return out;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // filters search JSON
    public static String filterSearchJSON(String s) {
        try {
            ArrayList<String> out = new ArrayList<>();
            String videoID;

            // Get JSON Object
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(s);
            JSONArray ja = (JSONArray) jo.get("items");

            for (Object o : ja) {
                JSONObject vid = (JSONObject) o;

                // get id then videoid
                JSONObject id = (JSONObject) vid.get("id");
                videoID = id.get("videoId").toString();

                out.add(videoID);
            }

            return String.join(", ", out);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // filters video info JSON
    public static ArrayList<ArrayList<String>> filterVideoJSON(String s) {
        try {
            ArrayList<ArrayList<String>> out = new ArrayList<>();
            String videoID;
            String videoTitle;
            String videoDuration;

            // Get JSON Object
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(s);
            JSONArray ja = (JSONArray) jo.get("items");

            for (Object o : ja) {
                ArrayList<String> vidInfo = new ArrayList<>();

                JSONObject vid = (JSONObject) o;

                // get id then videoid
                videoID = vid.get("id").toString();

                // get snippet then title
                JSONObject snip = (JSONObject) vid.get("snippet");
                videoTitle = snip.get("title").toString();

                // get contentDetails then duration
                JSONObject det = (JSONObject) vid.get("contentDetails");
                videoDuration = det.get("duration").toString();

                vidInfo.add(videoID);
                vidInfo.add(videoTitle);
                String dur = TimeTool.secToString(Duration.parse(videoDuration).getSeconds());
                dur = TimeTool.stripHours(dur);
                vidInfo.add(dur);
                out.add(vidInfo);
            }

            return out;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static String getTitleByID(String videoID) {
//
//        try {
//            YouTube.Videos.List request = yt.videos().list("snippet");
//            VideoListResponse response = request.setId(videoID).execute();
//
//            String responseString = response.toString();
//
//            // Get JSON Object
//            JSONParser parser = new JSONParser();
//            JSONObject jo = (JSONObject) parser.parse(responseString);
//
//            JSONArray ja = (JSONArray) jo.get("items");
//            JSONObject vid = (JSONObject) ja.get(0);
//            JSONObject snip = (JSONObject) vid.get("snippet");
//
//            return snip.get("title").toString();
//
//        } catch (Exception e) {
//            System.out.println("Error fetching Video data");
//        }
//        return "ERROR";
//    }
}