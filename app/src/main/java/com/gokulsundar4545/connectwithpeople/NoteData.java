package com.gokulsundar4545.connectwithpeople;

public class NoteData {
    private String coverUrl;
    private String notes;
    private String songurl;
    private String subtitle;
    private String title;
    private String uid;

    // No-argument constructor required for Firebase
    public NoteData() {
    }

    public NoteData(String coverUrl, String notes, String songurl, String subtitle, String title, String uid) {
        this.coverUrl = coverUrl;
        this.notes = notes;
        this.songurl = songurl;
        this.subtitle = subtitle;
        this.title = title;
        this.uid = uid;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getSongurl() {
        return songurl;
    }

    public void setSongurl(String songurl) {
        this.songurl = songurl;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}

