package com.example.movement.ui;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.movement.SessionManagement;
import com.example.movement.model.UserDTO;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;


import com.example.movement.ComplainAssignAdapter;
import com.example.movement.R;
import com.example.movement.model.Complaint;
import com.example.movement.model.studentData;
import com.example.movement.model.studentRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.Paragraph;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComplainAssign extends Fragment {

    private ComplainAssignViewModel viewModel;
    private RecyclerView ongoingList;
    private RecyclerView pastList;

    private CardView ongoing;
    private CardView past;
    private View view;


    public static ComplainAssign newInstance() {
        return new ComplainAssign();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        view=inflater.inflate(R.layout.fragment_complain_assign, container, false);

        ongoingList=view.findViewById(R.id.ongoing_complaints_list);
        pastList=view.findViewById(R.id.past_complaints_list);
        // Set Layout Manager for RecyclerViews
        ongoingList.setLayoutManager(new LinearLayoutManager(getContext()));
        pastList.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel=new ViewModelProvider(this).get(ComplainAssignViewModel.class);
        ongoing=view.findViewById(R.id.ongoingCV);
        past=view.findViewById(R.id.assginCV);
        ongoing.setOnClickListener(v -> toggleOngoingComplaints(v));
        past.setOnClickListener(v -> togglePastComplaints(v));

        Button generateReportButton = view.findViewById(R.id.generateReportButton);


        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        UserDTO myUser= SessionManagement.getInstance().getUser();

        // Repository to get student data
        studentRepository repo = new studentRepository();
        LiveData<studentData> data = repo.getStudent(myUser.getEmail());







        // Observe the LiveData for student info
//        data.observe(getViewLifecycleOwner(), studentData -> {
//            String hostelname = studentData.getHostel();
//            // Once the student data is available
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            // Correctly query the hostel document using whereEqualTo and add success listener
//            db.collection("hostel")
//                    .get()
//                    .addOnSuccessListener(queryDocumentSnapshots -> {
//                        if (!queryDocumentSnapshots.isEmpty()) {
//                            List<Complaint> ongoingComplaints = new ArrayList<>();
//                            List<Complaint> pastComplaints = new ArrayList<>();
//                            List<Complaint> assginComplaints = new ArrayList<>();
//                            for (QueryDocumentSnapshot queryDocument : queryDocumentSnapshots) {
//                                // Get the first matching hostel document reference
//                                CollectionReference complaintsRef = queryDocument.getReference().collection("complaints");
//
//
//
//                                // Query complaints for the current student
//                                complaintsRef
//                                        .get()
//                                        .addOnSuccessListener(complaintSnapshots -> {
//                                            if (!complaintSnapshots.isEmpty()) {
//
//
//
//
//                                                for (QueryDocumentSnapshot document : complaintSnapshots) {
//                                                    // Extract complaint data
//                                                    Long complaintId = document.getId();
//                                                    String complaintType = document.getString("complaintType");
//                                                    String complaintText = document.getString("complaintText");
//                                                    String roll = document.getString("Roll");
//                                                    String room = document.getString("room");
//                                                    String status = document.getString("status");
//                                                    String name = user.getDisplayName();
//
//                                                    // Determine if it's ongoing or past based on your criteria
//                                                    assert status != null;
//                                                    boolean isOngoing = status.equals("Ongoing"); // Adjust based on your logic
//                                                    boolean isAssign=status.equals("Assign");
//
//                                                    Complaint complaint = new Complaint(complaintId, complaintType, complaintText, roll, room, status, name);
//                                                    if (isOngoing) {
//                                                        ongoingComplaints.add(complaint);
//                                                    } else if(isAssign) {
//                                                        assginComplaints.add(complaint);
//                                                    }else{
//                                                        pastComplaints.add(complaint);
//                                                    }
//                                                }
//                                                generateReportButton.setOnClickListener(v -> {
//                                                    // Pass the list of completed complaints
//
//                                                    generatePDF(ongoingComplaints);
//
//
//                                                });
//
//
//                                                // Pass the complaints lists to display them
//                                                displayComplaints(ongoingComplaints, pastComplaints);
//                                            } else {
//                                                Toast.makeText(this.getContext(), "No complaints found for you", Toast.LENGTH_SHORT).show();
//                                            }
//                                        })
//                                        .addOnFailureListener(e -> {
//                                            // Handle errors
//                                            Toast.makeText(this.getContext(), "Error fetching complaints", Toast.LENGTH_SHORT).show();
//                                        });
//                            }
//                        } else {
//                            Toast.makeText(this.getContext(), "No hostel found with the name " + hostelname, Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(this.getContext(), "Error fetching hostel", Toast.LENGTH_SHORT).show();
//                    });
//        });
        return view;
    }

    public void generatePDF(List<Complaint> completedComplaints) {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(path, "Complaints_Report.pdf");

        try {
            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Add title
            document.add(new Paragraph("Complaints Report").setFontSize(26).setBold());

            // Loop through the list of completed complaints and add details to PDF
            for (Complaint complaint : completedComplaints) {
                document.add(new Paragraph("Complaint: " + complaint.getComplaintType()));
                document.add(new Paragraph("Room: " + complaint.getRoom()));
                document.add(new Paragraph("Description: " + complaint.getComplaintText()));
                document.add(new Paragraph("Status: " + complaint.getStatus()));
                document.add(new Paragraph("\n"));
            }

            document.close();
            Toast.makeText(getContext(), "PDF saved in Downloads successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error generating PDF", Toast.LENGTH_SHORT).show();
        }
    }


    private void displayComplaints(List<Complaint> ongoingComplaints, List<Complaint> pastComplaints) {



// Ongoing Complaints Adapter



        ComplainAssignAdapter adapter = new ComplainAssignAdapter(this.getContext(), ongoingComplaints,getViewLifecycleOwner());
        ongoingList.setAdapter(adapter);

// Past Complaints Adapter

        ComplainAssignAdapter adapter1 = new ComplainAssignAdapter(this.getContext(), pastComplaints,getViewLifecycleOwner());
        pastList.setAdapter(adapter1);


    }



    public void toggleOngoingComplaints(View view) {

        if (ongoingList.getVisibility() == View.GONE) {
            ongoingList.setVisibility(View.VISIBLE);
        } else {
            ongoingList.setVisibility(View.GONE);
        }
    }


    public void togglePastComplaints(View view) {
        if (pastList.getVisibility() == View.GONE) {
            pastList.setVisibility(View.VISIBLE);
        } else {
            pastList.setVisibility(View.GONE);
        }
    }




}