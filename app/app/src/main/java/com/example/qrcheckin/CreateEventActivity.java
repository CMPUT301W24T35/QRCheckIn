package com.example.qrcheckin;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
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
    ImageView QRCodeImage;

    Bitmap bitmap;

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
        confirmButton = findViewById(R.id.confirmCreateEventButton);
        editPosterImageButton = findViewById(R.id.editPosterImageButton);
        posterImage = findViewById(R.id.posterImageView);
        generateQRCodeButton = findViewById(R.id.generateQRCodeButton);
        reuseQRCodeButton = findViewById(R.id.reuseQRCodeButton);
        QRCodeImage = findViewById(R.id.QRCodeImageView);

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



    // TODO - Generate QR Code
        // Generate new QR Code
        // Set to display
        generateQRCodeButton.setOnClickListener(v->{
            //isEventInputValid();
            if (isEventInputValid()) {
                        String inputValue = String.valueOf(newEventName.getText());
                        Log.d("DEBUG", "QRCode InputValue:" + inputValue);
                        QRGEncoder qrgEncoder = new QRGEncoder(inputValue, null, QRGContents.Type.TEXT, 800);


                        // Getting QR-Code as Bitmap
                        bitmap = qrgEncoder.getBitmap(0);
                        // Setting Bitmap to ImageView (Assuming qrImage is your ImageView)
                        QRCodeImage.setImageBitmap(bitmap);
                    }
            else {
                return;
                    }
        }
        );

    // TODO - Reuse QR Code
        // Add once implemented by person assigned to QR Scanner

    // TODO - Confirm button
        // Gather all data entered inc PosterImage, QRCode
        // Perform data input checks
        // Write data to db
        confirmButton.setOnClickListener(v-> {
            if (isEventInputValid()) {
                // Do stuff
            } else {
                // Toast
            }
        });
    }

    public boolean isEventInputValid(){
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

}
