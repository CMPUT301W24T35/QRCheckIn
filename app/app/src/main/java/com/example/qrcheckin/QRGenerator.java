package com.example.qrcheckin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

/**
* Create QR Code
*/

public class QRGenerator extends AppCompatActivity {

    Button generateQRCodeButton;
    Button reuseQRCodeButton;
    Button createEventButton;
    ImageView QRCodeImage;
    String QRCodeBase64;
    Bitmap bitmap;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_generator);
        Bundle bundle = getIntent().getExtras();

        assert bundle != null;
        String eventID = bundle.getString("eventID");
        Log.d("BUNDLE", "EventID passed into QR-Scanner: " + eventID);

        generateQRCodeButton = findViewById(R.id.generateCheckinQRCodeButton);
        reuseQRCodeButton = findViewById(R.id.reuseCheckinQRCodeButton);
        QRCodeImage = findViewById(R.id.checkinQRCodeImageView);
        createEventButton = findViewById(R.id.confirmEventCreationButton);
        db = FirebaseFirestore.getInstance();

        // Generate new QR Code
        generateQRCodeButton.setOnClickListener(v->{
            //TODO String inputValue = documentID generated from firebase
            QRGEncoder qrgEncoder = new QRGEncoder(eventID, null, QRGContents.Type.TEXT, 800);

            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.getBitmap(0);
            // Setting Bitmap to ImageView
            QRCodeImage.setImageBitmap(bitmap);
            // Convert bitmap to Base64 for Firebase
            QRCodeBase64 = Helpers.bitmapToBase64(bitmap);
            Log.d("DEBUG", "QRCodeBase64: " + QRCodeBase64);
        });

        reuseQRCodeButton.setOnClickListener(v->{
            // TODO
            //  Add once implemented by person assigned to QR Scanner
            //  How do we link the QRScanner back to this event and activity once we
            //  take a picture of the Qr Code?
            Intent intent = new Intent(QRGenerator.this, QRScannerActivity.class);
            startActivity(intent);
        });

        createEventButton.setOnClickListener(v->{
            // Write checkinQRCode to database
            HashMap<String, Object> data = new HashMap<>();
            data.put("checkinQRCode", QRCodeBase64);

            assert eventID != null;
            db.collection("event")
                    .document(eventID)
                    .update(data);

            // Navigate to Organizer Homepage
            Intent intent = new Intent(QRGenerator.this, HomepageOrganizer.class);
            startActivity(intent);
        });
    }

}
