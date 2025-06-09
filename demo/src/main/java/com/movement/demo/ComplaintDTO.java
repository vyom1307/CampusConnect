package com.movement.demo;

import java.time.LocalDateTime;

public class ComplaintDTO {

    public Long getId() {
        return Id;
    }

    public void setId(Long Id) {
        this.Id = Id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getHostelName() {
        return hostelName;
    }

    public void setHostelName(String hostelName) {
        this.hostelName = hostelName;
    }

    private Long Id;
        private String complaintType;
        private String complaintText;
        private String status;
        private LocalDateTime timestamp;
        private String name;
        private String roll;
        private String room;
        private String hostelName;

    public ComplaintDTO(Long Id, String complaintType, String complaintText,String roll,String room, String status, String userName) {
        this.Id = Id;
        this.complaintType = complaintType;
        this.complaintText = complaintText;
        this.roll=roll;
        this.room=room;
        this.status = status;
        this.name = userName;


    }
}


