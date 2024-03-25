package com.example.qrcheckin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class BrowseProfileAdmin extends AppCompatActivity {

    private FirebaseFirestore db;

    // Main content
    private ArrayList<Profile> dataList;
    private ListView eventList;
    private ProfileArrayAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse_profile_admin);

        db = FirebaseFirestore.getInstance();

        // Reference to your collection
        CollectionReference collectionRef = db.collection("event");

        getProfile();


        dataList = new ArrayList<>();
        eventList = findViewById(R.id.event_list);
        Date startTime = new Date(); // Current time
        // Assuming the event ends in 2 hours from the current time


        eventAdapter = new ProfileArrayAdapter(this, dataList);
        eventList.setAdapter(eventAdapter);


        // Query to retrieve all documents
        collectionRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                // Check if documents exist
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Iterate through documents
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        // Extract document ID
                        String documentId = document.getId();
                        // Do something with the document ID
                        Log.d("Document ID", documentId);
                    }
                } else {
                    Log.d("TAG", "No documents found in collection");
                }
            }
        });


        eventList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked event
                Profile clickedEvent = dataList.get(position);



                // Create an Intent to start the new activity
                Intent intent = new Intent(BrowseProfileAdmin.this, AdminViewProfileActivity.class);

                // Pass data to the eventDetail activity
                intent.putExtra("profileName", clickedEvent.getUserName()); // get name
                intent.putExtra("profileEmail", clickedEvent.getEmail());
                intent.putExtra("profilePhone", clickedEvent.getPhone());

                Log.d("phone", "Phone no: " + clickedEvent.getPhone());




                // Add other event details as needed

                // Start the
                startActivity(intent);
            }
        });

    }

    private void getProfile() {
        db.collection("user").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){
                    Log.e("FirestoreError", "Error getting event details",error);
                    return;
                }
                Log.d("FirestoreSuccess", "HomepageActivity Successfully fetched events.");
                dataList.clear();

                assert value != null;
                for(QueryDocumentSnapshot doc: value){
                    String profileName = doc.getString("name");
                    String profileEmail = doc.getString("email");
                    String profilePhone = doc.getString("phone");

                    Log.d("-->phone", "Phone no: " + profilePhone);

                    Profile profile = new Profile(profileName, profilePhone, profileEmail, "Null");

                    dataList.add(profile);
                }
                eventAdapter.notifyDataSetChanged();
            }
        });
    }
}