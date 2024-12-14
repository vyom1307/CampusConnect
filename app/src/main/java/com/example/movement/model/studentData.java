package com.example.movement.model;

public class studentData {

        private String roomNumber;
        private String rollNumber;
        private String hostel;

        public studentData(String roomNumber, String rollNumber,String hostel) {

            this.roomNumber = roomNumber;
            this.rollNumber = rollNumber;
            this.hostel=hostel;
        }

        public String getRoomNumber() {
            return roomNumber;
        }

        public String getRollNumber() {
            return rollNumber;
        }
        public String getHostel(){
            return hostel;
        }
    }

