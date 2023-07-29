package com.gokulsundar4545.connectwithpeople.Model;

public class User {

    private String Email, Password, Name, Profission;
    private String Cover_photo;
    private String UserID;
    private String Profile_photo;
    private String uid;
    private String department;
    private String onlinestatus;
    private String typingstatus;
    private String token;
    private String status;
    private int followerCount;


    public User(){

    }

    public User(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public User(String email, String password, String name, String profission, String cover_photo, String userID, String profile_photo, String uid, String onlineStatus, String typingTo, int followerCount,String status) {
        Email = email;
        Password = password;
        Name = name;
        Profission = profission;
        Cover_photo = cover_photo;
        this.status = status;

        UserID = userID;
        Profile_photo = profile_photo;
        this.uid = uid;
        this.onlinestatus = onlineStatus;
        this.typingstatus= typingTo;
        this.followerCount = followerCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getProfission() {
        return Profission;
    }

    public void setProfission(String profission) {
        Profission = profission;
    }

    public String getCover_photo() {
        return Cover_photo;
    }

    public void setCover_photo(String cover_photo) {
        Cover_photo = cover_photo;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getProfile_photo() {
        return Profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        Profile_photo = profile_photo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getOnlineStatus() {
        return onlinestatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlinestatus = onlineStatus;
    }

    public String getTypingTo() {
        return typingstatus;
    }

    public void setTypingTo(String typingTo) {
        this.typingstatus = typingTo;
    }

    public int getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(int followerCount) {
        this.followerCount = followerCount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}


