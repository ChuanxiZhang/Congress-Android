package com.linciycuans.homework9.Jason;


/**
 * Created by linciy on 2016/11/17.
 */
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonFunction {
    public static JsonArray getJsonContent(String urlname) {
        try {
            URL url = new URL(urlname);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader read = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputline;
            StringBuffer response = new StringBuffer();
            while ((inputline = read.readLine()) != null) {
                response.append(inputline);
            }
            read.close();
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(response.toString()).getAsJsonObject();
            return jsonObject.get("results").getAsJsonArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}