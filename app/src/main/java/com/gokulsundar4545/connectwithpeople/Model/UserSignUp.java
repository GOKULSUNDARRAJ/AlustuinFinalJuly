package com.gokulsundar4545.connectwithpeople.Model;

public class UserSignUp {

    String Email,Password,Name,Profission,Uid,token,status;

    public UserSignUp(String email, String password, String name, String profission, String uid, String token, String status) {
        Email = email;
        Password = password;
        Name = name;
        Profission = profission;
        Uid = uid;
        this.token = token;
        this.status = status;
    }

    public UserSignUp() {
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

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
