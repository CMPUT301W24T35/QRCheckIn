package com.example.qrcheckin;

import android.annotation.SuppressLint;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;


/** ViewEventActivity allows users to view detailed information about an event.
 * Users can view event details, sign up for events, and view announcements related to the event.
 * This activity interacts with Firebase Firestore to retrieve and update event and user information.
 * @see AddAnnouncementFragment creates a fragment for the announcement
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
    TextView promoQRCodeTextViewTitle;
    ImageView promoQRCodeImage;
    private FirebaseFirestore db;
    private ArrayList<Announcement> announcementDataList;
    private ListView announcementList;
    private AnnouncementsAdapter announcementsAdapter;

    private ArrayList<Profile> signedAttendeeDataList;
    private ListView signedAttendeeList;
    private SignedAttendeeAdapter signedAttendeeAdapter;

    private ArrayList<Profile> attendeeDataList;
    private ListView attendeeList;
    private AttendeeAdapter attendeeAdapter;

    private String mainUserID;
    private String eventID;

    ImageButton back;

    @SuppressLint("MissingInflatedId")
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
        back = findViewById(R.id.button_back);
        qrCodeImage = findViewById(R.id.qrCodeImageView);
        promoQRCodeImage = findViewById(R.id.promoqrCodeImageView);
        editEventBtn = findViewById(R.id.editEventButton);
        viewMapBtn = findViewById(R.id.viewMapButton);
        addAnnouncement = findViewById(R.id.button_add_announcement);
        share = findViewById(R.id.button_share);
        promoQRCodeTextViewTitle = findViewById(R.id.promoQRCodeTitle);

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

        if (promoqr!=null){
            Bitmap promoqrBitmap = Helpers.base64ToBitmap(promoqr);
            promoQRCodeImage.setImageBitmap(promoqrBitmap);
        } else {
            promoQRCodeTextViewTitle.setText("No Promo QR Code");

        }


        if (isAttendee()) {
            // If it is an attendee, then hide unnecessary info
            ConstraintLayout eventButtons = findViewById(R.id.eventButtons);
            LinearLayout attendeeInfo = findViewById(R.id.attendeesInfo);
            LinearLayout signedAttendeeInfo = findViewById(R.id.signedAttendeesInfo);
            eventButtons.setVisibility(View.GONE);
            attendeeInfo.setVisibility(View.GONE);
            signedAttendeeInfo.setVisibility(View.GONE);
            addAnnouncement.setVisibility(View.GONE);
            share.setVisibility(View.GONE);
            ConstraintLayout signInBtnArea = findViewById(R.id.signInButtonArea);
            signInBtnArea.setVisibility(View.VISIBLE);
            promoQRCodeTextViewTitle.setVisibility(View.GONE);
            promoQRCodeImage.setVisibility(View.GONE);
        }

        // Edit event
        // TODO Temporary turn this button into back button
        editEventBtn.setText("BACK");
        editEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Create intent to switch to EditEvent Activity.
                //  Temporary serves as back button to homepage
                Intent intent = new Intent(ViewEventActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        // View map
        viewMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Create a map API to view map with attendees - Project Part 4
            }
        });


        // OPEN AI, 2024, ChatGPT, Share Images in Android Studio
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
        getUserID();
        //get the document depending on the eventID
        DocumentReference docRef = db.collection("event").document(eventID);
        //get the users' documents by users' collection
        //DocumentReference userRef = db.collection("users").document(mainUserID);
        //wrapper for storing the attendeeCapacity from firebase
        final int[] attendeeCapacityWrapper = new int[1];
        final int[] attendeeSignUpCount = new int[1];

        // get the attendeeCapacity from firebase
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // Check if the document contains the attendeeCapacity field
                    if (document.exists() && document.contains("attendeeCapacity")) {
                        // Get the value of attendeeCapacity
                        Number tempCapacity = document.getLong("attendeeCapacity"); // Firestore stores numbers as Long by default
                        // deal with the case where attendeeCapacity does not exist
                        if(tempCapacity != null) {
                            attendeeCapacityWrapper[0] = tempCapacity.intValue();
                            Log.d("Firestore", "Attendee Capacity: " + attendeeCapacityWrapper[0]);
                        }
                    } else {
                        Log.d("Firestore", "No such document or field");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    // Check if the document contains the attendeeCapacity field
                    if (document.exists() && document.contains("countSignup")) {
                        // Get the value of attendeeCapacity
                        Number tempCapacity = document.getLong("countSignup"); // Firestore stores numbers as Long by default
                        // deal with the case where attendeeCapacity does not exist
                        if(tempCapacity != null) {
                            attendeeSignUpCount[0] = tempCapacity.intValue();
                            Log.d("Firestore", "countSignup: " + attendeeSignUpCount[0]);
                        }
                    } else {
                        Log.d("Firestore", "No such document or field");
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });
        // Sign up to the event as an attendee
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Reference to the specific event document
                DocumentReference eventRef = db.collection("event").document(eventID);

                eventRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        //  list of user IDs who have signed up
                        List<String> signedUpUsers = (List<String>) documentSnapshot.get("signedUpAttendees");
                        List<String> checkInUsers = (List<String>) documentSnapshot.get("UserIDCheckIn");
                        if (signedUpUsers != null && signedUpUsers.contains(mainUserID)) {
                            // User ID is found in the list, indicating they've already signed up
                            Toast.makeText(ViewEventActivity.this, "You have already signed up for this event!", Toast.LENGTH_LONG).show();
                        } else if(checkInUsers != null && checkInUsers.contains(mainUserID)) {
                            Toast.makeText(ViewEventActivity.this, "You have already checked In for this event!", Toast.LENGTH_LONG).show();
                        }else {
                            // User ID is not in the list - proceed with the sign-up process
                            // Ensure there's capacity for more attendees before proceeding
                            //"capacity wrapper" is the max capacity for that event
                            //"SignUpCount" is the total number of attendeeSignedUp
                            if (attendeeSignUpCount[0] < attendeeCapacityWrapper[0]) {
                                signUpAttendee();
                                signUpCount();
                                Intent intent = new Intent(ViewEventActivity.this, SignedUpEventActivity.class);
                                startActivity(intent);
                            } else {
                                // Event is full
                                Toast.makeText(ViewEventActivity.this, "The event is full. You cannot sign up anymore.", Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        // Handle the case where the event document does not exist
                        Log.d("DEBUG", "Event document does not exist.");
                    }
                }).addOnFailureListener(e -> {
                    Log.e("Error", "Failed to fetch event document", e);
                    // handle the failure case
                });
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewEventActivity.this, HomepageActivity.class);
                startActivity(intent);
            }
        });

        announcementDataList = new ArrayList<>();
        announcementList = findViewById(R.id.announcement_list);
        announcementsAdapter = new AnnouncementsAdapter(this, announcementDataList);
        announcementList.setAdapter(announcementsAdapter);

        showAnnouncement();

        // Add announcement
        addAnnouncement.setOnClickListener(v -> {
            new AddAnnouncementFragment().show(getSupportFragmentManager(), "Add an announcement");
        });

        // TODO : Add attendee array item connect to firebase
        // TODO: Check if capacity has been reached or if there is space
        //  reject signup if capacity reached.
        attendeeDataList = new ArrayList<>();
        attendeeList = findViewById(R.id.attendees_list);
        attendeeAdapter = new AttendeeAdapter(this, attendeeDataList, mainUserID,eventID);
        attendeeList.setAdapter(attendeeAdapter);

        showAttendee();

        signedAttendeeDataList = new ArrayList<>();
        signedAttendeeList = findViewById(R.id.signedAttendees_list);
        signedAttendeeAdapter = new SignedAttendeeAdapter(this, signedAttendeeDataList);
        signedAttendeeList.setAdapter(signedAttendeeAdapter);

        showSignedAttendees();
    }



    @Override
    public void addAnnouncement(Announcement announcement) {
        String message = announcement.getAnnouncement();
        // TODO Get the eventID so that we can store announcements in the Event in firebase
        DocumentReference userRef = db.collection("event").document(eventID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Check if signedUpAttendees field exists
                        if (document.contains("announcements")) {
                            // If it exists, update the array by adding docID
                            userRef.update("announcements", FieldValue.arrayUnion(message))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "Document successfully updated!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w("Firestore", "Error updating document", e);
                                        }
                                    });
                        } else {
                            // If it doesn't exist, create a new array with docID
                            userRef.update("announcements", FieldValue.arrayUnion(message))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "New signedUpAttendees field created and document updated!");
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

        showAnnouncement();
    }

    /**
     *
     */
    public void showAnnouncement(){
        db.collection("event").document(eventID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){
                    Log.e("FirestoreError", "Error getting event details",error);
                    return;
                }
                Log.d("FirestoreSuccess", "Successfully fetched events.");
                announcementDataList.clear();
                if(value.exists()) {
                    ArrayList<String> announcements = (ArrayList<String>) value.get("announcements");
                    if (announcements != null && !announcements.isEmpty()){
                        for (String announcementMsg : announcements) {
                            Announcement announcement = new Announcement(announcementMsg);
                            announcementDataList.add(0, announcement);
                            announcementsAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

    //increment the signUpCount in the firebase by 1
    //reference: StackOverFlow, https://stackoverflow.com/questions/50762923/how-to-increment-existing-number-field-in-cloud-firestore
    public void signUpCount() {
        DocumentReference eventRef = db.collection("event").document(eventID);

        //increments the countSignup field by 1. If the field does not exist, it will be created with the value of 1.
        eventRef.update("countSignup", FieldValue.increment(1))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DEBUG", "countSignup successfully incremented");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("DEBUG", "Error incrementing countSignup", e);

                    }
                });
    }
    public void showAttendee() {
        db.collection("event").document(eventID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){
                    Log.e("FirestoreError", "Error getting event details",error);
                    return;
                }
                Log.d("FirestoreSuccess", "Successfully fetched events.");
                attendeeDataList.clear();
                if(value.exists()) {
                    Map<String, Long> userIDCheckIn = (Map<String, Long>) value.get("userIDCheckIn");
                    if (userIDCheckIn != null && !userIDCheckIn.isEmpty()){
                        for (String attendeeID : userIDCheckIn.keySet()){
                            DocumentReference attendeeRef = db.collection("user").document(attendeeID);
                            attendeeRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot doc) {
                                    if (doc.exists()) {
                                        String attendeeName = doc.getString("name");
                                        String attendeePhone = doc.getString("phone");
                                        String attendeeEmail = doc.getString("email");
                                        String attendeeHomepage = doc.getString("homepage");
                                        Long checkInCount = userIDCheckIn.get(mainUserID);

                                        Profile attendee = new Profile(attendeeName, attendeePhone, attendeeEmail, attendeeHomepage);
                                        attendee.setCheckInCount(checkInCount);
                                        attendeeDataList.add(attendee);
                                    }
                                    attendeeAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    public void showSignedAttendees() {
        db.collection("event").document(eventID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error!= null){
                    Log.e("FirestoreError", "Error getting event details",error);
                    return;
                }
                Log.d("FirestoreSuccess", "Successfully fetched events.");
                signedAttendeeDataList.clear();
                if(value.exists()) {
                    ArrayList<String> attendees = (ArrayList<String>) value.get("signedUpAttendees");
                    if (attendees != null && !attendees.isEmpty()){
                        for (String attendeeID : attendees){
                            DocumentReference attendeeRef = db.collection("user").document(attendeeID);
                            attendeeRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot doc) {
                                    if (doc.exists()) {
                                        String attendeeName = doc.getString("name");
                                        String attendeePhone = doc.getString("phone");
                                        String attendeeEmail = doc.getString("email");
                                        String attendeeHomepage = doc.getString("homepage");
                                        Profile attendee = new Profile(attendeeName, attendeePhone, attendeeEmail, attendeeHomepage);
                                        signedAttendeeDataList.add(attendee);
                                    }
                                    signedAttendeeAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    /**
     * Checks if the current user is an attendee of the event.
     * @return true if the user is an attendee, false otherwise.
     */

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

    /**
     * Signs up the current user as an attendee for the event.
     */
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

    /**
     * Retrieves the user ID of the current user from local storage.
     */
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

    /**
     * Adds the current event to the user's list of signed-up events in their profile.
     */
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
