package com.example.movement;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class Profile extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent i= getIntent();
        String url=i.getStringExtra("image");

        TextView name=findViewById(R.id.name);

        assert url != null;
        Log.d("profile PIC",url);
        name.setText(i.getStringExtra("name"));
        ImageView imageView=findViewById(R.id.profilePic);




       Glide.with(imageView.getContext()).load(url).into(imageView);
        Glide.with(imageView.getContext())
                .load(url) // Replace with the URL you get from Firebase
                .placeholder(R.drawable.user) // Show this image while loading or in case of error
                .error(R.drawable.user) // Show this image in case of error
                .into(imageView);

        String email=i.getStringExtra("email");

Log.d("EEEEEMAIAL",email);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("hostel")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot hostelDocument : task.getResult()) {
                            String hostelName = hostelDocument.getId();
                            db.collection("hostel").document(hostelName).collection("studentHostel")
                                    .whereEqualTo("email", email)
                                    .get()
                                    .addOnCompleteListener(subTask -> {
                                        if (subTask.isSuccessful() && !subTask.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot studentDocument : subTask.getResult()) {
                                                String roomNumber = studentDocument.getString("RoomNo");
                                                String rollNumber = studentDocument.getString("RollNo");
                                                TextView roomno=findViewById(R.id.roomno);
                                                TextView rollno=findViewById(R.id.rollno);
                                                Log.d("room NO0",roomNumber);
                                                roomno.setText(roomNumber);
                                                rollno.setText(rollNumber);

                                            }
                                        }
                                    });
                        }
                    }
                });



    }

}