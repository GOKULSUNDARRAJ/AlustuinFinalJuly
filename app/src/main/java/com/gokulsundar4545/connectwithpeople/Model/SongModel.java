package com.gokulsundar4545.connectwithpeople.Model;

public class SongModel {
    private String key;
    private String id;
    private String title;
    private String subtitle;
    private String url;
    private String coverUrl;
    private String lyrics;
    private String artist;
    private String name;
    private Long count;


    // No-argument constructor required for Firebase Realtime Database
    public SongModel() {
        // Default constructor required for calls to DataSnapshot.getValue(SongModel.class)
    }

    public SongModel(String key, String id, String title, String subtitle, String url, String coverUrl, String lyrics, String artist, String name, Long count) {
        this.key = key;
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.url = url;
        this.coverUrl = coverUrl;
        this.lyrics = lyrics;
        this.artist = artist;
        this.name = name;
        this.count = count;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
