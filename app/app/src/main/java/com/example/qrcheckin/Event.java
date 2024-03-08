package com.example.qrcheckin;

import java.util.ArrayList;

/**
* Represents an event class with attributes like name, description
 * timing of the event and other useful details. Also includes the getter and setter methods.
*/

public class Event {
    private String eventID; // Use for Firebase document reference
    private String name;
    private String description; // TODO - Add to figma etc
    private String startTime;
    private String endTime;
    private String Location;

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getPromoQR() {
        return promoQR;
    }

    public void setPromoQR(String promoQR) {
        this.promoQR = promoQR;
    }

    private String qrCode;
    private String poster;
    private String promoQR;
    private Integer attendeeCapacity;
    private String organizerID; // ID string for Firebase (use built in firebase functionality to generate this)
    private ArrayList<Profile> SignedUpAttendees;
    private ArrayList<Profile> CheckedInAttendees;

    // TODO private ArrayList<Announcement> announcements;

    private boolean CheckInStatus = false;

    /**
     *Constructors defined to create an instance of the class
     */
    public Event(){

    }

    // Constructor no attendee capacity with promo
    public Event(String eventName, String description, String start, String end, String Location, String qrCode, String promoQR, String poster, String organizerID){
        this.name = eventName;
        this.description = description;
        this.startTime = start;
        this.endTime = end;
        this.Location = Location;
        this.qrCode = qrCode;
        this.promoQR = promoQR;
        this.poster = poster;
        this.organizerID = organizerID;
    }

    // Constructor no attendee capacity without promo
    public Event(String eventName, String description, String start, String end, String Location, String qrCode, String poster, String organizerID){
        this.name = eventName;
        this.description = description;
        this.startTime = start;
        this.endTime = end;
        this.Location = Location;
        this.qrCode = qrCode;
        this.poster = poster;
        this.organizerID = organizerID;
    }

    // Method overload for when number of attendees is OPTIONALLY limited with promo
    public Event(String eventName, String description, String start, String end, String Location, Integer attendeeCapacity, String qrCode, String promoQR, String poster, String organizerID){
        this.name = eventName;
        this.description = description;
        this.startTime = start;
        this.endTime = end;
        this.Location = Location;
        this.attendeeCapacity = attendeeCapacity;
        this.qrCode = qrCode;
        this.promoQR = promoQR;
        this.poster = poster;
        this.organizerID = organizerID;
    }

    // Method overload for when number of attendees is OPTIONALLY limited without promo
    public Event(String eventName, String description, String start, String end, String Location, Integer attendeeCapacity, String qrCode, String poster, String organizerID){
        this.name = eventName;
        this.description = description;
        this.startTime = start;
        this.endTime = end;
        this.Location = Location;
        this.attendeeCapacity = attendeeCapacity;
        this.qrCode = qrCode;
        this.poster = poster;
        this.organizerID = organizerID;
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
     * @param attendee The profile of attendee to sign up
     * @throws Exception if the capacity is already reached
     */
    public void signUpAttendee(Profile attendee) throws Exception {
        if(reachedCapacity()){
            throw new Exception("Event has reached capacity");
        }
        else {
            SignedUpAttendees.add(attendee);
        }
    }

    /**
     * Retrieves the ID for the event.
     *
     * @return A string representing the event ID.
     */
    // Getters & Setters
    public String getEventID() {
        return eventID;
    }

    /**
     * Sets the ID for the event.
     *
     * @param eventID A string containing the event ID.
     */
    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    /**
     * Retrieves the name for the event.
     *
     * @return A string representing the event name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name for the event.
     *
     * @param name A string containing the event name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retrieves the description for the event.
     *
     * @return A string representing the event description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description for the event.
     *
     * @param description A string containing the event description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retrieves the start time for the event.
     *
     * @return A string representing the event start time.
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * Sets the start time for the event.
     *
     * @param startTime A string containing the event start time.
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * Retrieves the end time for the event.
     *
     * @return A string representing the event end time.
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * Sets the end time for the event.
     *
     * @param endTime A string containing the event end time.
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * Retrieves the location for the event.
     *
     * @return A string representing the event location.
     */
    public String getLocation() {
        return Location;
    }

    /**
     * Sets the location for the event.
     *
     * @param location A string containing the event location.
     */

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

