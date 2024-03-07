package com.example.qrcheckin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CreateProfileActivity extends AppCompatActivity {
    FirebaseFirestore db;
    boolean isDBConnected;
    EditText newUserName;
    EditText newUserPhone;
    EditText newUserEmail;
    EditText newUserHomepage;
    Button confirmButton;
    Button addProfileImageButton;
    ImageView profileImage;
    Bitmap profileImageBitmap;
    Bitmap initialsBitmap;
    String initialsBase64;
    String profileImageBase64;
    Bundle bundle;
    boolean isImageSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        newUserName = findViewById(R.id.userNameEditText);
        newUserEmail = findViewById(R.id.userEmailEditText);
        newUserPhone = findViewById(R.id.userPhoneEditText);
        newUserHomepage = findViewById(R.id.userHomepageEditText);
        confirmButton = findViewById(R.id.continueAddProfileButton);
        addProfileImageButton = findViewById(R.id.editProfileImageButton);
        profileImage = findViewById(R.id.profileImage);
        db = FirebaseFirestore.getInstance();

        isImageSet = false; // Set flag to not true by default

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
                                .into(profileImage);
                        isImageSet = true; // Update the flag because user added an image
                        try {
                            profileImageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            profileImageBase64 = Helpers.bitmapToBase64(profileImageBitmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        addProfileImageButton.setOnClickListener(v->{
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
            /*
            String initials = getInitials(String.valueOf(newUserName.getText()));
            Bitmap initialsBitmap = generateInitialsImage(initials);
            profileImage.setImageBitmap(initialsBitmap);*/
            }
        );

        // Create intent to move to the homepage after creating the profile
        //Intent intent = new Intent(CreateProfileActivity.this, HomepageActivity.class);

        // Set onclick listener for confirm button
        // Check if all input data are valid
        // Write data to db
        confirmButton.setOnClickListener(v -> {
            if (isProfileInputValid()) {
                String userName = newUserName.getText().toString();
                String phone = newUserPhone.getText().toString();
                String email = newUserEmail.getText().toString();
                String url = newUserHomepage.getText().toString();

                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("name", userName);
                userInfo.put("phone", phone);
                userInfo.put("email", email);
                if (!url.isEmpty()) {
                    userInfo.put("url", url);
                }

                if (isImageSet) {
                    userInfo.put("profileImage", profileImageBase64);
                } else {
                    String initials = getInitials(userName);
                    initialsBitmap = generateInitialsImage(initials);
                    initialsBase64 = Helpers.bitmapToBase64(initialsBitmap);
                    userInfo.put("profileImage", initialsBase64);
                }

                db.collection("user").add(userInfo).addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Added with ID: " + documentReference.getId());

//                    Bundle bundle = new Bundle();
//                    // Instead of just adding the document ID, add all the user information
//                    bundle.putString("UserID", documentReference.getId()); // keep this if you need the document ID later
//                    bundle.putString("name", userName);
//                    bundle.putString("phone", phone);
//                    bundle.putString("email", email);
//                    bundle.putString("url", url); // It's okay if this is empty, the receiving activity should handle it
//                    // No need to check if the profile image is set or not here; just pass what you have
//                    bundle.putString("profileImage", isImageSet ? profileImageBase64 : initialsBase64);

                    Intent intent = new Intent(CreateProfileActivity.this, HomepageActivity.class);
                    intent.putExtra("UserID", documentReference.getId()); // Attach the bundle to the intent
                    startActivity(intent);

                }).addOnFailureListener(e -> {
                    Log.w("Firestore", "Error adding document", e);
                });
            } else {
                Log.d("Validation", "Input validation failed.");
            }
        });
    }

    // TODO - Input validation for all fields
    // Check the input validity
    public boolean isProfileInputValid() {
        //Name validation
        if (String.valueOf(newUserName.getText()).isEmpty()){
            newUserName.setError("Name is required!");
            return false;
        }
        if (String.valueOf(newUserName.getText()).length() > 200) {
            newUserName.setError("Name is too long!");
            return false;
        }
        // Phone validation
        if (String.valueOf(newUserPhone.getText()).isEmpty()){
            newUserPhone.setError("Enter Phone Number");
            return false;
        }
        if (String.valueOf(newUserPhone.getText()).length() > 10){
            newUserPhone.setError("Enter a valid phone number!");
            return false;
        }
        // Email validation
        if (String.valueOf(newUserEmail.getText()).isEmpty()){
            newUserEmail.setError("Enter email address");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(String.valueOf(newUserEmail.getText())).matches()) {
            newUserEmail.setError("Enter valid email address");
            return false;
        }
        // Homepage validation
        if (!String.valueOf(newUserHomepage.getText()).isEmpty()) {
            if (!Patterns.WEB_URL.matcher(String.valueOf(newUserHomepage.getText())).matches()) {
                newUserHomepage.setError("Enter valid url!");
                return false;
            }
        }

        return true;
    }
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

    // Deterministically generate profile picture
    private Bitmap generateInitialsImage(String initials) {
        int width = 200; // Set the desired width for the image
        int height = 200; // Set the desired height for the image
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Fill background with a color
        canvas.drawColor(Color.parseColor("#FF5722")); // Example color, you can change it

        // Draw text (initials) in the center of the bitmap
        Paint paint = new Paint();
        paint.setColor(Color.WHITE); // Text color
        paint.setTextSize(80); // Text size
        paint.setTextAlign(Paint.Align.CENTER);

        // Calculate text position
        float xPos = canvas.getWidth() / 2f;
        float yPos = (canvas.getHeight() / 2f) - ((paint.descent() + paint.ascent()) / 2f);

        // Draw text on the canvas
        canvas.drawText(initials, xPos, yPos, paint);

        return bitmap;
    }
    private String getInitials(String name) {
        StringBuilder initials = new StringBuilder();
        for (String s : name.split("\\s+")) {
            initials.append(s.charAt(0));
        }
        return initials.toString();
    }
}
