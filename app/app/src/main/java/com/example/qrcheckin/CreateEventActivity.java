package com.example.qrcheckin;

import android.os.Bundle;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;





public class CreateEventActivity extends AppCompatActivity {
    EditText newEventName;
    EditText newStartTime;
    EditText newEndTime;
    EditText newLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        // Bind UI
        //TODO posterImage = findViewById(R.id.posterImageView);
        //TODO - EDIT POSTER button

        newEventName = findViewById(R.id.eventNameEditText);
        newStartTime = findViewById(R.id.eventStartTimeEditText);
        newEndTime = findViewById(R.id.eventEndTimeEditText);
        newLocation = findViewById(R.id.eventLocationEditText);

        // TODO On Confirm button click
        //     send to new fragment
        //     generate new QR code
        //

    }
}
