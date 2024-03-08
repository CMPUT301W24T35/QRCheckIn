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
* Keep user sign-up events
*/

public class SignedUpEventAdapter extends ArrayAdapter<Event> {
    public SignedUpEventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.signed_up_event_content,
                    parent, false);
        } else {
            view = convertView;
        }
        Event event = getItem(position);
        //prefill the texts
        TextView eventName = view.findViewById(R.id.event_text);
        TextView checkStatus = view.findViewById(R.id.checkIn_text);

        eventName.setText(event.getName());
        checkStatus.setText(event.isCheckInStatus()?"CheckedIn":"");

        return view;
    }
}
