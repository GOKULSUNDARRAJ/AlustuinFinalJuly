package com.gokulsundar4545.connectwithpeople;
public class Followers {
    private String id;
    private String name;

    // Default constructor required for calls to DataSnapshot.getValue(Following.class)
    public Followers() {
    }

    public Followers(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
