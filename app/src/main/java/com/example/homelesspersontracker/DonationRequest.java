package com.example.homelesspersontracker;

public class DonationRequest extends Request{
    private String status;
    private String date;
    private double requestLat;
    private double requestLong;
    private String preferredMOC;

    public DonationRequest(String status, String date, double requestLat, double requestLong) {
        super(status, date, requestLat, requestLong);
    }

    public DonationRequest(String status, String date, double requestLat, double requestLong, String preferredMOC) {
        super(status, date, requestLat, requestLong);
        this.preferredMOC = preferredMOC;
    }

    public DonationRequest(String status, String date, String location) {
        super(status, date, location);
    }

    public DonationRequest(String status, String date, String location, String preferredMOC) {
        super(status, date, location);
        this.preferredMOC = preferredMOC;
    }

    public String getPreferredMOC() {
        return preferredMOC;
    }

    public void setPreferredMOC(String preferredMOC) {
        this.preferredMOC = preferredMOC;
    }
}
