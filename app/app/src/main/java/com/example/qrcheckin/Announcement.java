package com.example.qrcheckin;

/**
 * This class defines Announcements
 */
public class Announcement {
    private String announcement;

    public Announcement(String announcement) {
        this.announcement = announcement;
    }

    /**
     * @return the string message for this announcement
     */
    public String getAnnouncement() {
        return announcement;
    }

    /**
     * @param announcement the string message to be set as announcement
     */
    public void setAnnouncement(String announcement) {
        this.announcement = announcement;
    }
}
