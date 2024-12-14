package com.example.movement;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class add_hostel_details extends AppCompatActivity {
    private FirebaseFirestore db;
    private List<String> hostelList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db=FirebaseFirestore.getInstance();
         spinner=findViewById(R.id.spinner);


         adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, hostelList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        db.collection("hostel")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String hostelName = document.getString("Name");
                            hostelList.add(hostelName); // Add hostel names to the list
                        }
                        // Add option to add a new hostel
                        hostelList.add("Add New Hostel");
                        adapter.notifyDataSetChanged(); // Notify the adapter to refresh the Spinner
                    } else {
                        // Handle the error
                        hostelList.add("Error retrieving hostel names");
                    }
                });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedHostel = parent.getItemAtPosition(position).toString();
                if(selectedHostel.equals("Add New Hostel")){
                    addHostel();
                }

                addStuDetails(spinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void addStuDetails(String hostelName) {
        // Initialize Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initialize views
        TextView cap_sing = findViewById(R.id.sing_cap);
        TextView cap_doub = findViewById(R.id.doubl_Cap);
        EditText stuName = findViewById(R.id.stu_name);
        EditText rollno = findViewById(R.id.rollno);
        EditText room = findViewById(R.id.roomno);
        Button save = findViewById(R.id.save);

        // Query Firestore for hostel details
        db.collection("hostel").whereEqualTo("Name", hostelName).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    // Access the fields of the document
                    Long doubleCap = document.getLong("DoubleCap");
                    Long singleCap = document.getLong("SingleCap");

                    // Check if values are not null before setting text
                    cap_doub.setText(doubleCap != null ? String.valueOf(doubleCap) : "N/A");
                    cap_sing.setText(singleCap != null ? String.valueOf(singleCap) : "N/A");
                }
            } else {
                Log.d("Firestore", "Error getting documents: ", task.getException());
            }
        });

        // Set OnClickListener for the sasve button
        save.setOnClickListener(v -> {
            // Retrieve data from EditText views
            String studentName = stuName.getText().toString().trim();
            String rollNo = rollno.getText().toString().trim();
            String roomNo = room.getText().toString().trim();



            if (!studentName.isEmpty() && !rollNo.isEmpty() && !roomNo.isEmpty()) {



                // Create a new student map
                Map<String, Object> student = new HashMap<>();
                student.put("Name", studentName);
                student.put("RollNo", rollNo);
                student.put("RoomNo", roomNo);


                db.collection("hostel").whereEqualTo("Name", hostelName).get().addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        String documentId = querySnapshot.getDocuments().get(0).getId();
                        // Use documentId as needed
                        CollectionReference studentDetailsRef = db.collection("hostel")
                                .document(documentId)
                                .collection("studentHostel");

                        studentDetailsRef.whereEqualTo("RollNo", rollNo).get().addOnSuccessListener(studentQuerySnapshot -> {
                            if (studentQuerySnapshot.isEmpty()) {

                                // Add student to Firestore


                                // Add a new student document with a random ID
                                studentDetailsRef.add(student)
                                        .addOnSuccessListener(documentReference -> {
                                            // Document added successfully
                                            Log.d("Firestore", "Student added with ID: " + documentReference.getId());
                                        })
                                        .addOnFailureListener(e -> {
                                            // Error occurred while adding document
                                            Log.w("Firestore", "Error adding student", e);
                                        });
                            }else{
                                Toast.makeText(this, "Student With ID Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        });
                        stuName.setText("");
                        rollno.setText("");
                        room.setText("");

                    } else {
                // Handle empty fields
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
        });
            }
        });
    }


    private void addHostel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Hostel");

        // Set up the input fields
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputName = new EditText(this);
        inputName.setHint("Hostel Name");
        layout.addView(inputName);

        final EditText SingleCapacity = new EditText(this);
        SingleCapacity.setHint("Single Room Capacity");
        SingleCapacity.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(SingleCapacity);
        final EditText DoubleCapacity = new EditText(this);
        DoubleCapacity.setHint("Double Room Capcaity");
        DoubleCapacity.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(DoubleCapacity);

        builder.setView(layout);

        // Set up the buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            String hostelName = inputName.getText().toString().trim();
            String capacityStr = DoubleCapacity.getText().toString().trim();
            String SingleCapStr = SingleCapacity.getText().toString().trim();

            if (!hostelName.isEmpty() && !capacityStr.isEmpty()) {
                int capacity = Integer.parseInt(capacityStr);
                int SingleCap = Integer.parseInt(SingleCapStr);

                // Add to Firestore
                Map<String, Object> hostel = new HashMap<>();
                hostel.put("Name", hostelName);
                hostel.put("DoubleCap", capacity);
                hostel.put("SingleCap", SingleCap);

                db.collection("hostel").add(hostel)
                        .addOnSuccessListener(documentReference -> {
                            hostelList.add(hostelName);
                            adapter.notifyDataSetChanged();
                            spinner.setSelection(hostelList.size() - 1);
                        })
                        .addOnFailureListener(e -> {
                            // Handle the error
                            Toast.makeText(this, "Failed to add hostel.Please Try Again Later", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();

    }
}