package com.adamsousa.mygreenhouse.model;

import android.os.Parcel;
import android.os.Parcelable;

public class LocationModel implements Parcelable {

    private String id;
    private String name;
    private double plant_count;
    private String picture_filepath;

    public LocationModel(String id, String room, double plantCount, String filePath) {
        this.id = id;
        this.name = room;
        this.plant_count = plantCount;
        this.picture_filepath = filePath;
    }

    private LocationModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        plant_count = in.readDouble();
        picture_filepath = in.readString();
    }

    public String getId() {
        return id;
    }

    public static final Creator<LocationModel> CREATOR = new Creator<LocationModel>() {
        @Override
        public LocationModel createFromParcel(Parcel in) {
            return new LocationModel(in);
        }

        @Override
        public LocationModel[] newArray(int size) {
            return new LocationModel[size];
        }
    };

    public double getPlantCount() {
        return plant_count;
    }

    public void setPlantCount(double plantCount) {
        this.plant_count = plantCount;
    }

    public String getRoom() {
        return name;
    }

    public void setRoom(String room) {
        this.name = room;
    }

    public String getPictureFilePath() {
        return picture_filepath;
    }

    public void setPictureFilePath(String filePath) {
        this.picture_filepath = filePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeDouble(plant_count);
        parcel.writeString(picture_filepath);
    }
}
