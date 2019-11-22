package com.csc472.hw4knowyourgov.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.csc472.hw4knowyourgov.R;
import com.csc472.hw4knowyourgov.model.Official;
import com.squareup.picasso.Picasso;

public class PhotoActivity extends AppCompatActivity {

    private TextView office;
    private TextView name;
    private TextView header;
    private ImageView pic;

    private ImageView logo;

    private ConstraintLayout background;
    private Official official;
    private String location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);
        header = findViewById(R.id.location);
        office = findViewById(R.id.office);
        name = findViewById(R.id.name);
        pic = findViewById(R.id.pic);
        background = findViewById(R.id.background);


        Intent intent = getIntent();
        if(intent.hasExtra("header")){
            location = intent.getStringExtra("header");
            header.setText(location);
        }
        if(intent.hasExtra("official")){
            official = (Official) intent.getSerializableExtra("official");
            mapDataFields();
            displayOfficialPic();
        }
    }



    private void displayOfficialPic(){
        final String officialPhotoUrl = official.getPhotoUrl();
        if (officialPhotoUrl != null) {
            Picasso picasso = new Picasso.Builder(this).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    final String changedUrl = officialPhotoUrl.replace("http:", "https:");
                    picasso.load(changedUrl)
                            .fit()
                            .centerInside()
                            .error(R.drawable.brokenimage)
                            .placeholder(R.drawable.placeholder)
                            .into(pic);
                }
            }).build();
            picasso.load(officialPhotoUrl)
                    .fit()
                    .centerInside()
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(pic);
        } else {
            Picasso.get().load(R.drawable.brokenimage)
                    .error(R.drawable.brokenimage)
                    .placeholder(R.drawable.placeholder)
                    .into(pic);
        }

    }


    private void mapDataFields(){
        office.setText(official.getOffice());
        name.setText(official.getName());
        if(official.getParty().contains("Republican")){
            background.setBackgroundColor(Color.RED);
        } else if(official.getParty().contains("Democrat") || official.getParty().contains("Democratic")){
            background.setBackgroundColor(Color.BLUE);
        } else {
            background.setBackgroundColor(Color.BLACK);
        }
    }
}
