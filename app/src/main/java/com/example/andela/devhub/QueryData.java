package com.example.andela.devhub;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static com.example.andela.devhub.DevelopersList.LOG_TAG;

/**
 * Created by Ismail on 8/14/2017.
 */

public class QueryData {



    private QueryData(){}

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    public static String makeHttpRequest(String stringURL) throws IOException {
        // Create URL object
        URL formattedURL = createUrl(stringURL);

        String jsonResponse = "";

        if(formattedURL == null)
            return stringURL;

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) formattedURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            if(urlConnection.getResponseCode() == 200)
            {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            }
        } catch (IOException e) {
            // TODO: Handle the exception
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // function must handle java.io.IOException here
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link DeveloperCardModel} object by parsing out information
     * about the first developer from the input developerJSON string.
     */
    public static ArrayList<DeveloperCardModel> extractDevsFromJson(String developerJSON) {

        ArrayList<DeveloperCardModel> devCards = new ArrayList<>();
        try {
            JSONObject baseJsonResponse = new JSONObject(developerJSON);
            JSONArray featureArray = baseJsonResponse.getJSONArray("items");


            // If there are results in the features array
            for (int i = 0; i < featureArray.length(); i++){

                // Extract out the first feature (which is an developer)
                JSONObject userDetail = featureArray.getJSONObject(i);


                // Extract out the title, time, and tsunami values
                String avatarURL = userDetail.getString("avatar_url");
                String userLogin = userDetail.getString("login");
                String userProfileLink = userDetail.getString("html_url");

                devCards.add(new DeveloperCardModel(avatarURL, userLogin, userProfileLink));

            }
            return devCards;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the github (location) JSON results", e);
        }
        return null;
    }
}
