package com.example.qrcheckin;

/**
* This is a class that is responsible for maintaining the text content of an announcement
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
