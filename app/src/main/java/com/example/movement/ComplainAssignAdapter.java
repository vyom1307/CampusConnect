package com.example.movement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movement.model.Complaint;
import com.example.movement.model.studentData;
import com.example.movement.model.studentRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ComplainAssignAdapter extends RecyclerView.Adapter<ComplainAssignAdapter.ViewHolder> {

        private Context context;
        private List<Complaint> complaints;

        private LifecycleOwner lifecycleOwner;

        public ComplainAssignAdapter(Context context, List<Complaint> complaints, LifecycleOwner lifecycleOwner) {
            this.context = context;
            this.complaints = complaints;
            this.lifecycleOwner = lifecycleOwner;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.complain_assign_item, parent, false);
            return new ViewHolder(view);
        }

    @Override
    public void onBindViewHolder(@NonNull ComplainAssignAdapter.ViewHolder holder, int position) {
        Complaint complaint = complaints.get(position);
        holder.complaintText.setText(complaint.getComplaintText());
        holder.roomNumber.setText(complaint.getRoom());
        holder.name.setText(complaint.getName());

        // Set background color based on status
        if (complaint.getStatus().equals("Completed")||complaint.getStatus().equals("Assign")) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.holo_green_dark));
        } else {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(context,R.color.blue));
        }

        // Handle click event for the complaint item
        holder.itemView.setOnClickListener(v -> showStatusChangeDialog(complaint));
    }


        @Override
        public int getItemCount() {
            return complaints.size();
        }

    private void showStatusChangeDialog(Complaint complaint) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Change Status")
                .setMessage("Mark this complaint as 'Completed'?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Change the status in Firestore and update the UI
                    updateComplaintStatus(complaint);
                })
                .setNegativeButton("No", null)
                .show();
    }
    // Method to update complaint status
    private void updateComplaintStatus(Complaint complaint) {
        // Get Firebase Firestore instance
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        studentRepository repo = new studentRepository();
        LiveData<studentData> data = repo.getStudent(user.getEmail());


        data.observe(lifecycleOwner, studentData -> {
            String hostelname = studentData.getHostel();

            if (studentData != null) {
                // Update the complaint's status in Firestore
                db.collection("hostel") // Using the fetched hostel name
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentReference hostelDocRef = queryDocumentSnapshots.getDocuments().get(0).getReference();

                                DocumentReference complaintRef = hostelDocRef
                                        .collection("complaints")
                                        .document(complaint.getComplaintId());

                                complaintRef.update("status", "Assign")
                                        .addOnSuccessListener(aVoid -> {
                                            // Update the status in the local list and notify the adapter
                                            complaint.setStatus("Assign");
                                            notifyDataSetChanged();  // Refresh the RecyclerView
                                            Toast.makeText(context, "Complaint status updated", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Failed to update complaint status", Toast.LENGTH_SHORT).show();
                                        });
                            }
                        });

            }
        });


    }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView complaintText;
            TextView roomNumber;
            Button completeButton;
            TextView name;

            public ViewHolder(View itemView) {
                super(itemView);
                complaintText = itemView.findViewById(R.id.complaintText);
                roomNumber = itemView.findViewById(R.id.roomNo);
                name=itemView.findViewById(R.id.Name);

            }
        }

    }


