package com.example.movement.ui.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.example.movement.Home;
import com.example.movement.MainActivity;
import com.example.movement.model.studentData;

import com.bumptech.glide.Glide;
import com.example.movement.R;
import com.example.movement.databinding.FragmentGalleryBinding;
import com.google.firebase.auth.FirebaseAuth;

public class myProfileFrag extends Fragment {

    private FragmentGalleryBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myProfileVievMod myProfileVievMod =
                new ViewModelProvider(this).get(myProfileVievMod.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView textView = binding.name;
        myProfileVievMod.getUserName().observe(getViewLifecycleOwner(), textView::setText);



        ImageView imageView = binding.profilePic;
        myProfileVievMod.getUserProfilePic().observe(getViewLifecycleOwner(), profilePicUri -> {
            if (profilePicUri != null) {
                Glide.with(this)
                        .load(profilePicUri)
                        .placeholder(R.drawable.user) // default placeholder
                        .into(imageView);
            }
        });

        TextView room=binding.roomno;
        TextView roll=binding.rollno;
        TextView hostel_maem=binding.hostelName;
        LiveData <studentData> data=myProfileVievMod.getStudentLiveData();
        data.observe(getViewLifecycleOwner(), studentData -> {
            if (studentData != null) {
                room.setText(studentData.getRoomNumber());
                roll.setText(studentData.getRollNumber());
                hostel_maem.setText(studentData.getHostel());
            }
        });



        Button signout  =binding.signOut;
        signout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            if(FirebaseAuth.getInstance().getCurrentUser()==null){
                Intent i=new Intent(this.getContext(), MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);

            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}