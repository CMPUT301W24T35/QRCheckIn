package com.example.qrcheckin;

/**
 * This class defines Profile
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

    /**
     * @return The user ID for this user
     */
    public String getUserID() {
        return userID;
    }

    /**
     * @param userID The string to be set as user ID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * @return The user name for this user
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the String to be set as username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return The phone number for this user
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the string to be set as phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return The email address for this user
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the string to be set as user email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the homepage url for this user
     */
    public String getHomepage() {
        return homepage;
    }

    /**
     * @param homepage the string to be set as the homepage url
     */
    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }
}
