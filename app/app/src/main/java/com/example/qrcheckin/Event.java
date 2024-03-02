package com.example.qrcheckin;

import java.util.ArrayList;
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
    private ArrayList<Profile> SignedUpAttendees;
    private ArrayList<Profile> CheckedInAttendees;

    // TODO private ArrayList<Announcement> announcements;

    private boolean CheckInStatus = false;

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

    /**
     * Helper method that checks if the event has reached capacity
     * @return true / false
     */
    public boolean reachedCapacity(){
        if(attendeeCapacity - SignedUpAttendees.size() == 0 ){
            return true;
        }
        else{
            return false;
        }

    }

    /**
     * Signs up attendee to Signed Up Attendee list, as long as there is space.
     * If there is no space it throws an exception - remember to handle this!
     * @param attendee
     *
     */
    public void signUpAttendee(Profile attendee) throws Exception {
        if(reachedCapacity()){
            throw new Exception("Event has reached capacity");
        }
        else {
            SignedUpAttendees.add(attendee);
        }
    }

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

    public ArrayList<Profile> getSignedUpAttendees() {
        return SignedUpAttendees;
    }

    public void setSignedUpAttendees(ArrayList<Profile> signedUpAttendees) {
        SignedUpAttendees = signedUpAttendees;
    }

    public ArrayList<Profile> getCheckedInAttendees() {
        return CheckedInAttendees;
    }

    public void setCheckedInAttendees(ArrayList<Profile> checkedInAttendees) {
        CheckedInAttendees = checkedInAttendees;
    }

    public String getOrganizerID() {
        return organizerID;
    }

    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }

    public boolean isCheckInStatus() {
        return CheckInStatus;
    }

    public void setCheckInStatus(boolean checkInStatus) {
        CheckInStatus = checkInStatus;
    }
}

