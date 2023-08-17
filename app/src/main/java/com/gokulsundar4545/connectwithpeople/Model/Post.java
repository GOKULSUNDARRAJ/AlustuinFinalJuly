package com.gokulsundar4545.connectwithpeople.Model;

public class Post {

    private String postId;
    private String postImg;
    private String postedBy;
    private String postDescription;
    private long posterAt;
    private int postLike;
    private int commentCount;
    private String coverUrl;
    private String title;
    private String subtitle;
    private String songurl;

    private String postType;

    public Post() {
    }

    public Post(String postId, String postImg, String postedBy, String postDescription, long posterAt, int postLike, int commentCount, String coverUrl, String title, String subtitle, String songurl, String postType) {
        this.postId = postId;
        this.postImg = postImg;
        this.postedBy = postedBy;
        this.postDescription = postDescription;
        this.posterAt = posterAt;
        this.postLike = postLike;
        this.commentCount = commentCount;
        this.coverUrl = coverUrl;
        this.title = title;
        this.subtitle = subtitle;
        this.songurl = songurl;
        this.postType = postType;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostImg() {
        return postImg;
    }

    public void setPostImg(String postImg) {
        this.postImg = postImg;
    }

    public String getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public long getPosterAt() {
        return posterAt;
    }

    public void setPosterAt(long posterAt) {
        this.posterAt = posterAt;
    }

    public int getPostLike() {
        return postLike;
    }

    public void setPostLike(int postLike) {
        this.postLike = postLike;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
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

    public String getSongurl() {
        return songurl;
    }

    public void setSongurl(String songurl) {
        this.songurl = songurl;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }
}
