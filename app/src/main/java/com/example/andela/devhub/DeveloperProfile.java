package com.example.andela.devhub;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DeveloperProfile extends AppCompatActivity {


    final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";


    private CustomTabsClient mCustomTabsClient;
    private CustomTabsSession mCustomTabsSession;
    private CustomTabsServiceConnection mCustomTabsServiceConnection;
    private CustomTabsIntent mCustomTabsIntent;
    private CustomTabActivityHelper mCustomTabActivityHelper;

    private ImageButton shareButton;
    private TextView profileLinkTextView;
    private TextView userNameTextView;
    private ImageView profileImageView;
    private String userName;
    private String profileLink;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devprofile);


        mCustomTabActivityHelper = new CustomTabActivityHelper();

        profileImageView = (ImageView) findViewById(R.id.github_profile_pic);
        shareButton = (ImageButton) findViewById(R.id.share_icon);
        profileLinkTextView = (TextView) findViewById(R.id.github_user_profile);
        userNameTextView = (TextView) findViewById(R.id.github_username);

        String[] devData = getIntent().getStringArrayExtra("Dev Data");
        if (devData == null) {
            return;
        }

        // do something with the data
        Picasso.with(this).load(devData[0]).into(profileImageView);
        userNameTextView.setText(devData[1]);

        SpannableString content = new SpannableString(devData[2]);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        profileLinkTextView.setText(content);

        userName = userNameTextView.getText().toString();
        profileLink = profileLinkTextView.getText().toString();

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shareMessage = String.format(
                        "Check out this awesome developer @%s, %s .", userName,
                        profileLink);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                shareIntent.setType("text/plain");

                Intent chooser= Intent.createChooser(shareIntent, "Share with");

                if (shareIntent.resolveActivity(getPackageManager()) != null){
                    startActivity(chooser);
                }

            }
        });

        profileLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri profileURI = Uri.parse(profileLink);

                openCustomChromeTab(profileURI);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCustomTabActivityHelper.bindCustomTabsService(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCustomTabActivityHelper.unbindCustomTabsService(this);
    }
//
//    /**
//     * Creates a pending intent to send a broadcast to the {@link ChromeTabActionBroadcastReceiver}
//     * @param actionSource
//     * @return
//     */
//    private PendingIntent createPendingIntent(int actionSource) {
//        Intent actionIntent = new Intent(this, ChromeTabActionBroadcastReceiver.class);
//        actionIntent.putExtra(ChromeTabActionBroadcastReceiver.KEY_ACTION_SOURCE, actionSource);
//        return PendingIntent.getBroadcast(this, actionSource, actionIntent, 0);
//    }


    /**
     * Handles opening the url in a custom chrome tab
     * @param uri
     */
    private void openCustomChromeTab(Uri uri) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        // set toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

//        // add menu items
//        intentBuilder.addMenuItem("Menu 1",
//                createPendingIntent(ChromeTabActionBroadcastReceiver.ACTION_MENU_ITEM_1));
//        intentBuilder.addMenuItem("Menu 2",
//                createPendingIntent(ChromeTabActionBroadcastReceiver.ACTION_MENU_ITEM_2));

//        // set action button
//        intentBuilder.setActionButton(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), "Action Button",
//                createPendingIntent(ChromeTabActionBroadcastReceiver.ACTION_ACTION_BUTTON));

        // set start and exit animations
        intentBuilder.setStartAnimations(this, R.anim.slide_in_right, R.anim.slide_out_left);
        intentBuilder.setExitAnimations(this, android.R.anim.slide_in_left,
                android.R.anim.slide_out_right);

        // build custom tabs intent
        CustomTabsIntent customTabsIntent = intentBuilder.build();

        // call helper to open custom tab
        CustomTabActivityHelper.openCustomTab(this, customTabsIntent, uri, new CustomTabActivityHelper.CustomTabFallback() {
            @Override
            public void openUri(Activity activity, Uri uri) {
                // fall back, call open open webview
                openWebView(uri);
            }
        });


    }
    /**
     * Handles opening the url in a webview
     * @param uri
     */
    private void openWebView(Uri uri) {
        Intent webViewIntent = new Intent(this, WebViewActivity.class);
        webViewIntent.putExtra(WebViewActivity.EXTRA_URL, uri.toString());
        startActivity(webViewIntent);
    }
}
