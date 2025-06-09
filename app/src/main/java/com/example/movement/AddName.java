package com.example.movement;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.movement.model.UserDTO;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddName#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddName extends Fragment {

    private Button save;
    private EditText name;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private SessionManagement sessionManagement;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_add_name, container, false);
        save=view.findViewById(R.id.add);
        name=view.findViewById(R.id.name);
        auth=FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveName();
            }
        });



        return view;
    }

    private void saveName() {
        String Name=name.getText().toString();

        FirebaseUser currUser=auth.getCurrentUser();

        UserDTO user= sessionManagement.getUser();
        if(currUser!=null&&!Name.isEmpty()){
            HashMap<String,String>map=new HashMap<>();
            map.put("Name",Name);
            map.put("Email", currUser.getEmail());
            map.put("role", "normal");
            firestore.collection("User").document(currUser.getUid()).set(map)
                    .addOnSuccessListener(aVoid -> {
                Toast.makeText(getActivity(), "User details saved.", Toast.LENGTH_SHORT).show();
                // Navigate to the next screen or update the UI
                        if (getActivity() instanceof MainActivity) {
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.updateUI(user,map.get("role"));
                        }
            }).addOnFailureListener(e -> {
                Toast.makeText(getActivity(), "Failed to save user details.", Toast.LENGTH_SHORT).show();
                            });
            name.setText("");
        }
    }

}