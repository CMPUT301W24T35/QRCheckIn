package com.example.qrcheckin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView userName;
    TextView userEmail;
    TextView userPhone;
    TextView userUrl;
    CircleImageView userImage;
    Button back;
    Button edit;

    String mainUserID;

    @Override
    protected void onResume() {
        super.onResume();
        // Check if mainUserID is not null and then fetch details
        if (mainUserID != null) {
            fetchDetails(mainUserID);
        }
    }


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);


        //Bundle extras = getIntent().getExtras();
        userName = findViewById(R.id.user_name_view);
        userEmail = findViewById(R.id.user_email_view);
        userPhone = findViewById(R.id.user_phone_view);
        userUrl = findViewById(R.id.user_url_view);
        userImage = findViewById(R.id.profile_image);
        //Bundle extras = getIntent().getExtras();
        back = findViewById(R.id.button_back);
        edit = findViewById(R.id.editEventButton);

        //String uID = getIntent().getStringExtra("UserID");
//        if (uID !=null){
//            fetchDetails(uID);
//        }
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


        if (mainUserID !=null){
            fetchDetails(mainUserID);
        }

        //TODO:Add name, email, and phone


        //TODO: Integrate with firebase and get the data from firebase
        //String name =
        //String email =
        //String phoneNumber =

        //userName.setText(name);
        //userEmail.setText(email);
        //userPhone.setText(phoneNumber);


        //edit button
        edit = findViewById(R.id.button_edit_events);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //feel free to use the code below to connect to the activity
                Intent intent = new Intent(ProfileActivity.this, EditProfileActivity.class);// go to event activity need to connect with other activity
                intent.putExtra("UserID",mainUserID);
                startActivity(intent);
            }
        });
        //back = findViewById(R.id.button_back_events);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //feel free to use the code below to connect to the activity
                Intent intent = new Intent(ProfileActivity.this, HomepageActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });
    }

    private void fetchDetails(String uID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").document(uID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()){
                String profileName = documentSnapshot.getString("name");
                String profilePhone = documentSnapshot.getString("phone");
                String profileEmail = documentSnapshot.getString("email");
                String profileUrl = documentSnapshot.getString("url");
                String profileImage = documentSnapshot.getString("profileImage");
                Bitmap profileBitmap = Helpers.base64ToBitmap(profileImage);
                userImage.setImageBitmap(profileBitmap);
                userName.setText(profileName);
                userEmail.setText(profileEmail);
                userPhone.setText(profilePhone);
                userUrl.setText(profileUrl);
            }
            else{
                Log.e("ProfileActivity", "No such document");
            }
        }).addOnFailureListener(error->{
            Log.e("ProfileActivity", "Error fetching document", error);
        });
    }

    }