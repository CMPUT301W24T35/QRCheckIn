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

            int checkInNum;      //record the number of check in

            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>(){
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // Check if userIDCheckIn field exists
                            if (document.contains("userIDCheckIn")) {
                                // If it exists, update the array by adding docID
                                docRef.update("userIDCheckIn", FieldValue.arrayUnion(userID))
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
                                docRef.update("userIDCheckIn", FieldValue.arrayUnion(userID))
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
                        } else { // QR Code could be a Promo code
                            checkPromoCode();
                            Log.d("Firestore", "No such document");
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                    }
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
            // Get the document
            /*
            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    // If EventID exists in Database
                    // Check in:
                    //  Update Event field "CheckedinAttendees" with userID
                    //  Increment number of times checked in
                    //  Update Event checked in Status to TRUE
                    if(docRef.data().checkInUsers) {

                    }
                    int numCheckIn = 0;
                    HashMap<String, HashMap<String,Object>> checkInUsers = new HashMap<>();
                    HashMap<String, Object> userCheckIns = checkInUsers.get("checkInUsers");
                    if(userCheckIns == null) {
                        userCheckIns = new HashMap<>();
                        checkInUsers.put("checkInUsers", userCheckIns);
                    }
                    numCheckIn+=1;
                    userCheckIns.put(userID, numCheckIn);
                    docRef.update(userCheckIns);
                    Object data = documentSnapshot.getData();

                    // Need
                } else {
                    Log.d("firebase", "wrong connection");
                }
            });

             */



            builder.setTitle("Checked In");
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
