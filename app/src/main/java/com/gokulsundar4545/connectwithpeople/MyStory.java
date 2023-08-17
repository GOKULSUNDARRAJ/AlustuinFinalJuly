package com.gokulsundar4545.connectwithpeople;

import java.io.Serializable;
import java.util.Date;

public class MyStory implements Serializable {

    private String url;          // URL of the story image or media
    private Date date;           // Date associated with the story (optional)
    private String description;  // Description or caption for the story (optional)
    private String storyId;      // Unique ID for the story

    // Constructors

    // Full constructor with all fields
    public MyStory(String url, Date date, String description, String storyId) {
        this.url = url;
        this.date = date;
        this.description = description;
        this.storyId = storyId;
    }

    // Constructor without description
    public MyStory(String url, Date date, String storyId) {
        this.url = url;
        this.date = date;
        this.storyId = storyId;
    }

    // Constructor without date and description
    public MyStory(String url, String storyId) {
        this.url = url;
        this.storyId = storyId;
    }

    // Constructor without storyId (assuming it's set separately)
    public MyStory(String url) {
        this.url = url;
    }

    // Empty constructor
    public MyStory() {
    }

    // Getter and Setter methods

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }
}
