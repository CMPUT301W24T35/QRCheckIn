package com.example.qrcheckin;

/**
* This is a class that stores and manages user personal information
*/ 

public class Profile {

    private String userID; // Use for firebase reference
    private String userName;
    private String phone;
    private String email;
    private String homepage;

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
}
