package com.gokulsundar4545.connectwithpeople;
import android.os.Parcel;
import android.os.Parcelable;

public class UserStory implements Parcelable {
    private String image;
    private String statusCaption;
    private long storyAt;

    // No-argument constructor required for Firebase
    public UserStory() {}

    // Constructor with parameters
    public UserStory(String image, String statusCaption, long storyAt) {
        this.image = image;
        this.statusCaption = statusCaption;
        this.storyAt = storyAt;
    }

    // Getters and Setters
    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatusCaption() {
        return statusCaption;
    }

    public void setStatusCaption(String statusCaption) {
        this.statusCaption = statusCaption;
    }

    public long getStoryAt() {
        return storyAt;
    }

    public void setStoryAt(long storyAt) {
        this.storyAt = storyAt;
    }

    // Parcelable implementation
    protected UserStory(Parcel in) {
        image = in.readString();
        statusCaption = in.readString();
        storyAt = in.readLong();
    }

    public static final Creator<UserStory> CREATOR = new Creator<UserStory>() {
        @Override
        public UserStory createFromParcel(Parcel in) {
            return new UserStory(in);
        }

        @Override
        public UserStory[] newArray(int size) {
            return new UserStory[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(image);
        dest.writeString(statusCaption);
        dest.writeLong(storyAt);
    }
}
