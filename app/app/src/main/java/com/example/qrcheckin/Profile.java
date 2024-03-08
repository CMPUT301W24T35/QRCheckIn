package com.example.qrcheckin;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The Profile class represents a user profile within the application,
 * containing information such as user identification, name, phone number,etc.
 */

public class Profile {

    private String userID; // Use for firebase reference
    private String userName;
    private String phone;
    private String email;
    private String homepage;

    // This keeps track of the checked in events and the number of times
    // the attendee has checked into that event.
    private ArrayList<HashMap<Event, Integer>> checkedInEventsAndCheckedInCount;



    // Constructor
    public Profile(String userName, String phone, String email, String homepage) {
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.homepage = homepage;
    }

    // Getters and Setters

    /**
     * Returns the Firebase reference user ID.
     *
     * @return the user ID.
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Sets the user's Firebase reference ID.
     *
     * @param userID the new user ID to set.
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Returns the name of the user.
     *
     * @return the user name.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Sets the name of the user.
     * @param userName the new name to set.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns the phone number of the user.
     * @return the phone number.
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number of the user.
     * @param phone the new phone number to set.
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the email address of the user.
     * @return email
     */

    public String getEmail() {
        return email;
    }

    /**
     * Returns the email address of the user.
     */

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Returns the homepage URL of the user.
     * @return the homepage URL.
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * Sets the homepage URL of the user.
     * @param homepage the new homepage URL to set.
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public ArrayList<HashMap<Event, Integer>> getCheckedInEventsAndCheckedInCount() {
        return checkedInEventsAndCheckedInCount;
    }

    public void setCheckedInEventsAndCheckedInCount(ArrayList<HashMap<Event, Integer>> checkedInEventsAndCheckedInCount) {
        this.checkedInEventsAndCheckedInCount = checkedInEventsAndCheckedInCount;
    }

    // TODO implement method to update number of times checked in
}
