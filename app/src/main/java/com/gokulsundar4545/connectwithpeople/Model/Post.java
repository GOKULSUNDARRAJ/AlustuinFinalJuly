package com.gokulsundar4545.connectwithpeople.Model;

public class Post {


    private String postId;
    private String postImg;
    private String postedBy;
    private String postDescription;
    private long posterAt;
    private int postLike;

    private int commentCount;
    public Post(int commentCount) {
        this.commentCount = commentCount;
    }






    public Post(String postId, String postImg, String postedBy, String postDescription, long posterAt) {
        this.postId = postId;
        this.postImg = postImg;
        this.postedBy = postedBy;
        this.postDescription = postDescription;
        this.posterAt = posterAt;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getPostLike() {
        return postLike;
    }

    public void setPostLike(int postLike) {
        this.postLike = postLike;
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

    public  Post(){

    }
}
