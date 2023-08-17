package com.gokulsundar4545.connectwithpeople.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserStories implements Parcelable {

    private String image;
    private long storyAt;
    private String statusCaption;

    // Default constructor required for Firebase
    public UserStories() {
    }

    public UserStories(String image, long storyAt, String statusCaption) {
        this.image = image;
        this.storyAt = storyAt;
        this.statusCaption = statusCaption;
    }

    protected UserStories(Parcel in) {
        image = in.readString();
        storyAt = in.readLong();
        statusCaption = in.readString();
    }

    public static final Creator<UserStories> CREATOR = new Creator<UserStories>() {
        @Override
        public UserStories createFromParcel(Parcel in) {
            return new UserStories(in);
        }

        @Override
        public UserStories[] newArray(int size) {
            return new UserStories[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeLong(storyAt);
        dest.writeString(statusCaption);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }

    public String getStatusCaption() {
        return statusCaption;
    }

    public void setStatusCaption(String statusCaption) {
        this.statusCaption = statusCaption;
    }
}
