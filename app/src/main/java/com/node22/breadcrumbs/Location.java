package com.node22.breadcrumbs;


import android.text.format.Time;

/**
 * Created by zharley on 15-05-29.
 */
public class Location {
    protected double latitude;
    protected double longitude;
    protected Time timestamp;

    public Location(double latitude, double longitude, Time timestamp) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Time getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Time timestamp) {
        this.timestamp = timestamp;
    }
}
