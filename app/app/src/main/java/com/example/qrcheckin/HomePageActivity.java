package com.example.qrcheckin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class HomePageActivity extends AppCompatActivity {

    Button MyEvents;
    Button CheckIn;
    Button notifi;
    Button local;
    Button profile;

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        //click the MyEvent button
        MyEvents = findViewById(R.id.button_events);
        MyEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, myEventActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });
        //click the CheckIn button
        CheckIn = findViewById(R.id.button_checkIn);
        CheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, checkInActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });
        profile = findViewById(R.id.profile_image_button);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, profileActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });
        local = findViewById(R.id.local_button);
        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, localActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });
        notifi = findViewById(R.id.notifiy_button);
        notifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomePageActivity.this, notifyActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });
    }
}