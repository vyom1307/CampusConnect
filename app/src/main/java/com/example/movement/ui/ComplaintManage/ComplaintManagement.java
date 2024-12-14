package com.example.movement.ui.ComplaintManage;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.movement.ComplainAdapter_recycler;
import com.example.movement.ComplaintAdapter;
import com.example.movement.Complaint_Filing;
import com.example.movement.Custom_adapter_gridview_complaint;
import com.example.movement.R;
import android.content.Intent;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movement.Home;
import com.example.movement.MainActivity;
import com.example.movement.model.Complaint;
import com.example.movement.model.studentData;
import com.bumptech.glide.Glide;
import com.example.movement.R;
import com.example.movement.databinding.FragmentGalleryBinding;
import com.example.movement.model.studentRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ComplaintManagement extends Fragment {


    private RecyclerView ongoingList;
    private RecyclerView pastList;

    private CardView ongoing;
    private CardView past;
    private View view;
    private ComplaintManagementViewModel viewModel;




    public static ComplaintManagement newInstance() {
        return new ComplaintManagement();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
         view=inflater.inflate(R.layout.fragment_complaint_management, container, false);
        GridView roomIssuesGrid = view.findViewById(R.id.room_issues_grid);
        GridView hostelIssuesGrid = view.findViewById(R.id.hostel_issues_grid);
       ongoingList=view.findViewById(R.id.ongoing_complaints_list);
        pastList=view.findViewById(R.id.past_complaints_list);
        // Set Layout Manager for RecyclerViews
        ongoingList.setLayoutManager(new LinearLayoutManager(getContext()));
        pastList.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel=new ViewModelProvider(this).get(ComplaintManagementViewModel.class);
        ongoing=view.findViewById(R.id.ongoingCV);
        past=view.findViewById(R.id.pastCV);
        ongoing.setOnClickListener(v -> toggleOngoingComplaints(v));
        past.setOnClickListener(v -> togglePastComplaints(v));





        List<String> room_issues=new ArrayList<>();
        List<String> hostel_issues=new ArrayList<>();


        room_issues.add("Internet");
        room_issues.add("Appliance");
        room_issues.add("Furniture");
        room_issues.add("Insects");

        hostel_issues.add("Washroom");
        hostel_issues.add("Corridors");


        // Add corresponding icons for each room type (if you have)
        int[] imageIds = {R.drawable.internet, R.drawable.gadget,
                R.drawable.fur, R.drawable.bug};

        int[] imageIds1 = {R.drawable.washroom, R.drawable.hallway};
        // Set adapter for GridView
        Custom_adapter_gridview_complaint adapter = new Custom_adapter_gridview_complaint(getActivity(), room_issues, imageIds);
        roomIssuesGrid.setAdapter(adapter);

        Custom_adapter_gridview_complaint adapter1 = new Custom_adapter_gridview_complaint(getActivity(), hostel_issues, imageIds1);
        hostelIssuesGrid.setAdapter(adapter1);

        // Set on item click listener
        roomIssuesGrid.setOnItemClickListener((parent, view1, position, id) -> {

            Intent intent = new Intent(getActivity(), Complaint_Filing.class);
            intent.putExtra("type", room_issues.get(position));
            startActivity(intent);
        });
        hostelIssuesGrid.setOnItemClickListener((parent, view1, position, id) -> {
            Intent intent = new Intent(getActivity(), Complaint_Filing.class);
            intent.putExtra("type", hostel_issues.get(position));
            startActivity(intent);
        });

        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();

        // Repository to get student data
        studentRepository repo = new studentRepository();
        LiveData<studentData> data = repo.getStudent(user.getEmail());

        // Observe the LiveData for student info
        data.observe(getViewLifecycleOwner(), studentData -> {
            String hostelname = studentData.getHostel();
            // Once the student data is available
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Correctly query the hostel document using whereEqualTo and add success listener
            db.collection("hostel").whereEqualTo("Name", hostelname)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Get the first matching hostel document reference
                            CollectionReference complaintsRef = queryDocumentSnapshots.getDocuments()
                                    .get(0).getReference().collection("complaints");

                            // Query complaints for the current student
                            complaintsRef.whereEqualTo("email", user.getEmail())
                                    .get()
                                    .addOnSuccessListener(complaintSnapshots -> {
                                        if (!complaintSnapshots.isEmpty()) {


                                            List<Complaint> ongoingComplaints = new ArrayList<>();
                                            List<Complaint> pastComplaints = new ArrayList<>();



                                            for (QueryDocumentSnapshot document : complaintSnapshots) {
                                                // Extract complaint data
                                                String complaintId = document.getId();
                                                String complaintType = document.getString("complaintType");
                                                String complaintText = document.getString("complaintText");
                                                String roll = document.getString("Roll");
                                                String room = document.getString("room");
                                                String status = document.getString("status");
                                                String name= user.getDisplayName();

                                                // Determine if it's ongoing or past based on your criteria
                                                assert status != null;
                                                boolean isOngoing = status.equals("Ongoing"); // Adjust based on your logic

                                                Complaint complaint = new Complaint(complaintId, complaintType, complaintText, roll, room,status,name);
                                                if (isOngoing) {
                                                    ongoingComplaints.add(complaint);
                                                } else {
                                                    pastComplaints.add(complaint);
                                                }
                                            }

                                            // Pass the complaints lists to display them
                                            displayComplaints(ongoingComplaints, pastComplaints);
                                        } else {
                                            Toast.makeText(this.getContext(), "No complaints found for you", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        // Handle errors
                                        Toast.makeText(this.getContext(), "Error fetching complaints", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            Toast.makeText(this.getContext(), "No hostel found with the name " + hostelname, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this.getContext(), "Error fetching hostel", Toast.LENGTH_SHORT).show();
                    });
        });








        return view;
    }

    private void displayComplaints(List<Complaint> ongoingComplaints, List<Complaint> pastComplaints) {



// Ongoing Complaints Adapter



        ComplainAdapter_recycler adapter = new ComplainAdapter_recycler(this.getContext(), ongoingComplaints,getViewLifecycleOwner());
        ongoingList.setAdapter(adapter);

// Past Complaints Adapter

        ComplainAdapter_recycler adapter1 = new ComplainAdapter_recycler(this.getContext(), pastComplaints,getViewLifecycleOwner());
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