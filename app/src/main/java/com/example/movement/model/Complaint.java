package com.example.movement.model;

public class Complaint {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public String getComplaintId() {
//        return complaintId;
//    }
//
//    public void setComplaintId(String complaintId) {
//        this.complaintId = complaintId;
//    }

    public String getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(String complaintType) {
        this.complaintType = complaintType;
    }

    public String getComplaintText() {
        return complaintText;
    }

    public void setComplaintText(String complaintText) {
        this.complaintText = complaintText;
    }

    public String getRoll() {
        return roll;
    }

    public void setRoll(String roll) {
        this.roll = roll;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long Id) {
        this.id = Id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    private String name;
//    private String complaintId;
    private String complaintType;
    private String complaintText;
    private String roll;
    private String room;
    private Long id;
    private String status;





//    public Complaint(String complaintId, String complaintType, String complaintText, String roll, String room, String status, String name) {
//        this.complaintId = complaintId;
//        this.complaintType = complaintType;
//        this.complaintText = complaintText;
//        this.roll = roll;
//        this.room = room;
//        this.status=status;
//        this.name=name;
//    }
    public Complaint(Long Id, String complaintType, String complaintText, String roll, String room,String status,String name) {
        this.id = Id;
        this.complaintType = complaintType;
        this.complaintText = complaintText;
        this.roll = roll;
        this.room = room;
        this.status=status;
        this.name=name;
    }



}
