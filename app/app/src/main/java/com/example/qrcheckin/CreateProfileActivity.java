package com.example.qrcheckin;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class CreateProfileActivity extends AppCompatActivity {
    FirebaseFirestore db;
    boolean isDBConnected;
    EditText newUserName;
    EditText newUserPhone;
    EditText newUserEmail;
    EditText newUserHomepage;
    Button confirmButton;
    Button editProfileImageButton;
    ImageView profileImage;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        newUserName = findViewById(R.id.userNameEditText);
        newUserEmail = findViewById(R.id.userEmailEditText);
        newUserPhone = findViewById(R.id.userPhoneEditText);
        newUserHomepage = findViewById(R.id.userHomepageEditText);
        confirmButton = findViewById(R.id.continueAddProfileButton);
        editProfileImageButton = findViewById(R.id.editProfileImageButton);
        profileImage = findViewById(R.id.profileImage);
        db = FirebaseFirestore.getInstance();

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
                    } else {
                        Log.d("PhotoPicker", "No media selected");
                    }
                });

        editProfileImageButton.setOnClickListener(v->{
            // Launch the photo picker and let the user choose only images.
            pickMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
            }
        );

        // Create intent to move to the homepage after creating the profile
        Intent intent = new Intent(CreateProfileActivity.this, MainActivity.class);

        // Set onclick listener for confirm button
        // Check if all input data are valid
        // Write data to db
        confirmButton.setOnClickListener(v-> {
            bundle.putString("userName", String.valueOf(newUserName.getText()));
            bundle.putString("phone", String.valueOf(newUserPhone.getText()));
            bundle.putString("email", String.valueOf(newUserEmail.getText()));
            bundle.putString("homepage", String.valueOf(newUserHomepage.getText()));

        });
    }

    // TODO - Input validation for all fields
    // Check the input validity
    public boolean isProfileInputValid() {
        if (String.valueOf(newUserName.getText()).isEmpty()){
            newUserName.setError("Enter user name");
            return false;
        }
        if (String.valueOf(newUserPhone.getText()).isEmpty()){
            newUserPhone.setError("Enter Phone Number");
            return false;
        }
        if (String.valueOf(newUserEmail.getText()).isEmpty()){
            newUserEmail.setError("Enter email address");
            return false;
        }
        
        return true;
    }
}
