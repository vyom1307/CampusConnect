package com.example.movement.model;

public class Complaint {
    private String name;
    private String complaintId;
    private String complaintType;
    private String complaintText;
    private String roll;
    private String room;

    private String status;
    public Complaint(String complaintId, String complaintType, String complaintText, String roll, String room,String status,String name) {
        this.complaintId = complaintId;
        this.complaintType = complaintType;
        this.complaintText = complaintText;
        this.roll = roll;
        this.room = room;
        this.status=status;
        this.name=name;
    }

    // Getters
    public String getComplaintId() {
        return complaintId;
    }

    public String getComplaintType() {
        return complaintType;
    }

    public String getComplaintText() {
        return complaintText;
    }

    public String getRoll() {
        return roll;
    }

    public String getRoom() {
        return room;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String str){
        status=str;

    }
    public String getName(){
        return name;
    }
    public void setName(String str){
        name=str;
    }

}
