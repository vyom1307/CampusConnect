package com.example.movement;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.credentials.CreatePublicKeyCredentialResponse;
import androidx.credentials.Credential;
import androidx.credentials.CredentialManager;
import androidx.credentials.GetCredentialRequest;
import androidx.fragment.app.FragmentManager;

import com.example.movement.R;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.identity.googleid.GetGoogleIdOption;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private SignInClient oneTapClient;
    private BeginSignInRequest signInRequest;
    private FirebaseFirestore db;
    private FrameLayout fragmentContainer;
    private ConstraintLayout loginLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null){
            checkUserInFirestore(mAuth.getCurrentUser());
        }


        Animation rotateAnimation = AnimationUtils.loadAnimation(this, R.drawable.border_anim);
        ImageView logo=findViewById(R.id.profilePic);
        //logo.startAnimation(rotateAnimation);



        ImageView signIn = findViewById(R.id.gSign);
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
        signIn.startAnimation(pulseAnimation);

        Button started=findViewById(R.id.getStart);
        started.startAnimation(pulseAnimation);

        Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        db= FirebaseFirestore.getInstance();

        // Initialize fragment container
        fragmentContainer = findViewById(R.id.fragment_container);
        loginLayout = findViewById(R.id.constraint);

        // Find the sign-in ImageView by ID


        // Initialize One Tap Client
        oneTapClient = Identity.getSignInClient(this);

        // Configure sign-in request
        signInRequest = new BeginSignInRequest.Builder()
                .setGoogleIdTokenRequestOptions(
                        BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                                .setSupported(true)
                                .setFilterByAuthorizedAccounts(false)
                                .setServerClientId("764849961356-68ogp149g75n7vhhrt1uk5n1bahgbuvn.apps.googleusercontent.com") // Replace with your actual server client ID
                                .build())
                .build();

        // Set up click listener for sign-in button
        signIn.setOnClickListener(v -> {
            // Start sign-in process
            if (vibe != null) {
                vibe.vibrate(50);
            }
            startSignIn();
        });
    }

    // Method to start sign-in process
    private void startSignIn() {
        oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this, result -> {
                    // Launch the sign-in UI
                    signInLauncher.launch(new IntentSenderRequest.Builder(result.getPendingIntent().getIntentSender()).build());
                })
                .addOnFailureListener(this, e -> {
                    // Handle failure
                    Log.e(TAG, "Error starting sign-in: ", e);
                });
    }

    // ActivityResultLauncher to handle sign-in result
    private final ActivityResultLauncher<IntentSenderRequest> signInLauncher = registerForActivityResult(
            new ActivityResultContracts.StartIntentSenderForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    try {
                        // Retrieve credential
                        SignInCredential credential = oneTapClient.getSignInCredentialFromIntent(result.getData());
                        String idToken = credential.getGoogleIdToken();
                        String email = credential.getId();
                        if (email != null) {
                            String domain = email.substring(email.indexOf("@") + 1);

                            if (domain.equals("lnmiit.ac.in")||email.equals("vrajshah006@gmail.com")) {

                                AuthCredential firebaseCredential = GoogleAuthProvider.getCredential(idToken, null);

                                // Sign in with Firebase Auth
                                mAuth.signInWithCredential(firebaseCredential)
                                        .addOnCompleteListener(this, task -> {
                                            if (task.isSuccessful()) {
                                                // Sign-in success, update UI
                                                FirebaseUser user = mAuth.getCurrentUser();

                                                // Optionally, update the UI
                                                checkUserInFirestore(user);

                                            } else {
                                                // Sign-in failed
                                                Log.w(TAG, "Firebase sign-in failed", task.getException());
                                                Toast.makeText(this, "Firebase sign-in failed.", Toast.LENGTH_SHORT).show();
                                                // Optionally, handle errors
                                                updateUI(null, null);
                                            }
                                        });
                            }else{
                                Toast.makeText(this, "Invalid domain.Sigin with lnmiit.ac.in", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }catch(ApiException e){
                                Log.e(TAG, "Sign-in failed: ", e);
                            }


                    }

            }
    );

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
                    promptForName(user);
                }
            }
            else{
                Log.e(TAG, "Error  ", task.getException());
            }

        });

    }

    private void promptForName(FirebaseUser user) {
        fragmentContainer.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.GONE);



        AddName userDetailsFragment = new AddName();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, userDetailsFragment)
                .addToBackStack(null)
                .show(userDetailsFragment).commit();
        

    }

    void updateUI(FirebaseUser user,String role){

        if(user!=null){

            Intent intent = new Intent(MainActivity.this, SplashScreen.class);
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
