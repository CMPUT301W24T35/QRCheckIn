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

public class EventArrayAdapter extends ArrayAdapter<Event> {
    public EventArrayAdapter(Context context, ArrayList<Event> cities) {
        super(context, 0, cities);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.event_content,
                    parent, false);
        } else {
            view = convertView;
        }
        Event event = getItem(position);
        //prefill the texts
        TextView eventName = view.findViewById(R.id.event_text);
        TextView organizerName = view.findViewById(R.id.organizer_text);

        eventName.setText(event.getEventName());
        organizerName.setText(event.getOrganizerName());

        return view;
    }
}
