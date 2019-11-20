package com.csc472.hw4knowyourgov.activity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.csc472.hw4knowyourgov.R;
import com.csc472.hw4knowyourgov.model.Official;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    private static final String TAG = "OfficialActivity";
    private final String DEFAULT = "Data Not Available";

    private Official official;
    private TextView locationTextView;

    private TextView officeTextView;
    private TextView nameTextView;
    private TextView politicalParty;

    private TextView addressTextView;
    private TextView phoneTextView;
    private TextView emailTextView;
    private TextView urlTextView;

    private ImageView photo;
    private ImageView googlePlus;
    private ImageView facebook;
    private ImageView twitter;
    private ImageView youTube;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.official_activity);
        locationTextView = findViewById(R.id.location);
        officeTextView = findViewById(R.id.office);
        nameTextView = findViewById(R.id.name);
        politicalParty = findViewById(R.id.party);
        addressTextView = findViewById(R.id.address);
        phoneTextView = findViewById(R.id.phone);
        emailTextView = findViewById(R.id.email);
        urlTextView = findViewById(R.id.url);

        photo = findViewById(R.id.photo);
        googlePlus = findViewById(R.id.googlePlus);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        youTube = findViewById(R.id.youTube);


        Intent intent = getIntent();

        if (intent.hasExtra("locationTextView")) {
            locationTextView.setText(intent.getStringExtra("locationTextView"));
        }

        if (intent.hasExtra("official")) {
            official = (Official) intent.getSerializableExtra("official");

            officeTextView.setText(official != null ? official.getOffice() : null);
            nameTextView.setText(official.getName());

            politicalParty.setText(String.format("(%s)", official.getParty()));
            if (official.getParty().equals("Democratic"))
                getWindow().getDecorView().setBackgroundColor(Color.BLUE);
            else if (official.getParty().equals("Republican"))
                getWindow().getDecorView().setBackgroundColor(Color.RED);
            else {
                if (official.getParty().equals("Unknown"))
                    politicalParty.setVisibility(View.INVISIBLE);

                getWindow().getDecorView().setBackgroundColor(Color.BLACK);
            }

            addressTextView.setText(official.getAddress());
            Linkify.addLinks(addressTextView, Linkify.MAP_ADDRESSES);

            phoneTextView.setText(official.getPhone());
            Linkify.addLinks(phoneTextView, Linkify.PHONE_NUMBERS);

            emailTextView.setText(official.getEmail());
            Linkify.addLinks(emailTextView, Linkify.EMAIL_ADDRESSES);

            urlTextView.setText(official.getUrl());
            Linkify.addLinks(urlTextView, Linkify.WEB_URLS);


            final String photoUrl = official.getPhotoUrl();
            if (photoUrl != null) {
                Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        final String changedUrl = photoUrl.replace("http:", "https:");
                        picasso.load(changedUrl)
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(photo);
                    }
                }).build();
                picasso.load(photoUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(photo);
            } else {
                Picasso.get().load(photoUrl)
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.missing)
                        .into(photo);
            }

            if (official.getGooglePlus().equals(DEFAULT))
                googlePlus.setVisibility(View.INVISIBLE);
            if (official.getFacebook().equals(DEFAULT))
                facebook.setVisibility(View.INVISIBLE);
            if (official.getTwitter().equals(DEFAULT))
                twitter.setVisibility(View.INVISIBLE);
            if (official.getYouTube().equals(DEFAULT))
                youTube.setVisibility(View.INVISIBLE);
        }
    }



    public void openPhotoActivity(View v) {
        Log.d(TAG, "openPhotoActivity: ");
        if (official.getPhotoUrl().equals(DEFAULT))
            return;
        Intent intent = new Intent(OfficialActivity.this, PhotoDetailActivity.class);
        intent.putExtra("addressTextView", locationTextView.getText().toString());
        intent.putExtra("official", String.valueOf(official));
        startActivity(intent);
    }



    public void googlePlusClicked(View v) {
        String name = official.getGooglePlus();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus", "com.google.android.apps.plus.phoneTextView.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://plus.google.com/" + name)));
        }
    }



    public void onFacebookClicked(View v) {
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getFacebook();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                urlToUse = "fb://page/" + official.getFacebook();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL; //normal web urlTextView
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void onTwitterClicked(View v) {
        Intent intent = null;
        String name = official.getTwitter();
        try {
            // get the Twitter app if possible
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }



    public void onYoutubeClicked(View v) {
        String name = official.getYouTube();
        Intent intent = null;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(Uri.parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.youtube.com/" + name)));
        }
    }
}
