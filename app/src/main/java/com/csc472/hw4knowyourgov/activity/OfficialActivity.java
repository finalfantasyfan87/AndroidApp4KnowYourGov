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
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.csc472.hw4knowyourgov.R;
import com.csc472.hw4knowyourgov.model.Official;
import com.squareup.picasso.Picasso;

import static android.net.Uri.parse;

public class OfficialActivity extends AppCompatActivity {
    private static final String TAG = "OfficialActivity";

    private Official official;
    private String location;
    private TextView office;
    private TextView name;
    private TextView party;
    private TextView address;
    private TextView phone;
    private TextView email;
    private TextView website;
    private TextView header;
    private ImageButton picture;
    private ImageButton facebook;
    private ImageButton twitter;
    private ImageButton youtube;
    private ImageButton googleplus;
    private ConstraintLayout appBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.official_activity);
        header = findViewById(R.id.location);
        office = findViewById(R.id.office);
        name = findViewById(R.id.name);
        party = findViewById(R.id.politicalParty);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        website = findViewById(R.id.website);
        picture = findViewById(R.id.pic);
        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        youtube = findViewById(R.id.youtube);
        googleplus = findViewById(R.id.googlePlus);
        appBackground = findViewById(R.id.appBackground);

        Intent intent = getIntent();
        if(intent.hasExtra("heading")){
            location = intent.getStringExtra("heading");
            header.setText(location);
        }
        if(intent.hasExtra("official")){
            official = (Official) intent.getSerializableExtra("official");
            mapOfficialFields(official);
            getOfficalPicture();
        }
    }

    private void mapOfficialFields(Official official){
        office.setText(official.getOffice());
        name.setText(official.getName());
        party.setText("("+official.getParty()+")");
        if(official.getAddress()==null){
            address.setText("No Address Provided");
        } else {
            address.setText(official.getAddress());
            Linkify.addLinks(address, Linkify.MAP_ADDRESSES);
            address.setLinkTextColor(Color.WHITE);
        }
        if(official.getPhone()==null){
            phone.setText("No Phone Provided");
        } else {
            phone.setText(official.getPhone());
            Linkify.addLinks(phone, Linkify.PHONE_NUMBERS);
            phone.setLinkTextColor(Color.WHITE);
        }
        if(official.getEmail()==null){
            email.setText("No Email Provided");
        } else {
            email.setText(official.getEmail());
            Linkify.addLinks(email, Linkify.EMAIL_ADDRESSES);
            email.setLinkTextColor(Color.WHITE);
        }
        if(official.getUrl()==null){
            website.setText("No Website Provided");
        } else {
            website.setText(official.getUrl());
            Linkify.addLinks(website, Linkify.WEB_URLS);
            website.setLinkTextColor(Color.WHITE);
        }
        if(official.getFacebook()==null){
            facebook.setVisibility(View.INVISIBLE);
            facebook.setClickable(false);
        }
        if(official.getTwitter()==null){
            twitter.setVisibility(View.INVISIBLE);
            twitter.setClickable(false);
        }
        if(official.getGooglePlus()==null){
            googleplus.setVisibility(View.INVISIBLE);
            googleplus.setClickable(false);
        }
        if(official.getYouTube()==null){
            youtube.setVisibility(View.INVISIBLE);
            youtube.setClickable(false);
        }
        if(official.getParty().contains("Republican")){
            appBackground.setBackgroundColor(Color.RED);
        } else if(official.getParty().contains("Democrat") || official.getParty().contains("Democratic")){
            appBackground.setBackgroundColor(Color.BLUE);
        } else {
            appBackground.setBackgroundColor(Color.BLACK);
        }
    }

    private void getOfficalPicture(){
        if (official.getPhotoUrl() != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = official.getPhotoUrl().replace("http:", "https:");
                    picasso.load(changedUrl)
                            .fit()
                            .centerInside()
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(picture);
                }
            }).build();
            picasso.load(official.getPhotoUrl())
                    .fit()
                    .centerInside()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(picture);
        } else {
            Picasso.get().load(R.drawable.brokenimage)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(picture);
        }
    }

    public void openPhotoActivity(View v){
        if(official.getPhotoUrl()!=null || !official.getPhotoUrl().isEmpty()){
            Intent photo = new Intent(this, PhotoActivity.class);
            photo.putExtra("official",official.toString());
            photo.putExtra("header",header.getText().toString());
            startActivity(photo);
        } else{
            Log.d(TAG, "There is no data to post in the Photo Activity.");
        }
    }

    public void onFacebookClick(View v){
        String FACEBOOK_URL = "https://www.facebook.com/" + official.getFacebook();
        String urlToUse;
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else {
                urlToUse = "fb://page/" + official.getFacebook();
            }
        } catch (PackageManager.NameNotFoundException e) {
            urlToUse = FACEBOOK_URL;
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(parse(urlToUse));
        startActivity(facebookIntent);
    }

    public void onTwitterClick(View v){
        Intent intent = null;
        String name = official.getTwitter();
        try {
            // Twitter App
            getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, parse("twitter://user?screen_name=" + name));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (Exception e) {
            // Twitter Browser
            intent = new Intent(Intent.ACTION_VIEW, parse("https://twitter.com/" + name));
        }
        startActivity(intent);
    }

    public void onGooglePlusClick(View v){
        String name = official.getGooglePlus();
        Intent intent;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", name);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    parse("https://plus.google.com/" + name)));
        }
    }

    public void onYoutubeClick(View v){
        String name = official.getYouTube();
        Intent intent;
        try {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.youtube");
            intent.setData(parse("https://www.youtube.com/" + name));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    parse("https://www.youtube.com/" + name)));
        }
    }
}