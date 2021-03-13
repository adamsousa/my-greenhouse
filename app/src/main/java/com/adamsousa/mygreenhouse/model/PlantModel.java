package com.adamsousa.mygreenhouse.model;

import android.os.Parcel;
import android.os.Parcelable;

public class PlantModel implements Parcelable {

    private String id;
    private String name;
    private String deviceId;
    private String pictureFilePath;
    private Integer moistureMax;
    private Integer moistureMin;
    private Integer temperatureMax;
    private Integer temperatureMin;
    private Integer humidityMax;
    private Integer humidityMin;
    private Integer sunlightMax;
    private Integer sunlightMin;
    private PhotonCaptureEvent latestCapture;
    private boolean captureRetrievalDone = false;

    public PlantModel(String id, String name, String deviceId, String pictureFilePath, Integer moistureMax, Integer moistureMin, Integer temperatureMax, Integer temperatureMin, Integer humidityMax, Integer humidityMin, Integer sunlightMax, Integer sunlightMin) {
        this.id = id;
        this.name = name;
        this.deviceId = deviceId;
        this.pictureFilePath = pictureFilePath;
        this.moistureMax = moistureMax;
        this.moistureMin = moistureMin;
        this.temperatureMax = temperatureMax;
        this.temperatureMin = temperatureMin;
        this.humidityMax = humidityMax;
        this.humidityMin = humidityMin;
        this.sunlightMax = sunlightMax;
        this.sunlightMin = sunlightMin;
    }

    private PlantModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        deviceId = in.readString();
        pictureFilePath = in.readString();
        if (in.readByte() == 0) {
            moistureMax = null;
        } else {
            moistureMax = in.readInt();
        }
        if (in.readByte() == 0) {
            moistureMin = null;
        } else {
            moistureMin = in.readInt();
        }
        if (in.readByte() == 0) {
            temperatureMax = null;
        } else {
            temperatureMax = in.readInt();
        }
        if (in.readByte() == 0) {
            temperatureMin = null;
        } else {
            temperatureMin = in.readInt();
        }
        if (in.readByte() == 0) {
            humidityMax = null;
        } else {
            humidityMax = in.readInt();
        }
        if (in.readByte() == 0) {
            humidityMin = null;
        } else {
            humidityMin = in.readInt();
        }
        if (in.readByte() == 0) {
            sunlightMax = null;
        } else {
            sunlightMax = in.readInt();
        }
        if (in.readByte() == 0) {
            sunlightMin = null;
        } else {
            sunlightMin = in.readInt();
        }
        captureRetrievalDone = in.readByte() != 0;
    }

    public static final Creator<PlantModel> CREATOR = new Creator<PlantModel>() {
        @Override
        public PlantModel createFromParcel(Parcel in) {
            return new PlantModel(in);
        }

        @Override
        public PlantModel[] newArray(int size) {
            return new PlantModel[size];
        }
    };

    public boolean isCaptureRetrievalDone() {
        return captureRetrievalDone;
    }

    public void setCaptureRetrievalDone(boolean captureRetrievalDone) {
        this.captureRetrievalDone = captureRetrievalDone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPictureFilePath() {
        return pictureFilePath;
    }

    public void setPictureFilePath(String pictureFilePath) {
        this.pictureFilePath = pictureFilePath;
    }

    public Integer getMoistureMax() {
        return moistureMax;
    }

    public void setMoistureMax(Integer moistureMax) {
        this.moistureMax = moistureMax;
    }

    public Integer getMoistureMin() {
        return moistureMin;
    }

    public void setMoistureMin(Integer moistureMin) {
        this.moistureMin = moistureMin;
    }

    public Integer getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(Integer temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public Integer getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(Integer temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public Integer getHumidityMax() {
        return humidityMax;
    }

    public void setHumidityMax(Integer humidityMax) {
        this.humidityMax = humidityMax;
    }

    public Integer getHumidityMin() {
        return humidityMin;
    }

    public void setHumidityMin(Integer humidityMin) {
        this.humidityMin = humidityMin;
    }

    public Integer getSunlightMax() {
        return sunlightMax;
    }

    public void setSunlightMax(Integer sunlightMax) {
        this.sunlightMax = sunlightMax;
    }

    public Integer getSunlightMin() {
        return sunlightMin;
    }

    public void setSunlightMin(Integer sunlightMin) {
        this.sunlightMin = sunlightMin;
    }

    public PhotonCaptureEvent getLatestCapture() {
        return latestCapture;
    }

    public void setLatestCapture(PhotonCaptureEvent latestCapture) {
        this.latestCapture = latestCapture;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(deviceId);
        parcel.writeString(pictureFilePath);
        if (moistureMax == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(moistureMax);
        }
        if (moistureMin == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(moistureMin);
        }
        if (temperatureMax == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(temperatureMax);
        }
        if (temperatureMin == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(temperatureMin);
        }
        if (humidityMax == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(humidityMax);
        }
        if (humidityMin == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(humidityMin);
        }
        if (sunlightMax == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(sunlightMax);
        }
        if (sunlightMin == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(sunlightMin);
        }
        parcel.writeByte((byte) (captureRetrievalDone ? 1 : 0));
    }
}
