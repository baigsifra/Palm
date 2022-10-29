package com.example.homelesspersontracker;

public class HelpRequest extends Request {
    private String status;
    private String date;
    private double requestLat;
    private double requestLong;
    private String name;
    private String otherComments;
    private String workerID;

    public HelpRequest(String status, String date, double requestLat, double requestLong) {
        super(status, date, requestLat, requestLong);
    }

    public HelpRequest(String status, String date, double requestLat, double requestLong, String name, String otherComments) {
        super(status, date, requestLat, requestLong);
        this.name = name;
        this.otherComments = otherComments;
    }

    public HelpRequest(String status, String date, String location) {
        super(status, date, location);
    }

    public HelpRequest(String status, String date, String location, String name, String otherComments) {
        super(status, date, location);
        this.name = name;
        this.otherComments = otherComments;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOtherComments() {
        return otherComments;
    }

    public void setOtherComments(String otherComments) {
        this.otherComments = otherComments;
    }

    public String getWorkerID() {
        return workerID;
    }

    public void setWorkerID(String workerID) {
        this.workerID = workerID;
    }
}
