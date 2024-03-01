package com.example.qrcheckin;

import java.util.Collection;
import java.util.Date;

public class Event {
    private String eventID; // Use for Firebase document reference
    private String name;
    private String description; // TODO - Add to figma etc
    private Date startTime;
    private Date endTime;
    private String Location;
    private Integer attendeeCapacity;
    private String organizerID; // ID string for Firebase (use built in firebase functionality to generate this)

    // TODO private ArrayList<Announcement> announcements;
    // TODO private ArrayList<Attendee> attendees;


    // Constructor
    public Event(String eventName, String description, Date start, Date end, String Location){
        this.name = eventName;
        this.description = description;
        this.startTime = start;
        this.endTime = end;
        this.Location = Location;

    }
    // Method overload for when number of attendees is OPTIONALLY limited
    public Event(String eventName, String description, Date start, Date end, String Location, Integer attendeeCapacity){
        this.name = eventName;
        this.description = description;
        this.startTime = start;
        this.endTime = end;
        this.Location = Location;
        this.attendeeCapacity = attendeeCapacity;

    }

    // TODO checkCapacity method
    //  - when capacity is reached no longer accept attendees to signup
    //  - Need to compare Attendee List and attendeeCapacity

    // Getters & Setters
    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public Integer getAttendeeCapacity() {
        return attendeeCapacity;
    }

    public void setAttendeeCapacity(Integer attendeeCapacity) {
        this.attendeeCapacity = attendeeCapacity;
    }

    public String getOrganizerID() {
        return organizerID;
    }

    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }
}

