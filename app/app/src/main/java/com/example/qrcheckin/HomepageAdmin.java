package com.example.qrcheckin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

/**
 * This class initializes the homepage for admin
 */
public class HomepageAdmin extends AppCompatActivity {
    private FirebaseFirestore db;

    private ArrayList<Event> dataList;
    private ListView eventList;
    private EventArrayAdapter eventAdapter;
    String mainUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage_admin);

        db = FirebaseFirestore.getInstance();

        getEvent();

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

        if (mainUserID !=null){
            fetchDetails(mainUserID);
        }

        dataList = new ArrayList<>();
        eventList = findViewById(R.id.event_list);

        eventAdapter = new EventArrayAdapter(this, dataList);
        eventList.setAdapter(eventAdapter);

        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event clickedEvent = dataList.get(position);

                Intent intent = new Intent(HomepageAdmin.this, ViewEventActivity.class);

                intent.putExtra("eventName", clickedEvent.getName());
                intent.putExtra("startTime", clickedEvent.getStartTime());
                intent.putExtra("endTime", clickedEvent.getEndTime());
                intent.putExtra("eventDes", clickedEvent.getDescription());
                intent.putExtra("location", clickedEvent.getLocation());
                intent.putExtra("poster",clickedEvent.getPoster());
                intent.putExtra("qr",clickedEvent.getQrCode());
                intent.putExtra("promoqr",clickedEvent.getPromoQR());
                intent.putExtra("origin", "attendee");

                startActivity(intent);
            }
        });
    }

    private void fetchDetails(String uID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").document(uID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                // You can add your desired logic here if needed
            }
            else{
                Log.e("ProfileActivity", "No such document");
            }
        }).addOnFailureListener(error->{
            Log.e("ProfileActivity", "Error fetching document", error);
        });
    }

    private void getEvent() {
        db.collection("event").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){
                    Log.e("FirestoreError", "Error getting event details",error);
                    return;
                }
                Log.d("FirestoreSuccess", "Successfully fetched events.");
                dataList.clear();

                if (value != null) {
                    for(QueryDocumentSnapshot doc: value){
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
                        if (promoqr != null) {
                            event.setPromoQR(promoqr);
                        }
                        dataList.add(event);
                    }
                }
                eventAdapter.notifyDataSetChanged();
            }
        });
    }
}

