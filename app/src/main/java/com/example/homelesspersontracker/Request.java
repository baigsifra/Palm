package com.example.homelesspersontracker;

import androidx.annotation.NonNull;

public class Request {
    private String status;
    private String date;
    private double requestLat;
    private double requestLong;
    private String location;
    private String id;

    public Request(String status, String date, double requestLat, double requestLong) {
        this.status = status;
        this.date = date;
        this.requestLat = requestLat;
        this.requestLong = requestLong;
    }

    public Request(String status, String date, String location) {
        this.status = status;
        this.date = date;
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getRequestLat() {
        return requestLat;
    }

    public void setRequestLat(double requestLat) {
        this.requestLat = requestLat;
    }

    public double getRequestLong() {
        return requestLong;
    }

    public void setRequestLong(double requestLong) {
        this.requestLong = requestLong;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @NonNull
    public String toString() {
        return "Status: " + status + "\nDate: " + date + "\nrequestLat: " + requestLat + "\nrequestLong: " + requestLong;
    }
}
