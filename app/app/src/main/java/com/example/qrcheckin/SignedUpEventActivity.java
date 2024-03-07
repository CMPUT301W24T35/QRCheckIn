package com.example.qrcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
* Allow user to sign up event
*/

public class SignedUpEventActivity extends AppCompatActivity {

    Button home;
    Button checkIn;
    ImageButton notification;

    CircleImageView profile;

    private ArrayList<Event> dataList;
    private ListView eventList;
    private SignedUpEventAdapter eventAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signed_up_events);

        home = findViewById(R.id.button_home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //feel free to use the code below to connect to the activity
                Intent intent = new Intent(SignedUpEventActivity.this, HomepageActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });
        //click the check in button
        checkIn = findViewById(R.id.button_check_in);
        checkIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignedUpEventActivity.this, QRScannerActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });

        profile = findViewById(R.id.profile_image_button);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignedUpEventActivity.this, ProfileActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });


        notification = findViewById(R.id.button_notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(HomePageActivity.this, notifyActivity.class);// go to event activity need to connect with other activity
                // startActivity(intent);
            }
        });
        //TODO:Add array item connect to firebase
        dataList = new ArrayList<>();
        eventList = findViewById(R.id.event_list);
        eventAdapter = new SignedUpEventAdapter(this, dataList);
        eventList.setAdapter(eventAdapter);




    }

}
