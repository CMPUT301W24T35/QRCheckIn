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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class CreateEventActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private CollectionReference eventsRef;
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
        db = FirebaseFirestore.getInstance();
        eventsRef = db.collection("events");

        newEventName = findViewById(R.id.eventNameEditText);
        newEventDescription = findViewById(R.id.eventDescriptionEditText);
        newStartTime = findViewById(R.id.eventStartTimeEditText);
        newEndTime = findViewById(R.id.eventEndTimeEditText);
        newLocation = findViewById(R.id.eventLocationEditText);
        continueButton = findViewById(R.id.continueCreateEventButton);
        editPosterImageButton = findViewById(R.id.editPosterImageButton);
        posterImage = findViewById(R.id.posterImageView);
        generatePromoQRCodeCheckbox = findViewById(R.id.checkboxGeneratePromoQRCode);

        addNewEvent();
        // TODO Optional Field - limit number of attendees

        eventsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("Firestore", error.toString());
                    return;
                }
                if (querySnapshots != null) {
                    for (QueryDocumentSnapshot doc : querySnapshots) {
                        String eventName = doc.getId();
                        String startTime = doc.getString("startTime");
                        String endTime = doc.getString("endTime");
                        String location = doc.getString("location");
                        Log.d("Firestore", String.format("Event(%s, %s, %s, %s) fetched", eventName, startTime, endTime, location));
                    }
                }
            }
        });

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
            bundle = new Bundle();
            bundle.putString("eventName", String.valueOf(newEventName.getText()));
            bundle.putString("eventDescription",String.valueOf(newEventDescription.getText()));
            bundle.putString("startTime", String.valueOf(newStartTime.getText()));
            bundle.putString("endTime", String.valueOf(newEndTime.getText()));
            bundle.putString("Location", String.valueOf(newLocation.getText()));

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

    private void addNewEvent() {
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
