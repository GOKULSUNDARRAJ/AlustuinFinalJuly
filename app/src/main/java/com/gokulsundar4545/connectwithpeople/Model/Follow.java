package com.gokulsundar4545.connectwithpeople.Model;

public class Follow {

    private String id;
    private String name;


    public Follow(){

    }

    public Follow(String id, String name) {
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
