package com.example.android.remedicappml;
import androidx.annotation.NonNull;

import com.example.android.remedicappml.api.RetrofitClient;
import com.example.android.remedicappml.api.UploadSignals;
import com.google.mlkit.vision.face.Face;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GetVitals {

    private RetrofitClient retrofit;
    private DataModel datamodel;
    private UploadSignals retrofitAPI;

    public void GetVitals() {
        retrofit = new RetrofitClient();
        datamodel = new DataModel();
        retrofitAPI = RetrofitClient.getClient().create(UploadSignals.class);
    }

    private void postData(String name, String job) {

        Call<DataModel> call = retrofitAPI.createPost(datamodel);
        // on below line we are executing our method.
        call.enqueue(new Callback<DataModel>() {
            @Override
            public void onResponse(Call<DataModel> call, Response<DataModel> response) {

                // we are getting response from our body
                // and passing it to our modal class.
                DataModel responseFromAPI = response.body();
            }

            @Override
            public void onFailure(Call<DataModel> call, Throwable t) {
            }
        });
    }

    protected void onSuccess(@NonNull List<Face> faces, @NonNull GraphicOverlay graphicOverlay) {
        for (Face face : faces) {
            graphicOverlay.add(
                    new VitalsInfoGraphicsNw(
                            graphicOverlay,
                            datamodel.get_beats_per_minute(),
                            datamodel.get_oxygen_saturation(),
                            null));
        }
    }

    public void stop() {

    }
}
