package com.example.qrcheckin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomepageOrganizer extends AppCompatActivity {

    CircleImageView profile;
    Button createEvent;
    Button back;
    private FirebaseFirestore db;
    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;
    String mainUserID;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_organizer);

        db = FirebaseFirestore.getInstance();

        getEvent();

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
                Intent intent = new Intent(HomepageOrganizer.this, CreateEventActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
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
                Event clickedEvent = dataList.get(position);

                // Create an Intent to start the new activity
                Intent intent = new Intent(HomepageOrganizer.this, ViewEventActivity.class);

                // Pass data to the eventDetail activity
                intent.putExtra("eventName", clickedEvent.getName()); // get name
                //intent.putExtra("organizerName", clickedEvent.getOrganizerID()); // And a getOrganizerName method
                intent.putExtra("startTime", clickedEvent.getStartTime());
                intent.putExtra("endTime", clickedEvent.getEndTime());
                intent.putExtra("eventDes", clickedEvent.getDescription());
                intent.putExtra("location", clickedEvent.getLocation());
                intent.putExtra("poster",clickedEvent.getPoster());
                intent.putExtra("qr",clickedEvent.getQrCode());
                intent.putExtra("promoqr",clickedEvent.getPromoQR());
                intent.putExtra("origin", "organiser");

                // Add other event details as needed

                // Start the
                startActivity(intent);
            }
        });
    }

    private void getEvent() {

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
                    ArrayList<String> eventIds = (ArrayList<String>) value.get("organizedEvent");
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