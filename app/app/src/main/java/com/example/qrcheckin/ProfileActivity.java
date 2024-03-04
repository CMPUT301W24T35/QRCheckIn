package com.example.qrcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    Button edit;
    Button back;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        //TODO:Add name, email, and phone
        TextView userName = findViewById(R.id.user_name_input);
        TextView userEmail = findViewById(R.id.user_email_input);
        TextView userPhone = findViewById(R.id.user_phoneNumber_input);

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
                Intent intent = new Intent(ProfileActivity.this, CreateProfileActivity.class);// go to event activity need to connect with other activity
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