package com.example.qrcheckin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

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
import java.util.List;
import java.util.Map;

//source:https://www.bing.com/videos/riverview/relatedvideo?q=open%20camera%20scan%20qr%20code%20in%20android%20studio&mid=27B08E2657DEFA5CC74327B08E2657DEFA5CC743&ajaxhist=0
/**
 * Activity for scanning QR codes related to events. It provides functionality
 * for users to scan QR codes to check in to events  with Firestore integration
 * for verifying and updating event attendance.
 */
public class QRScannerActivity extends AppCompatActivity {

    Button scan;

    ImageButton back;
    String userID;
    String qrContent;

    FirebaseFirestore db;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);

        // Fetch db
        db = FirebaseFirestore.getInstance();

        back = findViewById(R.id.button_backArrow);
        back.setOnClickListener(v -> {

            Intent intent = new Intent(QRScannerActivity.this, HomepageActivity.class);

            startActivity(intent);
        });

        scan = findViewById(R.id.button_scan_qr);
        scan.setOnClickListener(v -> {
            scanCode();
            //pass the qr information to eventActivity
            //Intent intent = new Intent(QRScannerActivity.this, CreateEventActivity.class);
            //intent.putExtra("QR_content", qrContent);
            //startActivity(intent);
        });
    }
    /**
     * Sets up and launches the QR code scanner..
     */
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Volume up to flash on");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLauncher.launch(options);
    }

    /**
     * Handles the result from scanning a QR code. If a QR code is successfully
     * scanned, it checks the Firestore database for event attendance, and
     * updates the database accordingly.
     */
    private ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if(result.getContents() != null) {
            qrContent = result.getContents();

            AlertDialog.Builder builder = new AlertDialog.Builder(QRScannerActivity.this);

            // TODO Query Database to find Event
            // Specify the collection and document ID
            DocumentReference docRef = db.collection("event").document(qrContent);


            try {
                FileInputStream fis = openFileInput("localStorage.txt");
                InputStreamReader isr = new InputStreamReader(fis);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                userID = sb.toString();
                Log.d("Main USER ID", userID);
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // hold the attendeeCapacity
            final int[] attendeeCapacityWrapper = new int[1];

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        // Check if the document contains the attendeeCapacity field
                        if (document.exists() && document.contains("attendeeCapacity")) {
                            // Get the value of attendeeCapacity
                            Number tempCapacity = document.getLong("attendeeCapacity"); // Firestore stores numbers as Long by default
                            // deal with the case where attendeeCapacity does not exist
                            if(tempCapacity != null) {
                                attendeeCapacityWrapper[0] = tempCapacity.intValue();
                                Log.d("Firestore", "Attendee Capacity: " + attendeeCapacityWrapper[0]);
                            }
                        } else {
                            Log.d("Firestore", "No such document or field");
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                    }
                }
            });

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        Map<String, Long> userIDCheckIn = (Map<String, Long>) document.get("userIDCheckIn");
                        if (userIDCheckIn == null) {
                            userIDCheckIn = new HashMap<>();
                        }
                        //get the current count from fire base
                        // reference: https://javatutorialhq.com/java/util/hashmap-class/getordefault-method-example/
                        Long currentCheckInCount = userIDCheckIn.getOrDefault(userID, 0L);
                        //if the current checkIn number is bigger than 1, it means we already checked in
                        boolean isCheckedIn = currentCheckInCount > 0;

                        //get the list of signedUp attendees
                        List<String> signedUpUsers = (List<String>) document.get("signedUpUsers");
                        // null = 0, not null = number of signed up
                        int uniqueAttendees = signedUpUsers != null ? signedUpUsers.size() : 0;

                        if (isCheckedIn || uniqueAttendees < attendeeCapacityWrapper[0]) {
                            // If the user has already checked in or adding them does not exceed capacity
                            userIDCheckIn.put(userID, currentCheckInCount + 1); // Increment or set their check-in count
                            docRef.update("userIDCheckIn", userIDCheckIn)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getApplicationContext(), "Checked in successfully!", Toast.LENGTH_LONG).show();

                                            Log.d("Firestore", "Document successfully updated!");
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Event is at full capacity. Cannot check in.", Toast.LENGTH_LONG).show();

                                            Log.w("Firestore", "Error updating document", e);
                                        }
                                    });
                        } else if (!isCheckedIn && uniqueAttendees >= attendeeCapacityWrapper[0]) {
                            // New attendee and capacity reached
                            Log.d("Firestore", "Cannot check-in. Event is at full capacity.");
                            // Handle full capacity (e.g., show a message to the user)
                        }
                    } else {
                        Log.d("Firestore", "No such document.");
                        // Handle the case where the document does not exist
                    }
                } else {
                    Log.e("Firestore", "Failed to fetch document.", task.getException());
                    // Handle the failure to fetch the document
                }
            });

            DocumentReference checkEventdocRef = db.collection("user").document(userID);

            checkEventdocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Check if checkedEvent field exists
                            if (document.contains("checkedEvent")) {
                                // If it exists, update the array by adding docID
                                checkEventdocRef.update("checkedEvent", FieldValue.arrayUnion(qrContent))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("Firestore", "Document successfully updated!");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Firestore", "Error updating document", e);
                                            }
                                        });
                            }else {
                                // If it doesn't exist, create a new array with docID
                                checkEventdocRef.update("checkedEvent", FieldValue.arrayUnion(qrContent))
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Log.d("Firestore", "New organizedEvent field created and document updated!");
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Firestore", "Error updating document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                    }
                }
            });

            builder.setTitle("Scanned !!!");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent intent = new Intent(QRScannerActivity.this, HomepageActivity.class);
                    intent.putExtra("QR_content", qrContent);
                    startActivity(intent);
                }
            }).show();
        }
    });

    /**
     * Verifies and handles promo codes and querying the Firestore
     * database for a matching event document.
     */
    public void checkPromoCode(){
        String reverseQRString = Helpers.reverseString(qrContent);
        DocumentReference docRef = db.collection("event").document(reverseQRString);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Navigate to EventPage
                        Log.d("Firestore", "PromoCode Scanend and found document with EventID: " + reverseQRString);

                    } else { // QR Code could be a Promo code
                        Log.d("Firestore", "No such document");
                        Toast.makeText(QRScannerActivity.this, "QR Code Not Found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });

    }

}
