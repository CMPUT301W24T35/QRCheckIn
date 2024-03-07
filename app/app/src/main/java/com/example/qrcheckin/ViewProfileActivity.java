package com.example.qrcheckin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

/**
* This is a class that displays the user's profile information, such as name, email, phone number, etc
*/ 

public class ViewProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
    }
}
