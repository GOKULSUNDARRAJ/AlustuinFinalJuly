package com.gokulsundar4545.connectwithpeople.Model;

public class VedioMode {
    private String vedioId; // This should be the push ID
    private String vedioUrl;
    private String vedioDescription;
    private String vedioBy;
    private int vediocommentCount;
    private int vediopostLike;
    private long vedioposterAt;
    private boolean isLiked;
    public VedioMode() {

    }

    public VedioMode(String vedioId, String vedioUrl, String vedioDescription, String vedioBy, int vediocommentCount, int vediopostLike, long vedioposterAt, boolean isLiked) {
        this.vedioId = vedioId;
        this.vedioUrl = vedioUrl;
        this.vedioDescription = vedioDescription;
        this.vedioBy = vedioBy;
        this.vediocommentCount = vediocommentCount;
        this.vediopostLike = vediopostLike;
        this.vedioposterAt = vedioposterAt;
        this.isLiked = isLiked;
    }


    public String getVedioId() {
        return vedioId;
    }

    public void setVedioId(String vedioId) {
        this.vedioId = vedioId;
    }

    public String getVedioUrl() {
        return vedioUrl;
    }

    public void setVedioUrl(String vedioUrl) {
        this.vedioUrl = vedioUrl;
    }

    public String getVedioDescription() {
        return vedioDescription;
    }

    public void setVedioDescription(String vedioDescription) {
        this.vedioDescription = vedioDescription;
    }

    public String getVedioBy() {
        return vedioBy;
    }

    public void setVedioBy(String vedioBy) {
        this.vedioBy = vedioBy;
    }

    public int getVediocommentCount() {
        return vediocommentCount;
    }

    public void setVediocommentCount(int vediocommentCount) {
        this.vediocommentCount = vediocommentCount;
    }

    public int getVediopostLike() {
        return vediopostLike;
    }

    public void setVediopostLike(int vediopostLike) {
        this.vediopostLike = vediopostLike;
    }

    public long getVedioposterAt() {
        return vedioposterAt;
    }

    public void setVedioposterAt(long vedioposterAt) {
        this.vedioposterAt = vedioposterAt;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }
}
