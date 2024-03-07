package com.example.qrcheckin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/**
* This is a class that generate a QR code which allow user to use camera scan for
*/ 

public class QRGenerator extends AppCompatActivity {

    Button generateQRCodeButton;
    Button reuseQRCodeButton;
    Button createEventButton;

    ImageView QRCodeImage;
    String inputValue;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_generator);
        Bundle bundle = getIntent().getExtras();

        String eventID = bundle.getString("eventID");
        Log.d("BUNDLE", "EventID passed as: " + eventID);

        generateQRCodeButton = findViewById(R.id.generateCheckinQRCodeButton);
        reuseQRCodeButton = findViewById(R.id.reuseCheckinQRCodeButton);
        QRCodeImage = findViewById(R.id.checkinQRCodeImageView);
        createEventButton = findViewById(R.id.confirmEventCreationButton);


        // Generate new QR Code
        generateQRCodeButton.setOnClickListener(v->{
            //TODO String inputValue = documentID generated from firebase
            QRGEncoder qrgEncoder = new QRGEncoder(eventID, null, QRGContents.Type.TEXT, 800);

            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.getBitmap(0);
            // Setting Bitmap to ImageView
            QRCodeImage.setImageBitmap(bitmap);
            // Convert bitmap to Base64 for Firebase
            String QRCodeBase64 = Helpers.bitmapToBase64(bitmap);
        });

        reuseQRCodeButton.setOnClickListener(v->{
            // TODO
            //  Add once implemented by person assigned to QR Scanner
            Intent intent = new Intent(QRGenerator.this, QRScannerActivity.class);
            startActivity(intent);
        });

        createEventButton.setOnClickListener(v->{
            // TODO
            //  1. Create instance of Event Class
            //  2. Write Event to database

            //  3. Navigate to new activity: EventPage (ORGANIZER)
            //Intent intent = new Intent(QRGenerator.this, EventPageOrganizer.class);
            //startActivity(intent);
        });
    }

}
