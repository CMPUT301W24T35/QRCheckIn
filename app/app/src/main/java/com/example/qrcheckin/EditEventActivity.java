package com.example.qrcheckin;

import android.content.Intent;
import android.graphics.Bitmap;
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

import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class EditEventActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    boolean isDBConnected;
    EditText editEventName;
    EditText editEventDescription;
    EditText editStartTime;
    EditText editEndTime;
    EditText editLocation;
    Button confirmButton;
    Button editPosterImageButton;
    CheckBox generatePromoQRCodeCheckbox;
    boolean needPromoQRCode;
    ImageView posterImage;

    Bundle bundle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);


        // Bind UI
        editEventName = findViewById(R.id.eventNameEditText);
        editEventDescription = findViewById(R.id.eventDescriptionEditText);
        editStartTime = findViewById(R.id.eventStartTimeEditText);
        editEndTime = findViewById(R.id.eventEndTimeEditText);
        editLocation = findViewById(R.id.eventLocationEditText);
        confirmButton = findViewById(R.id.continueCreateEventButton);
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
        //continueButton.setOnClickListener(v -> startNextActivity());
        // TEST
        confirmButton.setOnClickListener(v -> startNextActivity());

        // TODO: Create bundle to pass - VINCENT - pass instance object of Event class? whatever is easier

                // TODO Create new bitmap QR Code STUART

            // TODO Check database connected and get unique document ID - AYAN
    }

    private void startNextActivity() {
        if (isEventInputValid()) {
            editEvent();
        }
    }

    private void editEvent() {
        Bundle bundle = new Bundle();
        HashMap<String, Object> data = new HashMap<>();

        String eventName = editEventName.getText().toString();
        String eventDescription = editEventDescription.getText().toString();
        String startTime = editStartTime.getText().toString();
        String endTime = editEndTime.getText().toString();
        String location = editLocation.getText().toString();
        // TODO: Need docID passed from bundle, placeholder below
        String docID = "1234";

        // TODO profileID to organizerID

        data.put("eventName", eventName);
        data.put("eventDescription", eventDescription);
        data.put("startTime", startTime);
        data.put("endTime", endTime);
        data.put("location", location);

        if (generatePromoQRCodeCheckbox.isChecked()) {

            // CheckBox is checked
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

        Intent intent = new Intent(EditEventActivity.this, QRGenerator.class);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    // CHECK IF INPUTS EMPTY
    public boolean isEventInputValid(){ // TODO rename
        if (String.valueOf(editEventName.getText()).isEmpty()){
            editEventName.setError("Enter Event Name");
            return false;
        }
        if (String.valueOf(editStartTime.getText()).isEmpty()){
            editStartTime.setError("Enter Start Time");
            return false;
        }
        if (String.valueOf(editEndTime.getText()).isEmpty()){
            editEndTime.setError("Enter End Time");
            return false;
        }
        if (String.valueOf(editLocation.getText()).isEmpty()){
           editLocation.setError("Enter Event Location");
            return false;
        }

        return true;

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
}
