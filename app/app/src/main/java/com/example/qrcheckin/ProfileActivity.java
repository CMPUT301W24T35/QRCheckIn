package com.example.qrcheckin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    Button edit;
    Button back;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        //Bundle extras = getIntent().getExtras();
        TextView userName = findViewById(R.id.user_name_input);
        TextView userEmail = findViewById(R.id.user_email_input);
        TextView userPhone = findViewById(R.id.user_phoneNumber_input);
        CircleImageView userImage = findViewById(R.id.profile_image_button);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String name = extras.getString("name", ""); // Default to empty if not found
            String email = extras.getString("email", "");
            String phone = extras.getString("phone", "");
            String profileImageBase64 = extras.getString("profileImage", "");

            // Set the extracted information to your views
            userName.setText(name);
            userEmail.setText(email);
            userPhone.setText(phone);

            // Check if there is a profile image provided and it's not empty
            if (!profileImageBase64.isEmpty()) {
                // Decode the Base64 encoded string to a Bitmap
                byte[] decodedString = Base64.decode(profileImageBase64, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                // Set the Bitmap to the ImageView
                userImage.setImageBitmap(decodedByte);
            }
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
                startActivity(intent);
            }
        });
        back = findViewById(R.id.button_back_events);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //feel free to use the code below to connect to the activity
                Intent intent = new Intent(ProfileActivity.this, HomepageActivity.class);// go to event activity need to connect with other activity
                startActivity(intent);
            }
        });
    }
}