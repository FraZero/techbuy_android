package com.unimol.prova_upload_image.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class PlaceApi {
    public ArrayList<String> autoComplete(String input) {
        ArrayList<String> list = new ArrayList();
        HttpURLConnection connection = null;
        StringBuilder jsonResult = new StringBuilder();
        try {
            StringBuilder builder = new StringBuilder("https://maps.googleapis.com/maps/api/place/autocomplete/json?");
            builder.append("input=" + input );
            builder.append("&key=AIzaSyBrJ3ec9wTuS6L-xHkaXLU8BJbFsx_LZ9o" );
            URL url = new URL(builder.toString());
            connection = (HttpURLConnection) url.openConnection();
            InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream());

            int read;

            char[] buff = new char[1024];
            while ((read=inputStreamReader.read(buff)) !=-1) {
                jsonResult.append(buff,0,read);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException exception) {
            exception.printStackTrace();

        } finally {
            if(connection != null){
                connection.disconnect();
            }
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonResult.toString());
            JSONArray predictions = jsonObject.getJSONArray("predictions");
            for (int i=0; i<predictions.length();i++){
                list.add(predictions.getJSONObject(i).getString("description"));
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
