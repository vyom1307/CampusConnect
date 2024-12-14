package com.example.movement.ui.slideshow;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.movement.Homes;

import com.example.movement.R;
import com.example.movement.databinding.FragmentGalleryBinding;
import com.example.movement.databinding.FragmentSlideshowBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;


public class SlideshowFragment extends Fragment {

    private FirebaseFirestore db;
    private List<String> hostelList = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private Spinner spinner;
    private FragmentSlideshowBinding binding;
    private View view;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        view = binding.getRoot();
        Button btn=binding.selectCsvButton;

        // Initialize Firestore
        db = FirebaseFirestore.getInstance();
        spinner = view.findViewById(R.id.spinner);

        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, hostelList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Fetch hostel names
        fetchHostelNames();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedHostel = parent.getItemAtPosition(position).toString();
                if (selectedHostel.equals("Add New Hostel")) {
                    addHostel();
                } else {
                    addStuDetails(selectedHostel);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(requireContext(), "Manifest.permission.READ_EXTERNAL_STORAGE")
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            new String[]{"Manifest.permission.READ_EXTERNAL_STORAGE"}, 1);
                }
                pickCSVFile();
            }
        });


        // Initialize the ActivityResultLauncher
        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedFileUri = result.getData().getData();
                        if (selectedFileUri != null) {
                            readCSVFile(selectedFileUri); // Handle the selected file
                        }
                    }
                }
        );



        return view;
    }
    // Method to launch file picker
    private void pickCSVFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        filePickerLauncher.launch(intent);
    }

    // Method to read the selected CSV file
    private void readCSVFile(Uri fileUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(fileUri);



            CSVReader reader = new CSVReader(new InputStreamReader(inputStream));

            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // Ignore empty rows
                if (nextLine.length == 0 || nextLine[0].isEmpty()) {
                    continue;
                }

                // Ensure the row has at least 4 columns and non-empty values for required fields
                if (nextLine.length >= 4 && !nextLine[0].isEmpty() && !nextLine[1].isEmpty() && !nextLine[2].isEmpty() && !nextLine[3].isEmpty()) {
                    String name = nextLine[0];
                    String email = nextLine[1];
                    String rollNo = nextLine[2];
                    String roomNo = nextLine[3];
                    String hostelName=nextLine[4];

                    // Prepare the student data to be uploaded
                    Map<String, Object> studentData = new HashMap<>();
                    studentData.put("Name", name);
                    studentData.put("email", email);
                    studentData.put("RollNo", rollNo);
                    studentData.put("RoomNo", roomNo);






                    uploadToFirebase(studentData, hostelName); // Upload to Firebase
                } else {
                    // Handle the case where there are missing columns or empty data
                    Log.e("CSVError", "Invalid row or missing data: " + Arrays.toString(nextLine));
                }
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this.getContext(), "Error reading CSV file", Toast.LENGTH_SHORT).show();
        }
    }
    private void uploadToFirebase(Map<String, Object> studentData, String hostelName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("hostel").whereEqualTo("Name", hostelName) // Using the fetched hostel name
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentReference hostelDocRef = queryDocumentSnapshots.getDocuments().get(0).getReference();
                        hostelDocRef.collection("studentHostel").add(studentData).addOnSuccessListener(aVoid -> {
                                    Toast.makeText(requireContext(), "Student data uploaded", Toast.LENGTH_SHORT).show();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Error uploading student data", Toast.LENGTH_SHORT).show();
                                });
                    }
                });



    }




    private void fetchHostelNames() {
        db.collection("hostel").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String hostelName = document.getString("Name");
                            hostelList.add(hostelName);
                        }
                        hostelList.add("Add New Hostel");
                        adapter.notifyDataSetChanged();
                    } else {
                        // Handle error
                        hostelList.add("Error retrieving hostel names");
                    }
                });
    }

    private void addStuDetails(String hostelName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Initialize views


        TextView cap_sing = view.findViewById(R.id.sing_cap);
        TextView cap_doub = view.findViewById(R.id.doubl_Cap);
        EditText stuName =view. findViewById(R.id.stu_name);
        EditText rollno =view. findViewById(R.id.rollno);
        EditText room = view.findViewById(R.id.roomno);
        Button save = view.findViewById(R.id.save);


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
                                Toast.makeText(this.getContext(), "Student With ID Already Exists", Toast.LENGTH_SHORT).show();
                            }
                        });
                        stuName.setText("");
                        rollno.setText("");
                        room.setText("");

                    } else {
                        // Handle empty fields
                        Toast.makeText(this.getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void addHostel() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Add New Hostel");

        // Set up the input fields
        LinearLayout layout = new LinearLayout(this.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputName = new EditText(this.getContext());
        inputName.setHint("Hostel Name");
        layout.addView(inputName);

        final EditText SingleCapacity = new EditText(this.getContext());
        SingleCapacity.setHint("Single Room Capacity");
        SingleCapacity.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(SingleCapacity);
        final EditText DoubleCapacity = new EditText(this.getContext());
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
                            Toast.makeText(this.getContext(), "Failed to add hostel.Please Try Again Later", Toast.LENGTH_SHORT).show();
                        });
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();    }
}
