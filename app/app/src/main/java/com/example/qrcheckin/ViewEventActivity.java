package com.example.qrcheckin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

public class ViewEventActivity extends AppCompatActivity implements AddAnnouncementFragment.AddAnnouncementDialogListener {

    ImageView posterImage;
    TextView eventDescription;
    TextView eventStartTime;
    TextView eventEndTime;
    TextView eventLocation;
    Button editEventBtn;
    Button viewMapBtn;
    ImageButton share;
    ImageButton addAnnouncement;
    ImageView qrCodeImage;
    private FirebaseFirestore db;
    private ArrayList<Announcement> announcementDataList;
    private ListView announcementList;
    private AnnouncementsAdapter announcementsAdapter;

    private ArrayList<Profile> attendeeDataList;
    private ListView attendeeList;
    private AttendeeAdapter attendeeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_event);

        posterImage = findViewById(R.id.posterImageView);
        eventDescription = findViewById(R.id.eventDescriptionText);
        eventStartTime = findViewById(R.id.eventStartText);
        eventEndTime = findViewById(R.id.eventEndText);
        eventLocation = findViewById(R.id.eventLocationText);

        editEventBtn = findViewById(R.id.editEventButton);
        viewMapBtn = findViewById(R.id.viewMapButton);
        addAnnouncement = findViewById(R.id.button_add_announcement);
        share = findViewById(R.id.button_share);

        if (isAttendee()) {
            // If it is an attendee, then hide unnecessary info
            ConstraintLayout eventButtons = findViewById(R.id.eventButtons);
            LinearLayout attendeeInfo = findViewById(R.id.attendeesInfo);
            eventButtons.setVisibility(View.INVISIBLE);
            attendeeInfo.setVisibility(View.INVISIBLE);
            addAnnouncement.setVisibility(View.INVISIBLE);
            share.setVisibility(View.INVISIBLE);
        }

        // Edit event
        editEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO Create intent to switch to EditEvent Activity.
            }
        });

        // View map
        viewMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Create a map API to view map with attendees
            }
        });

        qrCodeImage = findViewById(R.id.qrCodeImageView);
        // Share QR code
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Get the QR code of the event from firebase to the variable "bitmap"
                BitmapDrawable drawable = (BitmapDrawable) qrCodeImage.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "QR Code", null);
                Uri imageUri = Uri.parse(path);

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
            }
        });

        announcementDataList = new ArrayList<>();
        announcementList = findViewById(R.id.announcement_list);
        announcementsAdapter = new AnnouncementsAdapter(this, announcementDataList);
        announcementList.setAdapter(announcementsAdapter);

        // Add announcement
        addAnnouncement.setOnClickListener(v -> {
            new AddAnnouncementFragment().show(getSupportFragmentManager(), "Add an announcement");
        });

        // TODO : Add attendee array item connect to firebase
        attendeeDataList = new ArrayList<>();
        attendeeList = findViewById(R.id.attendees_list);
        attendeeAdapter = new AttendeeAdapter(this, attendeeDataList);
        attendeeList.setAdapter(attendeeAdapter);
    }

    @Override
    public void addAnnouncement(Announcement announcement) {
        announcementDataList.add(0, announcement);
        announcementsAdapter.notifyDataSetChanged();
    }
    public boolean isAttendee() {
        // TODO Check if it is an attendee
        return false;
    }
}