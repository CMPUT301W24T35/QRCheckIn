package com.example.qrcheckin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomepageActivity extends AppCompatActivity {

    Button organizeEvent;
    Button signedUp;
    ImageButton notification;
    ImageButton local;
    CircleImageView profile;

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;


    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        //click the organizeEvent button
        organizeEvent = findViewById(R.id.button_organize_events);
        organizeEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //feel free to use the code below to connect to the activity
                Intent intent = new Intent(HomepageActivity.this, HomepageOrganizer.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });
        //click the signedUp button
        signedUp = findViewById(R.id.button_signed_up_events);
        signedUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageActivity.this, SignedUpEventActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });

        profile = findViewById(R.id.profile_image_button);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageActivity.this, ProfileActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });


        local = findViewById(R.id.button_location);
        local.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(HomePageActivity.this, localActivity.class);// go to event activity need to connect with other activity
                //startActivity(intent);
            }
        });
        notification = findViewById(R.id.button_notification);
        notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomepageActivity.this, NotificationActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });

        dataList = new ArrayList<>();
        eventList = findViewById(R.id.event_list);
        Date startTime = new Date(); // Current time
        // Assuming the event ends in 2 hours from the current time


        eventAdapter = new EventArrayAdapter(this, dataList);
        eventList.setAdapter(eventAdapter);

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked event
                //Event clickedEvent = dataList.get(position);

                // Create an Intent to start the new activity
                //Intent intent = new Intent(MainActivity.this, eventDetail.class); //

                // Pass data to the eventDetail activity
                //intent.putExtra("eventName", clickedEvent.getName()); // get name
                //intent.putExtra("organizerName", clickedEvent.getOrganizerID()); // And a getOrganizerName method
                //intent.putExtra("startTime", clickedEvent.getStartTime());
                //intent.putExtra("endTime", clickedEvent.getEndTime());
                //intent.putExtra("announcement", clickedEvent.getAnnouncement());
                // Add other event details as needed

                // Start the
                //startActivity(intent);
            }
        });


    }
}