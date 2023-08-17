package com.gokulsundar4545.connectwithpeople.Model;

public class UserStories {

    private String image;
    private long storyAt;
    private String statusCaption;

    public UserStories() {
    }

    public UserStories(String statusCaption) {
        this.statusCaption = statusCaption;
    }

    public String getStatusCaption() {
        return statusCaption;
    }

    public void setStatusCaption(String statusCaption) {
        this.statusCaption = statusCaption;
    }

    public UserStories(String image, long storyAt, String statusCaption) {
        this.image = image;
        this.storyAt = storyAt;
        this.statusCaption = statusCaption;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }
}
