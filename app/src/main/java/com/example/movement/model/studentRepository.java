package com.example.movement.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class studentRepository {

        private final FirebaseFirestore db=FirebaseFirestore.getInstance();
        private studentData cachedStudent;






    public LiveData<studentData> getStudent(String email) {
            MutableLiveData<studentData> studentLiveData = new MutableLiveData<>();

            // Return cached data if available
            if (cachedStudent != null) {
                studentLiveData.setValue(cachedStudent);
                return studentLiveData;
            }

            // Fetch from Firestore if no cached data
            db.collection("hostel")
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot hostelDocument : task.getResult()) {
                                String hostelName = hostelDocument.getString("Name"); // Assuming "Name" field holds the hostel's name

                                // Now query the studentHostel sub-collection within each hostel
                                db.collection("hostel").document(hostelDocument.getId())
                                        .collection("studentHostel")
                                        .whereEqualTo("email", email) // Replace `email` with the actual email you are searching for
                                        .get()
                                        .addOnCompleteListener(subTask -> {
                                            if (subTask.isSuccessful() && !subTask.getResult().isEmpty()) {
                                                // If a student document with the given email is found
                                                for (QueryDocumentSnapshot studentDocument : subTask.getResult()) {

                                                    String roomNumber = studentDocument.getString("RoomNo");
                                                    String rollNumber = studentDocument.getString("RollNo");
                                                    String name_hostel=hostelDocument.getString("Name");
                                                    cachedStudent = new studentData(roomNumber, rollNumber,hostelName); // Cache the result
                                                    studentLiveData.setValue(cachedStudent);
                                                }
                                            }
                                        });
                            }
                        }
                    });

            return studentLiveData;
        }
    }


