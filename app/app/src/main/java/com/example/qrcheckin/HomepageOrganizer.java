package com.example.qrcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import de.hdodenhof.circleimageview.CircleImageView;

/**
* This is a class that initializes the organizer's homepage view elements
*/ 

public class HomepageOrganizer extends AppCompatActivity {

    CircleImageView profile;
    Button createEvent;
    Button back;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_organizer);


        profile = findViewById(R.id.profile_image_button);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageOrganizer.this, ProfileActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });
        createEvent = findViewById(R.id.button_create_event);
        createEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(HomepageOrganizer.this, CreateEvent.class);// go to event activity need to connect with other activity
                //startActivity(intent);
            }
        });
        back = findViewById(R.id.button_back_events);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageOrganizer.this, HomepageActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });
    }
}
