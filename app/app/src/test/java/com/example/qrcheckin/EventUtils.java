package com.example.qrcheckin;

import android.os.Bundle;

//test viewEvent isAttendee()
public class EventUtils {

    public static boolean isAttendee(Bundle bundle) {
        if (bundle != null) {
            String origin = bundle.getString("origin");
            return "attendee".equals(origin);
        }
        return false;
    }
}
