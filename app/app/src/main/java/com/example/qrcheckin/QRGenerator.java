package com.example.qrcheckin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

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

        Intent intent = getIntent();
        String eventID = intent.getStringExtra("eventID");
        //String eventID = bundle != null ? bundle.getString("eventID") : null;

//        if (eventID == null || eventID.isEmpty()) {
//            Toast.makeText(this, "Event ID is required.", Toast.LENGTH_SHORT).show();
//            finish(); // Close activity if critical information is missing
//            return;
//        }

        Log.d("BUNDLE", "EventID passed into QR-Scanner: " + eventID);

        initializeUIComponents();
        setButtonListeners(eventID);
    }

    private void initializeUIComponents() {
        generateQRCodeButton = findViewById(R.id.generateCheckinQRCodeButton);
        reuseQRCodeButton = findViewById(R.id.reuseCheckinQRCodeButton);
        QRCodeImage = findViewById(R.id.checkinQRCodeImageView);
        createEventButton = findViewById(R.id.confirmEventCreationButton);
        db = FirebaseFirestore.getInstance();
    }

    private void setButtonListeners(String eventID) {
        generateQRCodeButton.setOnClickListener(v -> generateQRCode(eventID));
        reuseQRCodeButton.setOnClickListener(v -> startActivity(new Intent(QRGenerator.this, QRScannerActivity.class)));
        createEventButton.setOnClickListener(v -> createEvent(eventID));
    }

    private void generateQRCode(String eventID) {
        try {
            QRGEncoder qrgEncoder = new QRGEncoder(eventID, null, QRGContents.Type.TEXT, 800);
            bitmap = qrgEncoder.getBitmap();
            QRCodeImage.setImageBitmap(bitmap);
            QRCodeBase64 = Helpers.bitmapToBase64(bitmap);
        } catch (Exception e) {
            Log.e("QRGenerator", "Error generating QR Code", e);
            Toast.makeText(this, "Error generating QR code: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void createEvent(String eventID) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("checkinQRCode", QRCodeBase64);

        db.collection("event")
                .document(eventID)
                .update(data)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "DocumentSnapshot successfully updated!");
                    startActivity(new Intent(QRGenerator.this, HomepageOrganizer.class));
                })
                .addOnFailureListener(e -> Toast.makeText(QRGenerator.this, "Failed to create event", Toast.LENGTH_SHORT).show());
    }
}

