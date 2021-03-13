package com.adamsousa.mygreenhouse.model;

import java.util.Date;

public class PhotonCaptureEvent {

    private String deviceId;
    private String event;
    private Date publishedAt;
    private double temperature;
    private double humidity;
    private double moisture;
    private double sunlight;

    public PhotonCaptureEvent(double moisture, double temperature, double humidity, double sunlight, Date publishedAt) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.moisture = moisture;
        this.sunlight = sunlight;
        this.publishedAt = publishedAt;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getMoisture() {
        return moisture;
    }

    public void setMoisture(double moisture) {
        this.moisture = moisture;
    }

    public double getSunlight() {
        return sunlight;
    }

    public void setSunlight(double sunlight) {
        this.sunlight = sunlight;
    }
}
