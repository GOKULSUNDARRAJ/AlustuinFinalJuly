package com.gokulsundar4545.connectwithpeople.Model;
public class ModelChat {
    String message, sender, receiver, timestamp, type, messageimage, messagetext,position;
    boolean isseen;

    public ModelChat() {
        // Default constructor required for Firebase
    }

    public ModelChat(String message, String sender, String receiver, String timestamp, String type, String messageimage, String messagetext, String position, boolean isseen) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.timestamp = timestamp;
        this.type = type;
        this.messageimage = messageimage;
        this.messagetext = messagetext;
        this.position = position;
        this.isseen = isseen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessageimage() {
        return messageimage;
    }

    public void setMessageimage(String messageimage) {
        this.messageimage = messageimage;
    }

    public String getMessagetext() {
        return messagetext;
    }

    public void setMessagetext(String messagetext) {
        this.messagetext = messagetext;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
