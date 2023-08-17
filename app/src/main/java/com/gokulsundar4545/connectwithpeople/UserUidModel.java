package com.gokulsundar4545.connectwithpeople;

public class UserUidModel {
    private String currentUserUid;

    public UserUidModel() {
        // Empty constructor needed for Firebase
    }

    public UserUidModel(String currentUserUid) {
        this.currentUserUid = currentUserUid;
    }

    public String getCurrentUserUid() {
        return currentUserUid;
    }

    public void setCurrentUserUid(String currentUserUid) {
        this.currentUserUid = currentUserUid;
    }
}
