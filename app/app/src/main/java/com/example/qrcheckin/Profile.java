package com.example.qrcheckin;

import java.util.ArrayList;
import java.util.HashMap;

public class Profile {

    private String userID; // Use for firebase reference
    private String userName;
    private String phone;
    private String email;
    private String homepage;

    // This keeps track of the checked in events and the number of times
    // the attendee has checked into that event.
    private ArrayList<HashMap<Profile, Integer>> checkedInEventsAndCheckedInCount;



    // Constructor
    public Profile(String userName, String phone, String email, String homepage) {
        this.userName = userName;
        this.phone = phone;
        this.email = email;
        this.homepage = homepage;
    }

    // Getters and Setters
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public ArrayList<HashMap<Profile, Integer>> getCheckedInEventsAndCheckedInCount() {
        return checkedInEventsAndCheckedInCount;
    }

    public void setCheckedInEventsAndCheckedInCount(ArrayList<HashMap<Profile, Integer>> checkedInEventsAndCheckedInCount) {
        this.checkedInEventsAndCheckedInCount = checkedInEventsAndCheckedInCount;
    }

    // TODO implement method to update number of times checked in
}
