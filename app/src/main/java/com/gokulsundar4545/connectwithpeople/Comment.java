package com.gokulsundar4545.connectwithpeople;

public class Comment {
    private String text;
    private long timestamp;
    private String userId;

    // Default constructor required for calls to DataSnapshot.getValue(Comment.class)
    public Comment() {
    }

    public Comment(String text, long timestamp, String userId) {
        this.text = text;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
