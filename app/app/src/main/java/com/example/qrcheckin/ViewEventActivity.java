package com.example.qrcheckin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
/**
* Allow user to view the event information
*/

public class ViewEventActivity extends AppCompatActivity implements AddAnnouncementFragment.AddAnnouncementDialogListener {

    ImageView posterImage;
    TextView eventName;
    TextView eventDescription;
    TextView eventStartTime;
    TextView eventEndTime;
    TextView eventLocation;
    Button editEventBtn;
    Button viewMapBtn;
    Button signUpButton;
    ImageButton share;
    ImageButton addAnnouncement;
    ImageView qrCodeImage;
    private FirebaseFirestore db;
    private ArrayList<Announcement> announcementDataList;
    private ListView announcementList;
    private AnnouncementsAdapter announcementsAdapter;

    private ArrayList<Profile> attendeeDataList;
    private ListView attendeeList;
    private AttendeeAdapter attendeeAdapter;

    private String mainUserID;
    private String eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        posterImage = findViewById(R.id.posterImageView);
        eventDescription = findViewById(R.id.eventDescriptionText);
        eventStartTime = findViewById(R.id.eventStartText);
        eventEndTime = findViewById(R.id.eventEndText);
        eventLocation = findViewById(R.id.eventLocationText);
        eventName = findViewById(R.id.viewEventTitle);

        signUpButton = findViewById(R.id.signUpButton);
        qrCodeImage = findViewById(R.id.qrCodeImageView);
        editEventBtn = findViewById(R.id.editEventButton);
        viewMapBtn = findViewById(R.id.viewMapButton);
        addAnnouncement = findViewById(R.id.button_add_announcement);
        share = findViewById(R.id.button_share);

        Intent intent = getIntent();
        String name = intent.getStringExtra("eventName");
        String eventDes = intent.getStringExtra("eventDes");
        String startTime = intent.getStringExtra("startTime");
        String endTime = intent.getStringExtra("endTime");
        String location = intent.getStringExtra("location");
        String qr = intent.getStringExtra("qr");
        String promoqr = intent.getStringExtra("promoqr");
        String poster = intent.getStringExtra("poster");
        // Rehash the relevant fields to get eventID
        eventID = Helpers.createDocID(name, startTime, location);
        Log.d("DEBUG", "eventID " + eventID);

        // Get Firebase
        db = FirebaseFirestore.getInstance();


        eventDescription.setText(eventDes);
        eventStartTime.setText(startTime);
        eventEndTime.setText(endTime);
        eventLocation.setText(location);
        eventName.setText(name);

        if (poster!=null){
            Bitmap posterBitmap = Helpers.base64ToBitmap(poster);
            posterImage.setImageBitmap(posterBitmap);
        }

        if (qr!=null){
            Bitmap qrBitmap = Helpers.base64ToBitmap(qr);
            qrCodeImage.setImageBitmap(qrBitmap);
        }


        if (isAttendee()) {
            // If it is an attendee, then hide unnecessary info
            ConstraintLayout eventButtons = findViewById(R.id.eventButtons);
            LinearLayout attendeeInfo = findViewById(R.id.attendeesInfo);
            eventButtons.setVisibility(View.GONE);
            attendeeInfo.setVisibility(View.GONE);
            addAnnouncement.setVisibility(View.GONE);
            share.setVisibility(View.GONE);
            ConstraintLayout signInBtnArea = findViewById(R.id.signInButtonArea);
            signInBtnArea.setVisibility(View.VISIBLE);
        }

        // Edit event
        editEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Create intent to switch to EditEvent Activity.
            }
        });

        // View map
        viewMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Create a map API to view map with attendees - Project Part 4
            }
        });


        // Share QR code
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Get the QR code of the event from firebase to the variable "bitmap"
                BitmapDrawable drawable = (BitmapDrawable) qrCodeImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "QR Code", null);
                Uri imageUri = Uri.parse(path);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            }
        });


        // Sign up to the event as an attendee
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUpAttendee();

                Intent intent = new Intent(ViewEventActivity.this, SignedUpEventActivity.class);

                Log.d("DEBUG", "intent created: " + intent);
                startActivity(intent);
            }
        });

        announcementDataList = new ArrayList<>();
        announcementList = findViewById(R.id.announcement_list);
        announcementsAdapter = new AnnouncementsAdapter(this, announcementDataList);
        announcementList.setAdapter(announcementsAdapter);

        // Add announcement
        addAnnouncement.setOnClickListener(v -> {
            new AddAnnouncementFragment().show(getSupportFragmentManager(), "Add an announcement");
        });

        // TODO : Add attendee array item connect to firebase
        // TODO: Check if capacity has been reached or if there is space
        //  reject signup if capacity reached.
        attendeeDataList = new ArrayList<>();
        attendeeList = findViewById(R.id.attendees_list);
        attendeeAdapter = new AttendeeAdapter(this, attendeeDataList);
        attendeeList.setAdapter(attendeeAdapter);
    }



    @Override
    public void addAnnouncement(Announcement announcement) {
        String message = announcement.getAnnouncement();
        // TODO Get the eventID so that we can store announcements in the Event in firebase
        announcementDataList.add(0, announcement);
        announcementsAdapter.notifyDataSetChanged();
    }
    public boolean isAttendee() {
        // Checks if the request for this page is coming from an attendee or an organizer
        /*
        Add the following wherever we create an intent to come to this page
        * Intent intent = new Intent(Activity1.this,Activity2.class);
          intent.putExtra("origin","organiser"); // or attendee if you're running it from activity3
          startActivity(intent);
        * */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle!=null){
            String origin = bundle.getString("origin");
            if(origin!=null && origin.equals("organiser")){
                //from organiser
                return false;
            }
            if(origin!=null && origin.equals("attendee")){
                //from attendee
                return true;
            }
        }
        return false;
    }

    public void signUpAttendee(){

        getUserID();

        DocumentReference eventRef = db.collection("event").document(eventID);
        eventRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Check if signedUpAttendees field exists
                        if (document.contains("signedUpAttendees")) {
                            // If it exists, update the array by adding docID
                            eventRef.update("signedUpAttendees", FieldValue.arrayUnion(mainUserID))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "Document successfully updated!");
                                            addToSignedUpEventsInProfile();
                                            Toast.makeText(ViewEventActivity.this, "You signed up!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error updating document", e);
                                        }
                                    });
                        } else {
                            // If it doesn't exist, create a new array with docID
                            eventRef.update("signedUpAttendees", FieldValue.arrayUnion(mainUserID))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "New signedUpAttendees field created and document updated!");
                                            addToSignedUpEventsInProfile();
                                            Toast.makeText(ViewEventActivity.this, "You signed up!", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error updating document", e);
                                        }
                                    });
                        }
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });


    }

    public void getUserID(){
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
    }

    public void addToSignedUpEventsInProfile(){
        // Update user's document with signed up events
        DocumentReference userRef = db.collection("user").document(mainUserID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Check if signedUpAttendees field exists
                        if (document.contains("signedUpEvents")) {
                            // If it exists, update the array by adding docID
                            userRef.update("signedUpEvents", FieldValue.arrayUnion(eventID))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "User document successfully updated!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error updating User document", e);
                                        }
                                    });
                        } else {
                            // If it doesn't exist, create a new array with docID
                            userRef.update("signedUpEvents", FieldValue.arrayUnion(eventID))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "New signedUpEvents field created and document updated!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error updating user document", e);
                                        }
                                    });
                        }
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });
    }
}