package com.example.andela.devhub;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class DeveloperProfile extends AppCompatActivity {


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
        profileLinkTextView.setText(devData[2]);


        userName = userNameTextView.getText().toString();
        profileLink = profileLinkTextView.getText().toString();

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String shareMessage = String.format(
                        "Check out this awesome developer @%s, %s.", userName,
                        profileLink);
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                shareIntent.setType("text/plain");

                Intent chooser= Intent.createChooser(shareIntent, "Share");

                if (shareIntent.resolveActivity(getPackageManager()) != null){
                    startActivity(chooser);
                }

            }
        });

        profileLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri profileURI = Uri.parse(profileLink);
                Intent profileViewIntent = new Intent(Intent.ACTION_VIEW, profileURI);

                startActivity(profileViewIntent);

            }
        });

    }


}
