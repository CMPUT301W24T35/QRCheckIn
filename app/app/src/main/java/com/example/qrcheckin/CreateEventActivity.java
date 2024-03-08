package com.example.qrcheckin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/**
* This class is responsible for creating new events
*/
public class CreateEventActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    boolean isDBConnected;
    EditText newEventName;
    EditText newEventDescription;
    EditText newStartTime;
    EditText newEndTime;
    EditText newLocation;
    EditText newAttendeeCapacity;
    Button continueButton;
    Button editPosterImageButton;
    CheckBox generatePromoQRCodeCheckbox;
    boolean needPromoQRCode;

    int attendeeCapacity;
    ImageView posterImage;
    Bitmap posterImageBitmap;
    String posterImageBase64;
    Bitmap promoCodeBitmap;
    String promoCodeBase64;
    String mainUserID;

    Bundle bundle;

    String docID;

    // TODO For now QR Code generated here
    //  Decide whether to delete this workaround later

    String checkinQRCodeBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);


        // Bind UI
        newEventName = findViewById(R.id.eventNameEditText);
        newEventDescription = findViewById(R.id.eventDescriptionEditText);
        newStartTime = findViewById(R.id.eventStartTimeEditText);
        newEndTime = findViewById(R.id.eventEndTimeEditText);
        newLocation = findViewById(R.id.eventLocationEditText);
        continueButton = findViewById(R.id.continueCreateEventButton);
        editPosterImageButton = findViewById(R.id.editPosterImageButton);
        posterImage = findViewById(R.id.posterImageView);
        newAttendeeCapacity = findViewById(R.id.attendeeCapacityEditText);
        generatePromoQRCodeCheckbox = findViewById(R.id.checkboxGeneratePromoQRCode);
        db = FirebaseFirestore.getInstance();

        // TODO
        //  1. Display poster
        //  2. Assign poster to variable
        //  3. Convert poster to bitmap

        // Registers a photo picker activity launcher in single-select mode.
        // Source: https://developer.android.com/training/data-storage/shared/photopicker#select-single-item
        ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
                registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                    // Callback is invoked after the user selects a media item or closes the
                    // photo picker.
                    if (uri != null) {
                        Log.d("PhotoPicker", "Selected URI: " + uri);
                        // Inserts Poster image into ImageView
                        Glide.with(this)
                                .load(uri)
                                .into(posterImage);
                        try {
                            posterImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            posterImageBase64 = Helpers.bitmapToBase64(posterImageBitmap);
                        } catch (IOException e) {
                            Log.d("PhotoPicker", "Unable to convert to base64");
                        }

                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });


        editPosterImageButton.setOnClickListener(v->{
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
            }
        );

        continueButton.setOnClickListener(v -> startNextActivity());
    }

    private void startNextActivity() {
        if (isTextEditInputEmpty()) {
            addEvent();
        }
    }

    private void addEvent() {
        Bundle bundle = new Bundle();
        HashMap<String, Object> data = new HashMap<>();

        String eventName = newEventName.getText().toString();
        String eventDescription = newEventDescription.getText().toString();
        String startTime = newStartTime.getText().toString();
        String endTime = newEndTime.getText().toString();
        String location = newLocation.getText().toString();
        docID = Helpers.createDocID(eventName, startTime, location);
        String attendeeCapacityString = newAttendeeCapacity.getText().toString();
        Log.d("DEBUG", "attendeeCapacityString: " + attendeeCapacityString);


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

        checkPromoCodeAndGenerate();

        // TODO Temporary workaround solution
        //     Decide if we want to keep this workflow later
        generateQRCodeAndSetString();

        Log.d("DEBUG", "docID in CreateEventActivity: " + docID);
        // TODO profileID to organizerID

        data.put("eventName", eventName);
        //data.put("OrganiserID", mainUserID);
        data.put("eventDescription", eventDescription);
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        data.put("location", location);
        data.put("poster", posterImageBase64);
        data.put("checkinQRCode", checkinQRCodeBase64);


        // OPTIONAL FIELDS
        // If promo code was generated then add it to Firebase bundle
        if (promoCodeBase64 != null) {
            Log.d("DEBUG", "promocode: " + promoCodeBase64);
            data.put("promoQRCode", promoCodeBase64);
        }

        if (!attendeeCapacityString.isEmpty()) {
            // Convert to integer and package for database
            Integer attendeeCapacity = Integer.parseInt(attendeeCapacityString);
            data.put("attendeeCapacity", attendeeCapacity);
            Log.d("DEBUG", "AttendeeCapacity: " + attendeeCapacityString);

        }



        // TODO - only pass relevant bundle info for QR Code
        bundle.putString("eventName", eventName);
        bundle.putString("eventDescription", eventDescription);
        bundle.putString("startTime", startTime);
        bundle.putString("endTime", endTime);
        bundle.putString("location", location);
        bundle.putString("eventID", docID);

        DocumentReference userRef = db.collection("user").document(mainUserID);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Check if organizedEvent field exists
                        if (document.contains("organizedEvent")) {
                            // If it exists, update the array by adding docID
                            userRef.update("organizedEvent", FieldValue.arrayUnion(docID))
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
                            userRef.update("organizedEvent", FieldValue.arrayUnion(docID))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("Firestore", "New organizedEvent field created and document updated!");
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

        db.collection("event")
                .document(docID)
                .set(data);

        Intent intent = new Intent(CreateEventActivity.this, HomepageOrganizer.class);
        //intent.putExtras(bundle);
        //Log.d("DEBUG", "intent created: " + intent);
        startActivity(intent);

        /* CHANGED WORKFLOW TO NAVIGATE TO ORGANIZED EVENTS
        Intent intent = new Intent(CreateEventActivity.this, QRGenerator.class);
        intent.putExtras(bundle);
        //Log.d("DEBUG", "intent created: " + intent);
        startActivity(intent);

         */

    }

    // CHECK IF INPUTS EMPTY
    public boolean isTextEditInputEmpty(){
        if (String.valueOf(newEventName.getText()).isEmpty()){
            newEventName.setError("Enter Event Name");
            return false;
        }
        if (String.valueOf(newStartTime.getText()).isEmpty()){
            newStartTime.setError("Enter Start Time");
            return false;
        }
        if (String.valueOf(newEndTime.getText()).isEmpty()){
            newEndTime.setError("Enter End Time");
            return false;
        }
        if (String.valueOf(newLocation.getText()).isEmpty()){
           newLocation.setError("Enter Event Location");
            return false;
        }

        return true;

    }

    public boolean isPosterChosen(){
        return false;
    }

    // TODO: CHECK INPUTS ARE VALID - ANN


    public void dbConnected(){
        db.getInstance()
                .enableNetwork()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Firestore is connected
                        Log.d("Firestore", "Connected to Firestore");
                        isDBConnected = true;
                    } else {
                        // Firestore connection failed
                        Log.d("Firestore", "Disconnected from Firestore");
                        isDBConnected = false;
                    }
                });
    }

    public void checkPromoCodeAndGenerate(){
        if (generatePromoQRCodeCheckbox.isChecked()) {

            // CheckBox is checked
            String inputValue = Helpers.reverseString(docID);
            QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, 800);

            // Getting QR-Code as Bitmap
            promoCodeBitmap = qrgEncoder.getBitmap(0);
            promoCodeBase64 = Helpers.bitmapToBase64(promoCodeBitmap);
            Log.d("Checkbox", "Checkbox is checked");
        } else {
            // CheckBox is not checked
            Log.d("Checkbox", "Checkbox is not checked");
        }
    }

    public void generateQRCodeAndSetString(){
        QRGEncoder qrgEncoder = new QRGEncoder(docID, null, QRGContents.Type.TEXT, 800);

        // Getting QR-Code as Bitmap
        Bitmap bitmap = qrgEncoder.getBitmap(0);

        // Convert bitmap to Base64 for Firebase
        checkinQRCodeBase64 = Helpers.bitmapToBase64(bitmap);
    }

}
