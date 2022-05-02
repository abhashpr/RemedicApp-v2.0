package com.example.android.remedicappml.api;
import com.example.android.remedicappml.DataModel;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadSignals {
//    @Multipart
//    @POST("/api/uploadSignal")
//    Call<model> uploadSignal(@Part MultipartBody.Part signal);

    // as we are making a post request to post a data
    // so we are annotating it with post
    // and along with that we are passing a parameter as users
    @POST("vitals")

    //on below line we are creating a method to post our data.
    Call<DataModel> createPost(@Body DataModel dataModel);
}