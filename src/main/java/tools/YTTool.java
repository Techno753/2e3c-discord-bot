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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

public class YTTool {
    private static final String APPLICATION_NAME = "2E3C Bot";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static String API_KEY;
    private static YouTube yt;

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

    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public static ArrayList<ArrayList<String>> search(String query) {
        try {
            // Define and execute the API request
            YouTube.Search.List request = yt.search()
                    .list("snippet");
            SearchListResponse response = request.setMaxResults(10L)
                    .setQ(query)
                    .setType("video")
                    .execute();

            ArrayList<ArrayList<String>> filtered = filterJSON(response.toString());

            for (int i = 0; i < filtered.size(); i++) {
                System.out.println((i+1) + " - " + filtered.get(i).get(0));
            }

            return filterJSON(response.toString());
        } catch (Exception e) {
            return null;
        }
    }

    public static ArrayList<ArrayList<String>> filterJSON(String s) {
        try {
            ArrayList<ArrayList<String>> out = new ArrayList<>();
            String title;
            String videoID;

            // Get JSON Object
            JSONParser parser = new JSONParser();
            JSONObject jo = (JSONObject) parser.parse(s);

            JSONArray ja = (JSONArray) jo.get("items");

            for (Object o : ja) {
                ArrayList<String> vidInfo = new ArrayList<>();

                JSONObject vid = (JSONObject) o;

                // get snippet then title
                JSONObject snip = (JSONObject) vid.get("snippet");
                title = snip.get("title").toString();

                // get id then videoid
                JSONObject id = (JSONObject) vid.get("id");
                videoID = id.get("videoId").toString();

                vidInfo.add(title);
                vidInfo.add(videoID);
                out.add(vidInfo);
            }

            return out;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}