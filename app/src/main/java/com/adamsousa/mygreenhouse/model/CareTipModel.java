package com.adamsousa.mygreenhouse.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CareTipModel implements Parcelable {

    private String title;
    private String description;
    private String pictureFilePath;

    public CareTipModel(String title, String description, String pictureFilePath) {
        this.title = title;
        this.description = description;
        this.pictureFilePath = pictureFilePath;
    }

    protected CareTipModel(Parcel in) {
        title = in.readString();
        description = in.readString();
        pictureFilePath = in.readString();
    }

    public static final Creator<CareTipModel> CREATOR = new Creator<CareTipModel>() {
        @Override
        public CareTipModel createFromParcel(Parcel in) {
            return new CareTipModel(in);
        }

        @Override
        public CareTipModel[] newArray(int size) {
            return new CareTipModel[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPictureFilePath() {
        return pictureFilePath;
    }

    public void setPictureFilePath(String pictureFilePath) {
        this.pictureFilePath = pictureFilePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(pictureFilePath);
    }
}
