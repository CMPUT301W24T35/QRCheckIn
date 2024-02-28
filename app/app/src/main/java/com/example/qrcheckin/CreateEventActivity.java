package com.example.qrcheckin;
import android.content.Intent;

import android.os.Bundle;
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

public class CreateEventActivity extends AppCompatActivity {
    FirebaseFirestore db;
    boolean isDBConnected;
    EditText newEventName;
    EditText newStartTime;
    EditText newEndTime;
    EditText newLocation;
    Button continueButton;
    Button editPosterImageButton;
    CheckBox generatePromoQRCodeCheckbox;
    boolean needPromoQRCode;

    ImageView posterImage;

    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);


        // Bind UI
        //TODO posterImage = findViewById(R.id.posterImageView);
        //TODO - EDIT POSTER button

        newEventName = findViewById(R.id.eventNameEditText);
        newStartTime = findViewById(R.id.eventStartTimeEditText);
        newEndTime = findViewById(R.id.eventEndTimeEditText);
        newLocation = findViewById(R.id.eventLocationEditText);
        continueButton = findViewById(R.id.continueCreateEventButton);
        editPosterImageButton = findViewById(R.id.editPosterImageButton);
        posterImage = findViewById(R.id.posterImageView);
        db = FirebaseFirestore.getInstance();


        // TODO Optional Field - limit number of attendees

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


    // State intent to move to QRGenerator activity
    Intent intent = new Intent(CreateEventActivity.this, QRGenerator.class);


    // TODO - Continue button
        // Gather all data entered inc PosterImage, QRCode
        // Perform data input checks
        // Write data to db
        continueButton.setOnClickListener(v-> {
            // TODO: Create bundle to pass - VINCENT - pass instance object of Event class? whatever is easier
            bundle.putString("eventName", String.valueOf(newEventName.getText()));
            bundle.putString("startTime", String.valueOf(newStartTime.getText()));
            bundle.putString("endTime", String.valueOf(newEndTime.getText()));
            bundle.putString("Location", String.valueOf(newLocation.getText()));

            if (generatePromoQRCodeCheckbox.isChecked()) {
                // CheckBox is checked
                // Create new bitmap QR Code STUART
                Log.d("Checkbox", "Checkbox is checked");
            } else {
                // CheckBox is not checked
                Log.d("Checkbox", "Checkbox is not checked");
            }

            if (isEventInputValid()){
                intent.putExtras(bundle); // Attach bundle to intent
                dbConnected(); // Call to check if connected and set isDBConnect variable
            } else {
                return;
            }

            // TODO Check database connected and get unique document ID - AYAN
            if (isDBConnected){
                // Get Unique Event ID / document ID
                // Go to QRGenerator
                startActivity(intent);
            } else {
                // Toast - need network connection to proceed
                return;

            }
        });
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


    };

    // TODO: CHECK INPUTS ARE VALID - ANN


    public void dbConnected(){
        FirebaseFirestore.getInstance()
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

}
