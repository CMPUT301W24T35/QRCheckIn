package com.example.qrcheckin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Firebase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
    private FirebaseFirestore db;

    private String mainUserID;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signed_up_events);

        db = FirebaseFirestore.getInstance();

        getSignedUpEvents();


        //TODO Populate Array List with all signed up events
        //  Signed up events need to be got from the profile
        //  Therefore need to add an array list of signed up events
        //  Once got the signed up events then need to query the database
        //  for the event name

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

        dataList = new ArrayList<>();
        eventList = findViewById(R.id.event_list);
        eventAdapter = new SignedUpEventAdapter(this, dataList);
        eventList.setAdapter(eventAdapter);




    }


    private void getSignedUpEvents() {

        try {
            FileInputStream fis = openFileInput("localStorage.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            mainUserID = sb.toString();
            Log.d("Main USER ID", mainUserID);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.collection("user").document(mainUserID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){
                    Log.e("FirestoreError", "Error getting event details",error);
                    return;
                }
                Log.d("FirestoreSuccess", "Successfully fetched events.");
                dataList.clear();

                if(value.exists()) {
                    ArrayList<String> eventIds = (ArrayList<String>) value.get("signedUpEvents");
                    if (eventIds != null && !eventIds.isEmpty()){
                        for (String eventId : eventIds){
                            // Get reference to the Event document using eventId
                            DocumentReference eventRef = db.collection("event").document(eventId);
                            eventRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot doc) {
                                    if (doc.exists()) {
                                        String eventName = doc.getString("eventName");
                                        String eventDes = doc.getString("eventDescription");
                                        String startTime = doc.getString("startTime");
                                        String endTime = doc.getString("endTime");
                                        String location = doc.getString("location");
                                        String poster = doc.getString("poster");
                                        String qr = doc.getString("checkinQRCode");
                                        String promoqr = doc.getString("promoQRCode");

                                        Event event = new Event();
                                        event.setName(eventName);
                                        event.setDescription(eventDes);
                                        event.setStartTime(startTime);
                                        event.setEndTime(endTime);
                                        event.setLocation(location);
                                        event.setPoster(poster);
                                        event.setQrCode(qr);
                                        if (promoqr!= null){
                                            event.setPromoQR(promoqr);
                                        }
                                        dataList.add(event);
                                    }
                                    eventAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }
        });
    }

}

