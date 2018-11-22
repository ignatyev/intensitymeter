package sample;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.deploy.net.BasicHttpRequest;
import com.sun.deploy.net.HttpRequest;
import com.sun.deploy.net.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;

public class Stats {
    public static final String HTTPS_API_CROSSOVER_COM_API_IDENTITY_USERS_CURRENT_DETAIL =
            "https://api.crossover.com/api/identity/users/current/detail";
    public static final String AUTH = null;
    public static final String API_WORKDIARIES = "https://api.crossover.com/api/timetracking/workdiaries?assignmentId=%s&date=%s&timeZoneId=%s";
    static HttpRequest request = new BasicHttpRequest();

    static <T> T sendRequest(String url, Class<T> cls) {
        T jsonObject = null;
        try {
            HttpResponse response = request.doGetRequest(
                    new URL(url),
                    new String[]{"Authorization"},
                    new String[]{AUTH});

            jsonObject = new Gson().fromJson(readResponse(response), cls);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static List<IntensityDrop> get() {
        JsonObject details = sendRequest(HTTPS_API_CROSSOVER_COM_API_IDENTITY_USERS_CURRENT_DETAIL, JsonObject.class);
        String assignmentId = details.get("assignment").getAsJsonObject().get("id").getAsString();
        String timezoneId = details.get("location").getAsJsonObject().get("timeZone").getAsJsonObject().get("id").getAsString();
        String date = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        JsonArray diaries = sendRequest(String.format(API_WORKDIARIES, assignmentId, date, timezoneId), JsonArray.class);
        List<IntensityDrop> intensityDropDropsDates = StreamSupport.stream(diaries.spliterator(), true)
                .filter(el -> el.getAsJsonObject().get("intensityScore").getAsInt() < Main.INTENSITY_THRESHOLD)
                .map(el -> new IntensityDrop(
                        el.getAsJsonObject().get("date").toString(),
                        el.getAsJsonObject().get("windowTitle").toString(),
                        el.getAsJsonObject().get("intensityScore").toString()))
                .collect(Collectors.toList());

        System.out.println(intensityDropDropsDates);
        return intensityDropDropsDates;
    }

    private static String readResponse(HttpResponse response) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader in = new BufferedReader(
                new InputStreamReader(new GZIPInputStream(response.getInputStream())))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        }
        return content.toString();
    }
}
