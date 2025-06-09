package com.example.movement;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;

import com.example.movement.model.Complaint;
import com.example.movement.model.RetrofitInstance;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.example.movement.model.studentRepository;
import com.example.movement.model.studentData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Complaint_Filing extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_complaint_filing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Get the complaint type from the intent
        String type = getIntent().getStringExtra("type");
        TextView type_issue = findViewById(R.id.type);
        if (type != null) {
            type_issue.setText(type);
        }

        // Firebase user and auth
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        TextView name = findViewById(R.id.name);
        assert user != null;
        name.setText(user.getDisplayName());
        String email = user.getEmail();

        TextView roll = findViewById(R.id.rollno);
        TextView room = findViewById(R.id.roomno);
        EditText issue = findViewById(R.id.issue);
        Button submit = findViewById(R.id.submit);

        // Repository to get student data
        studentRepository repo = new studentRepository();
        LiveData<studentData> data = repo.getStudent(email);

        // Observe the LiveData for student info
        data.observe(this, studentData -> {
            // Once the student data is available
            roll.setText(studentData.getRollNumber());
            room.setText(studentData.getRoomNumber());
            String hostelname = studentData.getHostel(); // Directly use this value

            // Now display a toast with the hostel name
            Toast.makeText(this, "Hostel: " + hostelname + ", Roll: " + roll.getText().toString(), Toast.LENGTH_SHORT).show();

            // Fetch the hostel document from Firestore
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("hostel").whereEqualTo("Name", hostelname) // Using the fetched hostel name
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentReference hostelDocRef = queryDocumentSnapshots.getDocuments().get(0).getReference();

                            // Now setup the submit button
                            submit.setOnClickListener(view -> {
                                String issue_text = issue.getText().toString().trim();
                                if (!issue_text.isEmpty()) {
                                    Complaint com=new Complaint(null,type,issue_text,roll.getText().toString(),room.getText().toString(),"Ongoing",user.getDisplayName());
                                    fileComplaint(com);




                                    Map<String, String> complaint = new HashMap<>();
                                    complaint.put("Roll", roll.getText().toString());
                                    complaint.put("email", email);
                                    complaint.put("room",room.getText().toString());
                                    complaint.put("complaintType", type);
                                    complaint.put("complaintText", issue_text);
                                    complaint.put("status", "Ongoing");

                                    // Submit the complaint to Firestore
                                    hostelDocRef.collection("complaints")
                                            .add(complaint)
                                            .addOnSuccessListener(documentReference -> {
                                                Toast.makeText(this, "Complaint Submitted", Toast.LENGTH_SHORT).show();
                                                finish();
                                            })
                                            .addOnFailureListener(e -> Toast.makeText(this, "Error submitting complaint", Toast.LENGTH_SHORT).show());
                                } else {
                                    Toast.makeText(this, "Please write a complaint", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(this, "Hostel not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error fetching hostel", Toast.LENGTH_SHORT).show());
        });
    }

    private void fileComplaint(Complaint com) {
        FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();

                        VerifyApi authService = RetrofitInstance.getAuthService();

                        Call<ResponseBody> call = authService.registerComplaint("Bearer " + idToken, com);
                        call.enqueue(new Callback<ResponseBody>() {
                            @Override
                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                if(response.isSuccessful()){
                                    Toast.makeText(Complaint_Filing.this, "Complaint Registered Mysql", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }

                            @Override
                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                Toast.makeText(Complaint_Filing.this, "Complaint FAILED Mysql", Toast.LENGTH_SHORT).show();

                            }
                        });

                    } else {
                        Log.e("FIREBASE", "ID token failed: ", task.getException());
                    }
                });
    }
}
