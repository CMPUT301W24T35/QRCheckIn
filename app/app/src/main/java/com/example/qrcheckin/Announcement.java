package com.example.qrcheckin;
/**
* This is a class that maintain the text content of an announcement
* Responsible for adding or modifying the announcement
*/ 
public class Announcement {
    private String announcement;

    public Announcement(String announcement) {
        this.announcement = announcement;
    }

    public String getAnnouncement() {
        return announcement;
    }

    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }
}
