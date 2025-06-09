package com.example.movement;

import com.example.movement.model.Complaint;
import com.example.movement.model.UserDTO;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface VerifyApi {
    @POST("api/auth/verify")
    Call<UserDTO> verifyToken(@Header("Authorization") String idToken);

    @GET("/complaints/user")
    Call<List<Complaint>> getComplaints(@Header("Authorization") String authToken);

    @POST("complaints/status")
    Call<ResponseBody> updateComplaintStatus(
            @Header("Authorization") String token,
            @Query("id") Long complaintId
    );

    @POST("complaints/register")
    Call<ResponseBody> registerComplaint(
            @Header("Authorization") String token,
            @Body Complaint complaint
    );

}

