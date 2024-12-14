package com.example.movement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Home extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        OnBackPressedDispatcher onBackPressedDispatcher = new OnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
        Intent intent=getIntent();
        String role=intent.getStringExtra("role");
        Button signout  =findViewById(R.id.signOut);
        signout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            if(FirebaseAuth.getInstance().getCurrentUser()==null){
                Intent i=new Intent(Home.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        });
        Button addHostel=findViewById(R.id.addHostel);
        if(role.equals("normal")){
            addHostel.setVisibility(View.GONE);
        }
        addHostel.setOnClickListener(v -> {
            Intent i=new Intent(Home.this,add_hostel_details.class);
            Toast.makeText(this, "whhhhhhhhhy", Toast.LENGTH_SHORT).show();
            startActivity(i);
        });
            Button  Profile=findViewById(R.id.profile);
            Profile.setVisibility(View.VISIBLE);
            Profile.setOnClickListener(v -> {
                Intent i=new Intent(Home.this,Profile.class);
                i.putExtra("email", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail());
                i.putExtra("name",Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
                i.putExtra("image", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl()).toString());
                startActivity(i);
            });
            Button nav =findViewById(R.id.frag);
            nav.setOnClickListener(v -> {

                Intent i=new Intent(Home.this,Homes.class);
                i.putExtra("role",role);
                startActivity(i);
            });


    }
}