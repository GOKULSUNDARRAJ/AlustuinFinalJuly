package com.gokulsundar4545.connectwithpeople.Model;

public class Storynew {

    private String imageUrl;
    private long timestart;
    private  long timeEnd;
    private String storyid;
    private String userid;

    public Storynew(String imageUrl, long timestart, long timeEnd, String storyid, String userid) {
        this.imageUrl = imageUrl;
        this.timestart = timestart;
        this.timeEnd = timeEnd;
        this.storyid = storyid;
        this.userid = userid;
    }

    public Storynew(){

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getStoryid() {
        return storyid;
    }

    public void setStoryid(String storyid) {
        this.storyid = storyid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }
}
