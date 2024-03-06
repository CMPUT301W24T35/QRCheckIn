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
import java.util.List;

public class AttendeeAdapter extends ArrayAdapter<Profile> {
    private Context context;
    private ArrayList<Profile> attendees;

    public AttendeeAdapter(@NonNull Context context, ArrayList<Profile> attendees) {
        super(context,0, attendees);
        this.attendees = attendees;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.attendee_content,
                    parent, false);
        } else {
            view = convertView;
        }
        Profile attendee = getItem(position);
        TextView attendeeName = view.findViewById(R.id.attendee_text);

        assert attendee != null;
        attendeeName.setText(attendee.getUserName());
        return view;
    }
}
