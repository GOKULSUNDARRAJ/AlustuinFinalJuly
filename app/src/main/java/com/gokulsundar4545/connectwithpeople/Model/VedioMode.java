package com.gokulsundar4545.connectwithpeople.Model;

public class VedioMode {

    String vedioname;
    String vedioUrl;
    String search;
    private String VedioId;
    private String vedioBy;
    private String vedioDescription;
    private long vedioposterAt;
    private int vediopostLike;

    private int vediocommentCount;

    public VedioMode(String vedioname, String vedioUrl, String search, String vedioId, String vedioBy, String vedioDescription, long vedioposterAt, int vediopostLike, int vediocommentCount) {
        this.vedioname = vedioname;
        this.vedioUrl = vedioUrl;
        this.search = search;
        VedioId = vedioId;
        this.vedioBy = vedioBy;
        this.vedioDescription = vedioDescription;
        this.vedioposterAt = vedioposterAt;
        this.vediopostLike = vediopostLike;
        this.vediocommentCount = vediocommentCount;
    }

    public String getVedioname() {
        return vedioname;
    }

    public void setVedioname(String vedioname) {
        this.vedioname = vedioname;
    }

    public String getVedioUrl() {
        return vedioUrl;
    }

    public void setVedioUrl(String vedioUrl) {
        this.vedioUrl = vedioUrl;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getVedioId() {
        return VedioId;
    }

    public void setVedioId(String vedioId) {
        VedioId = vedioId;
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

    public long getVedioposterAt() {
        return vedioposterAt;
    }

    public void setVedioposterAt(long vedioposterAt) {
        this.vedioposterAt = vedioposterAt;
    }

    public int getVediopostLike() {
        return vediopostLike;
    }

    public void setVediopostLike(int vediopostLike) {
        this.vediopostLike = vediopostLike;
    }

    public int getVediocommentCount() {
        return vediocommentCount;
    }

    public void setVediocommentCount(int vediocommentCount) {
        this.vediocommentCount = vediocommentCount;
    }

    public VedioMode() {
    }
}
