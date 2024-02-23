package com.example.qrcheckin;

import java.io.Serializable;

public class Event implements Serializable {
    private String eventName;
    private String organizerName;
    private int numberAttendees;

    //There are more need to implement

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }

    public int getNumberAttendees() {
        return numberAttendees;
    }

    public void setNumberAttendees(int numberAttendees) {
        this.numberAttendees = numberAttendees;
    }
}
