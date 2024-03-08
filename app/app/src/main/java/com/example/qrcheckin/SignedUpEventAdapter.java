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
 * An ArrayAdapter is an implementation for displaying a list of events
 * that a user has signed up for. This adapter is responsible for
 * converting an Event object into a view.
 * @see ArrayAdapter<Event> array apadter for event
 */

public class SignedUpEventAdapter extends ArrayAdapter<Event> {
    /**
     * Constructs a new SignedUpEventAdapter
     * @param context The current context.
     * @param events  A list of Event objects to display in a list.
     */
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
