package com.example.andela.devhub;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static com.example.andela.devhub.DevelopersList.LOG_TAG;

/**
 * Created by Ismail on 8/14/2017.
 */

public class DevhubAsyncLoader extends AsyncTaskLoader<List<DeveloperCardModel>> {


    /** URL to query the github dataset for developers information  */
    private static final String GITHUB_REQUEST_URL = "https://api.github.com/search/users?q=location:lagos";
    private List<DeveloperCardModel> mData;

    public DevhubAsyncLoader(Context context)
    {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            // Use cached data
            deliverResult(mData);
        } else {
            // We have no data, so kick off loading it
            forceLoad();
        }

        Log.v("DevhubAsyncLoader", "Log Message from onStartLoading()");
    }

    @Override
    public List<DeveloperCardModel> loadInBackground()
    {

        Log.v("DevhubAsyncLoader", "Log Message from loadInBackground()");
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = QueryData.makeHttpRequest(GITHUB_REQUEST_URL);
        } catch (IOException e) {
            // TODO Handle the IOException
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        List<DeveloperCardModel> result = QueryData.extractDevsFromJson(jsonResponse);

        Collections.shuffle(result);

        Log.v(LOG_TAG, result != null ? result.toString() : null);
        // Return the {@link Event} object as the result fo the {@link DevhubAsyncTask}
        return result;
    }

    @Override
    public void deliverResult(List<DeveloperCardModel> data) {
        // We’ll save the data for later retrieval
        mData = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        super.deliverResult(data);
    }
}
