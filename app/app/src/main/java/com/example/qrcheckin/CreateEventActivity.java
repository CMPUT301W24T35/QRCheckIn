package com.example.qrcheckin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

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


    // TODO - Continue button
        continueButton.setOnClickListener(v -> startNextActivity());

           // TODO: Create bundle to pass - VINCENT - pass instance object of Event class? whatever is easier

                // TODO Create new bitmap QR Code STUART

            // TODO Check database connected and get unique document ID - AYAN
    }

    private void startNextActivity() {
        if (!isEventInputValid()){
            return;
        }
        continueButton.setEnabled(false);
        FirebaseFirestore.getInstance().enableNetwork().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.d("Firestore","Connected to DB");
                addEvent();
            }
            else{
                Log.d("Firestore","Error connecting to db");
            }
        });
    }

    private void addEvent() {
        Bundle bundle = new Bundle();
        HashMap<String, Object> data = new HashMap<>();

        String eventName = newEventName.getText().toString();
        String eventDescription = newEventDescription.getText().toString();
        String startTime = newStartTime.getText().toString();
        String endTime = newEndTime.getText().toString();
        String location = newLocation.getText().toString();

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
                .add(data)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Event added ID: " + documentReference.getId());
                    bundle.putString("EventID", documentReference.getId());
                    //data.put("eventID", documentReference.getId());
                    Intent intent = new Intent(CreateEventActivity.this, QRGenerator.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding event", e);
                    Toast.makeText(CreateEventActivity.this, "Failed to create event. Please try again.", Toast.LENGTH_LONG).show();
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

    }

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
