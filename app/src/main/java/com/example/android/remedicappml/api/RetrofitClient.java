package com.example.android.remedicappml.api;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// You have to use 10.0.2.2 IP address to connect with localhost system.
public class RetrofitClient {
    public static Retrofit retrofitClient = null;
    public static Retrofit getClient() {
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
//            @NonNull
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Request original = chain.request();
//                Request request = original.newBuilder()
//                        .header("User-Agent", "Your-App-Name")
//                        .header("Accept", "application/vnd.yourapi.v1.full+json")
//                        .addHeader("X-Amz-Date", "20220724T041057Z")
//                        .addHeader("Authorization", "AWS4-HMAC-SHA256 Credential=AKIAQFBWDE5BBCXDXEP5/20220724/ap-south-1/execute-api/aws4_request, SignedHeaders=host;x-amz-date, Signature=1a02da6d689ab15feb9e438d96cb4cbba0c5d1bf7cfdb194f01ac710af35fda5")
//                        .method(original.method(), original.body())
//                        .build();
//
//                return chain.proceed(request);
//            }
//        });
//
//        OkHttpClient client = httpClient.build();
        if (retrofitClient == null) {
            retrofitClient = new Retrofit.Builder()
                    // .baseUrl("http://ec2-34-238-191-142.compute-1.amazonaws.com:8080")
                    // .baseUrl("http://192.168.1.7:5000/")
                    // .baseUrl("http://192.168.0.63:5000/")
                    .baseUrl("http://192.168.1.7:5000/")
                    //.baseUrl("https://s621s8o9x2.execute-api.ap-south-1.amazonaws.com/default/myfunc/")
                    .addConverterFactory(GsonConverterFactory.create())
                    //.client(client)
                    .build();
        }
        return retrofitClient;
    }
}