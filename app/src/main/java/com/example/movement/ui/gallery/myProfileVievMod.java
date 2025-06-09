package com.example.movement.ui.gallery;


import android.app.Application;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movement.SessionManagement;
import com.example.movement.model.studentRepository;
import com.example.movement.model.studentData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

import java.util.Objects;

public class myProfileVievMod extends AndroidViewModel {
     studentRepository repo=new studentRepository();
    private final LiveData<studentData> studentLiveData;

    private final FirebaseUser user;
    //did not change the firebase to mysql because i end profile pic and to save in mysql the profile ic i need firebae clod so why
    private SessionManagement sessionManagement;
    private final MutableLiveData<String> userName;
    private final MutableLiveData<String> userEmail;
    private final MutableLiveData<Uri> userProfilePic;


    public myProfileVievMod(@NonNull Application application) {
        super(application);

        user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        studentLiveData= repo.getStudent(user.getEmail());

        userName = new MutableLiveData<>();
        userEmail = new MutableLiveData<>();
        userProfilePic = new MutableLiveData<>();


        // Set the user information if available
        userName.setValue(user.getDisplayName());
        userEmail.setValue(user.getEmail());
        userProfilePic.setValue(user.getPhotoUrl());

    }

    public LiveData<String> getUserName() {
        return userName;
    }

    public LiveData<String> getUserEmail() {
        return userEmail;
    }

    public LiveData<studentData>getStudentLiveData(){
        return studentLiveData;
    }
    public LiveData<Uri> getUserProfilePic() {
        return userProfilePic;
    }
}
