package com.gokulsundar4545.connectwithpeople;



public class RealVideoModel {

    private String videoId;
    private String vedioBy;
    private String vedioDescription;
    private String vedioUrl;
    private int vedioCommentCount;
    private int vedioPostLike;
    private long vedioPosterAt;

    public RealVideoModel() {
        // Default constructor required for calls to DataSnapshot.getValue(RealVideoModel.class)
    }

    public RealVideoModel(String videoId, String vedioBy, String vedioDescription, String vedioUrl, int vedioCommentCount, int vedioPostLike, long vedioPosterAt) {
        this.videoId = videoId;
        this.vedioBy = vedioBy;
        this.vedioDescription = vedioDescription;
        this.vedioUrl = vedioUrl;
        this.vedioCommentCount = vedioCommentCount;
        this.vedioPostLike = vedioPostLike;
        this.vedioPosterAt = vedioPosterAt;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVedioBy() {
        return vedioBy;
    }

    public void setVedioBy(String vedioBy) {
        this.vedioBy = vedioBy;
    }

    public String getVedioDescription() {
        return vedioDescription;
    }

    public void setVedioDescription(String vedioDescription) {
        this.vedioDescription = vedioDescription;
    }

    public String getVedioUrl() {
        return vedioUrl;
    }

    public void setVedioUrl(String vedioUrl) {
        this.vedioUrl = vedioUrl;
    }

    public int getVedioCommentCount() {
        return vedioCommentCount;
    }

    public void setVedioCommentCount(int vedioCommentCount) {
        this.vedioCommentCount = vedioCommentCount;
    }

    public int getVedioPostLike() {
        return vedioPostLike;
    }

    public void setVedioPostLike(int vedioPostLike) {
        this.vedioPostLike = vedioPostLike;
    }

    public long getVedioPosterAt() {
        return vedioPosterAt;
    }

    public void setVedioPosterAt(long vedioPosterAt) {
        this.vedioPosterAt = vedioPosterAt;
    }
}
