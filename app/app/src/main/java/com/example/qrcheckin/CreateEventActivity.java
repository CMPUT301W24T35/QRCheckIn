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
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

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
    Bundle bundle;

    String docID;

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
                            throw new RuntimeException(e);
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
        Integer attendeeCapacity = Integer.parseInt(attendeeCapacityString);

        checkPromoCodeAndGenerate();

        Log.d("DEBUG", "docID in CreateEventActivity: " + docID);
        // TODO profileID to organizerID

        data.put("eventName", eventName);
        data.put("eventDescription", eventDescription);
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        data.put("location", location);
        data.put("attendeeCapacity", attendeeCapacity);
        data.put("poster", posterImageBase64);

        // If promo code was generated then add it to Firebase bundle
        if (promoCodeBase64 != null) {
            Log.d("DEBUG", "promocode: " + promoCodeBase64);
            data.put("promoQRCode", promoCodeBase64);
        }

        // TODO - only pass relevant bundle info for QR Code
        bundle.putString("eventName", eventName);
        bundle.putString("eventDescription", eventDescription);
        bundle.putString("startTime", startTime);
        bundle.putString("endTime", endTime);
        bundle.putString("location", location);
        bundle.putString("eventID", docID);

        db.collection("event")
                .document(docID)
                .set(data);

        Intent intent = new Intent(CreateEventActivity.this, QRGenerator.class);
        intent.putExtras(bundle);
        startActivity(intent);

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

}
