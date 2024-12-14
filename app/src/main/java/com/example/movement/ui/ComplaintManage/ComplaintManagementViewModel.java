package com.example.movement.ui.ComplaintManage;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.movement.model.Complaint;

import java.util.List;

public class ComplaintManagementViewModel extends ViewModel {
    // TODO: Implement the ViewModel
    private MutableLiveData<List<Complaint>> ongoingComplaintsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Complaint>> pastComplaintsLiveData = new MutableLiveData<>();

    public LiveData<List<Complaint>> getOngoingComplaintsLiveData() {
        return ongoingComplaintsLiveData;
    }

    public LiveData<List<Complaint>> getPastComplaintsLiveData() {
        return pastComplaintsLiveData;
    }

}