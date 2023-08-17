package com.gokulsundar4545.connectwithpeople;

public class Highlight {
    private String highlightId;
    private String caption;
    private String imageUrl;
    private long storyAt;

    public Highlight() {
        // Default constructor required for Firebase
    }

    public Highlight(String highlightId, String caption, String imageUrl, long storyAt) {
        this.highlightId = highlightId;
        this.caption = caption;
        this.imageUrl = imageUrl;
        this.storyAt = storyAt;
    }

    public String getHighlightId() {
        return highlightId;
    }

    public void setHighlightId(String highlightId) {
        this.highlightId = highlightId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }
}
