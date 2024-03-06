package com.example.qrcheckin;

<<<<<<< HEAD
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
=======
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
>>>>>>> 7044ab24b655fd5348f453537fe799830ec8bda8
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
<<<<<<< HEAD
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateEventActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    boolean isDBConnected;
    EditText newEventName;
    EditText newEventDescription;
    EditText newStartTime;
    EditText newEndTime;
    EditText newLocation;
    Button continueButton;
    Button editPosterImageButton;
    CheckBox generatePromoQRCodeCheckbox;
    boolean needPromoQRCode;

    ImageView posterImage;

    Bundle bundle;


=======

import kotlinx.coroutines.channels.Send;


public class CreateEventActivity extends AppCompatActivity {
    EditText newEventName;
    EditText newStartTime;
    EditText newEndTime;
    EditText newLocation;
    Button confirmButton;
    Button editPosterImageButton;

    Button generateQRCodeButton;
    Button reuseQRCodeButton;

    ImageView posterImage;

>>>>>>> 7044ab24b655fd5348f453537fe799830ec8bda8
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);


        // Bind UI
        //TODO posterImage = findViewById(R.id.posterImageView);
        //TODO - EDIT POSTER button

        newEventName = findViewById(R.id.eventNameEditText);
<<<<<<< HEAD
        newEventDescription = findViewById(R.id.eventDescriptionEditText);
        newStartTime = findViewById(R.id.eventStartTimeEditText);
        newEndTime = findViewById(R.id.eventEndTimeEditText);
        newLocation = findViewById(R.id.eventLocationEditText);
        continueButton = findViewById(R.id.continueCreateEventButton);
        editPosterImageButton = findViewById(R.id.editPosterImageButton);
        posterImage = findViewById(R.id.posterImageView);
        generatePromoQRCodeCheckbox = findViewById(R.id.checkboxGeneratePromoQRCode);
        db = FirebaseFirestore.getInstance();
        // TODO Optional Field - limit number of attendees
=======
        newStartTime = findViewById(R.id.eventStartTimeEditText);
        newEndTime = findViewById(R.id.eventEndTimeEditText);
        newLocation = findViewById(R.id.eventLocationEditText);
        confirmButton = findViewById(R.id.confirmCreateEventButton);
        editPosterImageButton = findViewById(R.id.editPosterImageButton);
        posterImage = findViewById(R.id.posterImageView);
        generateQRCodeButton = findViewById(R.id.generateQRCodeButton);
        reuseQRCodeButton = findViewById(R.id.reuseQRCodeButton);
>>>>>>> 7044ab24b655fd5348f453537fe799830ec8bda8

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


<<<<<<< HEAD
    // TODO - Continue button
        //continueButton.setOnClickListener(v -> startNextActivity());
        // TEST
        continueButton.setOnClickListener(v -> startNextActivity());

        // TODO: Create bundle to pass - VINCENT - pass instance object of Event class? whatever is easier

                // TODO Create new bitmap QR Code STUART

            // TODO Check database connected and get unique document ID - AYAN
    }

    private void startNextActivity() {
        if (isEventInputValid()) {
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
        String docID = Helpers.createDocID(eventName, startTime, location);

        // TODO profileID to organizerID

        data.put("eventName", eventName);
        data.put("eventDescription", eventDescription);
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        data.put("location", location);

        bundle.putString("eventName", eventName);
        bundle.putString("eventDescription", eventDescription);
        bundle.putString("startTime", startTime);
        bundle.putString("endTime", endTime);
        bundle.putString("location", location);
        bundle.putString("eventID", docID);


        if (generatePromoQRCodeCheckbox.isChecked()) {

            // CheckBox is checked
            // TODO Create new bitmap QR Code STUART
            String inputValue = "tester";
            QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, 800);

            // Getting QR-Code as Bitmap
            Bitmap bitmap = qrgEncoder.getBitmap(0);
            //  Add bitmap to bundle
            Log.d("Checkbox", "Checkbox is checked");
        } else {
            // CheckBox is not checked
            Log.d("Checkbox", "Checkbox is not checked");
        }

        db.collection("event")
                .document(docID)
                .set(data);

        Intent intent = new Intent(CreateEventActivity.this, QRGenerator.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    // CHECK IF INPUTS EMPTY
    public boolean isEventInputValid(){ // TODO rename
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

    // TODO: CHECK INPUTS ARE VALID - ANN
    public boolean isEventInputValid() {
        // TODO: CHECK INPUTS ARE VALID - ANN

        // Check if event name is a non-empty string
        if (!isValidString(String.valueOf(newEventName.getText()))) {
            newEventName.setError("Enter Event Name"); // Set error message if empty
            return false; // Return false indicating invalid input
        }

        // Check if start time is a valid time format (HH:mm)
        if (!isValidTime(String.valueOf(newStartTime.getText()))) {
            newStartTime.setError("Enter Valid Start Time (HH:mm)"); // Set error message if invalid time format
            return false; // Return false indicating invalid input
        }

        // Check if end time is a valid time format (HH:mm)
        if (!isValidTime(String.valueOf(newEndTime.getText()))) {
            newEndTime.setError("Enter Valid End Time (HH:mm)"); // Set error message if invalid time format
            return false; // Return false indicating invalid input
        }

        // Check if location is a non-empty string
        if (!isValidString(String.valueOf(newLocation.getText()))) {
            newLocation.setError("Enter Event Location"); // Set error message if empty
            return false; // Return false indicating invalid input
        }

        // All input fields are valid
        return true; // Return true indicating all inputs are valid
    }

    // Helper method to check if a string is a non-empty string
    private boolean isValidString(String input) {
        return input != null && !input.isEmpty(); // Check if input is not null and not empty
    }

    // Helper method to check if a string is a valid time in format (HH:mm)
    private boolean isValidTime(String input) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm"); // Define time format
        sdf.setLenient(false); // Set parsing to strict to avoid leniency

        try {
            Date date = sdf.parse(input); // Attempt to parse input as date
            return date != null; // If parsing succeeds, return true indicating valid time
        } catch (ParseException e) {
            return false; // If parsing fails, return false indicating invalid time format
        }
    }
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
=======
    // TODO - Generate QR Code
        // Generate new QR Code
        // Set to display


    // TODO - Reuse QR Code

    // TODO - Confirm button
        // Gather all data entered inc PosterImage, QRCode
        // Perform data input checks
        // Write data to db

>>>>>>> 7044ab24b655fd5348f453537fe799830ec8bda8
    }
}
