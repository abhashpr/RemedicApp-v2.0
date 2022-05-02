package com.example.android.remedicappml.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// You have to use 10.0.2.2 IP address to connect with localhost system.
public class RetrofitClient {
    private static Retrofit retrofitClient = null;

    public static Retrofit getClient() {
        if (retrofitClient == null) {
            retrofitClient = new Retrofit.Builder()
                    //.baseUrl("http://ec2-34-238-191-142.compute-1.amazonaws.com:8080")
                    //.baseUrl("http://192.168.1.7:5000/")
                    .baseUrl("http://192.168.0.63:5000/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitClient;
    }
}