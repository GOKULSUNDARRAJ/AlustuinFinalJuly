package com.gokulsundar4545.connectwithpeople.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Story implements Parcelable {

    private String storyBy;
    private long storyAt;
    private ArrayList<UserStories> stories;
    private String statusCaption;
    private long timestart;
    private long timeend;
    private String storyId;

    // Default constructor required for Firebase
    public Story() {
    }

    public String getStoryId() {
        return storyId;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public long getTimestart() {
        return timestart;
    }

    public void setTimestart(long timestart) {
        this.timestart = timestart;
    }

    public long getTimeend() {
        return timeend;
    }

    public void setTimeend(long timeend) {
        this.timeend = timeend;
    }

    public String getStatusCaption() {
        return statusCaption;
    }

    public void setStatusCaption(String statusCaption) {
        this.statusCaption = statusCaption;
    }

    public String getStoryBy() {
        return storyBy;
    }

    public void setStoryBy(String storyBy) {
        this.storyBy = storyBy;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }

    public ArrayList<UserStories> getStories() {
        return stories;
    }

    public void setStories(ArrayList<UserStories> stories) {
        this.stories = stories;
    }

    // Parcelable implementation

    protected Story(Parcel in) {
        storyBy = in.readString();
        storyAt = in.readLong();
        stories = in.createTypedArrayList(UserStories.CREATOR);
        statusCaption = in.readString();
        timestart = in.readLong();
        timeend = in.readLong();
        storyId = in.readString();
    }

    public static final Creator<Story> CREATOR = new Creator<Story>() {
        @Override
        public Story createFromParcel(Parcel in) {
            return new Story(in);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(storyBy);
        dest.writeLong(storyAt);
        dest.writeTypedList(stories);
        dest.writeString(statusCaption);
        dest.writeLong(timestart);
        dest.writeLong(timeend);
        dest.writeString(storyId);
    }
}
