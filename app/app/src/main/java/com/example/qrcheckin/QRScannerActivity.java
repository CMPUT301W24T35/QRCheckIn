package com.example.qrcheckin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
//source:https://www.bing.com/videos/riverview/relatedvideo?q=open%20camera%20scan%20qr%20code%20in%20android%20studio&mid=27B08E2657DEFA5CC74327B08E2657DEFA5CC743&ajaxhist=0
/**
* Allow user to scan the code on device
*/

public class QRScannerActivity extends AppCompatActivity {

    Button scan;

    String qrContent;

    FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);

        // Fetch db
        db = FirebaseFirestore.getInstance();

        scan = findViewById(R.id.button_scan_qr);
        scan.setOnClickListener(v -> {
            scanCode();
            //pass the qr information to eventActivity
            //Intent intent = new Intent(QRScannerActivity.this, CreateEventActivity.class);
            //intent.putExtra("QR_content", qrContent);
            //startActivity(intent);
        });
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
            qrContent = result.getContents();

            AlertDialog.Builder builder = new AlertDialog.Builder(QRScannerActivity.this);

            // TODO Query Database to find Event
            // Specify the collection and document ID
            DocumentReference docRef = db.collection("event").document(qrContent);

            // Get the document
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // If EventID exists in Database
                    // Check in:
                    //  Update Event field "CheckedinAttendees" with userID
                    //  Increment number of times checked in
                    //  Update Event checked in Status to TRUE

                    Object data = documentSnapshot.getData();

                    // Need
                } else {

                }
            });



            //  Find eventID == qrContent
            //      Checkin to Event


            builder.setTitle("Checked In");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //Intent intent = new Intent(QRScannerActivity.this, CreateEventActivity.class);
                    //intent.putExtra("QR_content", qrContent);
                    //startActivity(intent);
                }
            }).show();
        }
    });
}
