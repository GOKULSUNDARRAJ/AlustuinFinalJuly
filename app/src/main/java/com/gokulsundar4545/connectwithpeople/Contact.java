package com.gokulsundar4545.connectwithpeople;

public class Contact {
    private String phoneNumber;
    private String uid;

    public Contact(String phoneNumber, String uid) {
        this.phoneNumber = phoneNumber;
        this.uid = uid;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getUid() {
        return uid;
    }
}
