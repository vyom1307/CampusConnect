package com.example.movement;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class spash_launcher extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private  FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_spash_launcher);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView view=findViewById(R.id.logo);
        Animation pulse= AnimationUtils.loadAnimation(this,R.anim.pulse);
        view.startAnimation(pulse);
        mAuth = FirebaseAuth.getInstance();

        // Set a delay for splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check if user is signed in (non-null) and update UI accordingly
                FirebaseUser currentUser = mAuth.getCurrentUser();

                if (currentUser != null) {
                    // User is signed in, navigate to the home screen
                    checkUserInFirestore(currentUser);
                } else {
                    // User is not signed in, navigate to the login screen
                    Intent loginIntent = new Intent(spash_launcher.this, MainActivity.class);
                    startActivity(loginIntent);
                    finish();  // Close the splash screen
                }
            }
        }, 3000);  // Delay of 3 seconds (adjust as necessary)
    }
    private void checkUserInFirestore(FirebaseUser user) {
        db= FirebaseFirestore.getInstance();
        db.collection("User").document(user.getUid()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String role=document.getString("role");
                    updateUI(user,role);
                }
                else{
                    Log.d(TAG, "User does not exist in Firestore.");
                    Intent loginIntent = new Intent(spash_launcher.this, MainActivity.class);
                    startActivity(loginIntent);
                    finish();
                }
            }
            else{
                Log.e(TAG, "Error  ", task.getException());
            }

        });

    }



    void updateUI(FirebaseUser user,String role){

        if(user!=null){

            Intent intent = new Intent(spash_launcher.this, SplashScreen.class);
            intent.putExtra("role",role);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this, "Sign-in failed.", Toast.LENGTH_SHORT).show();
        }
    }

}