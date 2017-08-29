package com.example.andela.devhub;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

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

public class DevelopersList extends AppCompatActivity {

    private ArrayList<DeveloperCardModel> devCards;
    private DeveloperCardAdapter developerCardAdapter;
    private GridView gridView;

    /** Tag for the log messages */
    public static final String LOG_TAG = DevelopersList.class.getSimpleName();

    /** URL to query the USGS dataset for earthquake information */
    private static final String USGS_REQUEST_URL = "https://api.github.com/search/users?q=location:lagos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devlist);

        devCards = new ArrayList<>();

        // Kick off an {@link AsyncTask} to perform the network request
        DevhubAsyncTask task = new DevhubAsyncTask();
        task.execute();




    }


    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the first earthquake in the response.
     */
    private class DevhubAsyncTask extends AsyncTask<URL, Void, ArrayList<DeveloperCardModel> >{

        @Override
        protected ArrayList<DeveloperCardModel> doInBackground(URL... urls) {
            // Create URL object
            URL url = createUrl(USGS_REQUEST_URL);

            // Perform HTTP request to the URL and receive a JSON response back
            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                // TODO Handle the IOException
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object
            // Return the {@link Event} object as the result fo the {@link DevhubAsyncTask}
            return extractFeatureFromJson(jsonResponse);
        }

        /**
         * Update the screen with the given earthquake (which was the result of the
         * {@link DevhubAsyncTask}).
         */
        @Override
        protected void onPostExecute(ArrayList<DeveloperCardModel> devCard) {

            setContentView(R.layout.activity_devlist);
            if (devCard == null) {
                return;
            }

            developerCardAdapter = new DeveloperCardAdapter(DevelopersList.this, devCards);

            gridView = (GridView) findViewById(R.id.gridview);

            gridView.setAdapter(developerCardAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                    Intent intent = new Intent(DevelopersList.this, DeveloperProfile.class);

                    DeveloperCardModel devDataObj = devCards.get(position);

                    String[] devData = new String[3];
                    devData[0] = devDataObj.getAvaterResourceId();
                    devData[1] = devDataObj.getUsername();
                    devData[2] = devDataObj.getProfileLink();

                    intent.putExtra("Dev Data", devData);

                    startActivity(intent);
                }
            });
        }

        /**
         * Returns new URL object from the given string URL.
         */
        private URL createUrl(String stringUrl) {
            URL url = null;
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
        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";

            if(url == null)
                return jsonResponse;

            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
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
        private String readFromStream(InputStream inputStream) throws IOException {
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



        @Override
        protected void onPreExecute() {
            setContentView(R.layout.startup_progress_bar);
        }

        /**
         * Return an {@link DeveloperCardModel} object by parsing out information
         * about the first earthquake from the input earthquakeJSON string.
         */
        private ArrayList<DeveloperCardModel> extractFeatureFromJson(String earthquakeJSON) {

            try {
                JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);
                JSONArray featureArray = baseJsonResponse.getJSONArray("items");


                // If there are results in the features array
                for (int i = 0; i < featureArray.length(); i++){

                    // Extract out the first feature (which is an earthquake)
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
}
