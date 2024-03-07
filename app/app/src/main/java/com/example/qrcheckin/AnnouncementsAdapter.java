package com.example.qrcheckin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
* This is a class that responsible for displaying a list of announcements in a ListView or Spinner
*/ 

public class AnnouncementsAdapter extends ArrayAdapter<Announcement> {
    //private Context context;
    //private ArrayList<Announcement> announcements;
    public AnnouncementsAdapter(Context context, ArrayList<Announcement> announcements) {
        super(context, 0, announcements);
        //this.announcements = announcements;
        //this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.announcement_content, parent, false);
        } else {
            view = convertView;
        }
        Announcement announcement = getItem(position);
        TextView announcementMsg = view.findViewById(R.id.announcement_text);

        //assert announcement != null;
        announcementMsg.setText(announcement.getAnnouncement());
        return view;
    }
}
