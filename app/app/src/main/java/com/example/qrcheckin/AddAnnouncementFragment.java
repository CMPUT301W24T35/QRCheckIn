package com.example.qrcheckin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
* This is a class that Display a dialog to allow the user to add an announcement
*/ 
    
public class AddAnnouncementFragment extends DialogFragment {
    interface AddAnnouncementDialogListener {
        void addAnnouncement(Announcement announcement);
    }
    private AddAnnouncementDialogListener listener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddAnnouncementDialogListener) {
            listener = (AddAnnouncementDialogListener) context;
        } else {
            throw new RuntimeException(context + " must implement AddAnnouncementDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_announcement, null);
        EditText announcementText = view.findViewById(R.id.add_announcement_text);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        return builder.setView(view).setTitle("Add an announcement").setNegativeButton("Cancel", null)
                .setPositiveButton("Add", (dialog, which) -> {
                    String announcement = announcementText.getText().toString();
                    listener.addAnnouncement(new Announcement(announcement));
                }).create();
    }
}
