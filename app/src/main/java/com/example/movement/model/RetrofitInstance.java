package com.example.movement.model;

import com.example.movement.VerifyApi;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    private static final String BASE_URL = "http://192.168.0.102:8080/"; // <-- replace with actual IP

    private static Retrofit retrofit;

    public static VerifyApi getAuthService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)  // MUST end with slash'

                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(VerifyApi.class);
    }
}
