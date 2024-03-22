package com.example.qrcheckin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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

    String eventID;


    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_generator);

        //assert bundle != null;
        eventID = getIntent().getExtras().getString("eventID");
        Log.d("BUNDLE", "EventID passed into QR-Scanner: " + eventID);

        generateQRCodeButton = findViewById(R.id.generateCheckinQRCodeButton);
        reuseQRCodeButton = findViewById(R.id.reuseCheckinQRCodeButton);
        QRCodeImage = findViewById(R.id.checkinQRCodeImageView);
        createEventButton = findViewById(R.id.confirmEventCreationButton);

        db = FirebaseFirestore.getInstance();
        Log.d("DEBUG", "fetch db:" + db);


        //checkIfNullPointers();

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
            scanCode();
        });

        createEventButton.setOnClickListener(v->{

            // Write checkinQRCode to database
            HashMap<String, Object> data = new HashMap<>();
            data.put("checkinQRCode", QRCodeBase64);


            db.collection("event")
                    .document(eventID)
                    .update(data)
                    .addOnSuccessListener(w->{
                        // Navigate to Organizer Homepage
                        Intent intent = new Intent(QRGenerator.this, HomepageOrganizer.class);
                        startActivity(intent);
                    });

        });

    }

    public void checkIfNullPointers(){
        // Log if any of the variables are null pointers
        if (generateQRCodeButton == null) {
            Log.e("ERROR", "generateQRCodeButton is null");
        }
        if (reuseQRCodeButton == null) {
            Log.e("ERROR", "reuseQRCodeButton is null");
        }
        if (QRCodeImage == null) {
            Log.e("ERROR", "QRCodeImage is null");
        }
        if (createEventButton == null) {
            Log.e("ERROR", "createEventButton is null");
        }
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    private ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null) {
            String qrContent = result.getContents();
            AlertDialog.Builder builder = new AlertDialog.Builder(QRGenerator.this);

            builder.setTitle("Got QR Code");
            builder.setMessage(qrContent).show();

            // TODO make database call to check if it is an in use QR code
            db.collection("event").document(eventID).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            builder
                                    .setTitle("Cannot Use")
                                    .setMessage("QR Code already in use. Please use a different QR Code")
                                    .show();
                        }
                        else {
                            HashMap<String, Object> data = new HashMap<>();
                            data.put("reused QR Code", qrContent);
                            db.collection("event").document(eventID).update(data);
                            Intent intent = new Intent(this, HomepageOrganizer.class);
                            startActivity(intent);
                            finish();

                        }
                    });
            //   if it has already been used
            //      then ask to use another qr code
            //   if it's not already been used
            //      then upload the qrContent to the document using eventID
            //      navigate to HomepageOrganizer activity
        }


    });
}