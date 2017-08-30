package com.example.andela.devhub;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DevelopersList extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<DeveloperCardModel>>{


    private DeveloperCardAdapter developerCardAdapter;
    private GridView gridView;
    private TextView mTextView;
    private ProgressBar mProgress;
    private RelativeLayout relativeLayout;


    private NetworkInfo networkInfo;
    private ConnectivityManager connManager;
    /** Tag for the log messages */
    public static final String LOG_TAG = DevelopersList.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devlist);

        gridView = (GridView) findViewById(R.id.gridview);
        mTextView = (TextView)findViewById(R.id.empty_message);
        mProgress = (ProgressBar)findViewById(R.id.loading_progress);
        relativeLayout = (RelativeLayout) findViewById(R.id.main_view);

        gridView.setEmptyView(mTextView);

        // Create a new adapter that takes an empty list of earthquakes as input
        developerCardAdapter =
                new DeveloperCardAdapter(DevelopersList.this, new ArrayList<DeveloperCardModel>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        gridView.setAdapter(developerCardAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(DevelopersList.this, DeveloperProfile.class);

                DeveloperCardModel devDataObj = developerCardAdapter.getItem(position);

                String[] devData = new String[3];
                devData[0] = devDataObj.getAvaterResourceId();
                devData[1] = devDataObj.getUsername();
                devData[2] = devDataObj.getProfileLink();

                intent.putExtra("Dev Data", devData);

                startActivity(intent);
            }
        });

        connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        networkInfo  = connManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()){
            getLoaderManager().initLoader(0, null, this);
        }else{
                mProgress.setVisibility(View.GONE);
            final String noInternetMessage = "No internet connection. Connect then Tab";
            mTextView.setText(noInternetMessage);
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    networkInfo  = connManager.getActiveNetworkInfo();

                    if(networkInfo != null && networkInfo.isConnected()){

                        mProgress.setVisibility(View.VISIBLE);
                        mTextView.setVisibility(View.GONE);
                        getLoaderManager().restartLoader(0, null, DevelopersList.this);
                    }else{
                        mProgress.setVisibility(View.GONE);
                        mTextView.setText(noInternetMessage);
                    }
                }
            });
        }

    }

    @Override
    public Loader<List<DeveloperCardModel>> onCreateLoader(int i, Bundle bundle) {
        return new DevhubAsyncLoader(DevelopersList.this);
    }

    @Override
    public void onLoadFinished(Loader<List<DeveloperCardModel>> loader, List<DeveloperCardModel> data) {
        Log.v("DeveloperList", "Log Message from onLoadFinished()");
        // Clear the adapter of previous earthquake data
        developerCardAdapter.clear();

        mProgress.setVisibility(View.GONE);
        // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            developerCardAdapter.addAll(data);
        }else{
            String emptyMessage = "No Dev found!";
            mTextView.setText(emptyMessage);
        }


    }


    @Override
    public void onLoaderReset(Loader<List<DeveloperCardModel>> loader) {

        Log.v("DeveloperList", "Log Message from onLoaderReset()");
        developerCardAdapter.clear();
    }



}
