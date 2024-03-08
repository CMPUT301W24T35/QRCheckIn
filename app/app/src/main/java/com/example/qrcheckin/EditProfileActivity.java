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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for editing existing profile.
 * The user can edit all the details associated with their profile.
 * Updates are then made in the Firebase on the relevant event document.
 */

public class EditProfileActivity extends AppCompatActivity {
    FirebaseFirestore db;
    EditText newUserName;
    EditText newUserPhone;
    EditText newUserEmail;
    EditText newUserUrl;
    Button confirmButton;
    Button editProfileImageButton;
    Button deleteProfileImageButton;
    ImageView profileImage;
    Bitmap initialsBitmap;
    String initialsBase64;
    String profileImageBase64;
    boolean isImageSet;
    Bitmap profileImageBitmap;
    String mainUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        newUserName = findViewById(R.id.editUserNameText);
        newUserEmail = findViewById(R.id.edituserEmailText);
        newUserPhone = findViewById(R.id.edituserPhoneText);
        newUserUrl = findViewById(R.id.edituserHomepageText);
        confirmButton = findViewById(R.id.contAddProfileButton);
        editProfileImageButton = findViewById(R.id.ProfileImageEditButton);
        deleteProfileImageButton = findViewById(R.id.ProfileImageDeleteButton);
        profileImage = findViewById(R.id.ProfileImage);
        db = FirebaseFirestore.getInstance();

        isImageSet = false; // Set flag to not true by default

        try {
            FileInputStream fis = openFileInput("localStorage.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            mainUserID = sb.toString();
            Log.d("Main USER ID", mainUserID);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //String uID = getIntent().getStringExtra("UserID");
        if (mainUserID != null) {
            fetchUserProfile(mainUserID);
        }

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

        deleteProfileImageButton.setOnClickListener(v -> {
            // Delete the existing profile picture
            isImageSet = false;
            String userName = newUserName.getText().toString();
            String initials = getInitials(userName);
            initialsBitmap = generateInitialsImage(initials);
            profileImage.setImageBitmap(initialsBitmap);
        });

        editProfileImageButton.setOnClickListener(v -> {
                    // Launch the photo picker and let the user choose only images.
                    pickMedia.launch(new PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                            .build());
                }
        );

        // Create intent to move to the homepage after creating the profile
        Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);


        confirmButton.setOnClickListener(v -> {
            updateProfile(mainUserID);
        });
    }

    /**
     * Function to read the
     * @param mainUserID
     */
    private void fetchUserProfile(String mainUserID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").document(mainUserID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String profileName = documentSnapshot.getString("name");
                String profilePhone = documentSnapshot.getString("phone");
                String profileEmail = documentSnapshot.getString("email");
                String profileUrl = documentSnapshot.getString("url");
                String profImage = documentSnapshot.getString("profileImage");
                Bitmap profileBitmap = Helpers.base64ToBitmap(profImage);
                profileImage.setImageBitmap(profileBitmap);
                newUserName.setText(profileName);
                newUserEmail.setText(profileEmail);
                newUserPhone.setText(profilePhone);
                newUserUrl.setText(profileUrl);
            } else {
                Log.e("ProfileActivity", "No such document");
            }
        }).addOnFailureListener(error -> {
            Log.e("ProfileActivity", "Error fetching document", error);
        });
    }

    private void updateProfile(String mainUserID) {
        Map<String, Object> info = new HashMap<>();

        if (isProfileInputValid()) {
            String userName = newUserName.getText().toString();
            String phone = newUserPhone.getText().toString();
            String email = newUserEmail.getText().toString();
            String url = newUserUrl.getText().toString();

            info.put("name", userName);
            info.put("phone", phone);
            info.put("email", email);
            if (!url.isEmpty()) {
                info.put("url", url);
            }

            if (isImageSet) {
                info.put("profileImage", profileImageBase64);
            } else {
                String initials = getInitials(userName);
                initialsBitmap = generateInitialsImage(initials);
                initialsBase64 = Helpers.bitmapToBase64(initialsBitmap);
                info.put("profileImage", initialsBase64);
            }
            db.collection("user").document(mainUserID).update(info).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("EditProfileActivity", "User profile updated successfully");
                    // Navigate to ProfileActivity upon successful update
                    Intent intent = new Intent(EditProfileActivity.this, ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Log.e("EditProfileActivity", "Error updating user profile", task.getException());
                    // Optionally show an error message to the user
                }
            });
        } else {
            Log.d("Validation", "Input validation failed.");
        }
    }

    /**
     * This function validates whether the TextEdit input fields are empty and
     * also displays errors if they are empty.
     * @return true if no errors, false if errors
     */
    public boolean isProfileInputValid () {
        //Name validation
        if (String.valueOf(newUserName.getText()).isEmpty()) {
            newUserName.setError("Name is required!");
            return false;
        }
        if (String.valueOf(newUserName.getText()).length() > 200) {
            newUserName.setError("Name is too long!");
            return false;
        }
        // Phone validation
        if (String.valueOf(newUserPhone.getText()).isEmpty()) {
            newUserPhone.setError("Enter Phone Number");
            return false;
        }
        if (String.valueOf(newUserPhone.getText()).length() > 10) {
            newUserPhone.setError("Enter a valid phone number!");
            return false;
        }
        // Email validation
        if (String.valueOf(newUserEmail.getText()).isEmpty()) {
            newUserEmail.setError("Enter email address");
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(String.valueOf(newUserEmail.getText())).matches()) {
            newUserEmail.setError("Enter valid email address");
            return false;
        }
        // Homepage validation
        if (!String.valueOf(newUserUrl.getText()).isEmpty()) {
            if (!Patterns.WEB_URL.matcher(String.valueOf(newUserUrl.getText())).matches()) {
                newUserUrl.setError("Enter valid url!");
                return false;
            }
        }

        return true;
    }

    /**
     * Function to deterministically generate profile picture.
     * @return Bitmap, based on users initials
     * @param initials
     */

    private Bitmap generateInitialsImage (String initials){
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

    /**
     * Helper function to parse the initials from the users entered name.
     *
     * @param name, e.g John Smith
     * @return String, users initials eg JS
     */
    private String getInitials (String name){
        StringBuilder initials = new StringBuilder();
        for (String s : name.split("\\s+")) {
            initials.append(s.charAt(0));
        }
        return initials.toString();
    }
}

