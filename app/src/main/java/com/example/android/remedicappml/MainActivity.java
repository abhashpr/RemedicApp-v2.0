package com.example.android.remedicappml;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.remedicappml.facedetector.FaceDetectorProcessor;
import com.google.android.gms.common.annotation.KeepName;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.opencv.android.OpenCVLoader;


@KeepName
public final class  MainActivity extends AppCompatActivity
        implements OnRequestPermissionsResultCallback,
        AdapterView.OnItemSelectedListener,
        CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "LivePreviewActivity";
    private static final int PERMISSION_REQUESTS = 1;

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private static final String FACE_DETECTION = "Face Detection";
    private String selectedModel = FACE_DETECTION;

    private ProgressBar progressBar;
    private int progressStatus = 0;
    private static int progress;
    private Handler handler = new Handler();

    FaceDetectorProcessor faceDetectorProcessor;

    /* Created this variable on 06.05.2022
     to pass this to FaceDetector processor
     The calculated value will be passed to
     dashboard page
    */
    private final ArrayList<Float> signals = new ArrayList<Float>(2);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision_live_preview);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        preview = findViewById(R.id.preview_view);
        if (preview == null) {
            Log.d(TAG, "Preview is null");
        }

        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
        } else {
            getRuntimePermissions();
        }

        if (!OpenCVLoader.initDebug())
            Log.e("OpenCV", "Unable to load OpenCV!");
        else
            Log.d("OpenCV", "OpenCV loaded Successfully!");

        // Intent intent = new Intent(this, Dashboard.class);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setMax(200);

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 200) {
                    progressStatus = sleepThread();
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                        }
                    });
                }
                handler.post(new Runnable() {
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        if (progressStatus == 200) {
                            List<Float> vitals = faceDetectorProcessor.returnSignals();
                            // intent.putExtra("vitals", (Serializable) faceDetectorProcessor.returnSignals());
                            // startActivity(intent);
                            sendUserVitals(vitals.get(0), vitals.get(1), 0F);
                        }
                    }
                });
            }

            private int sleepThread() {
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return ++progress;
            }
        }).start();
        faceDetectorProcessor = new FaceDetectorProcessor(this, signals);
    }

    private void sendUserVitals(Float bpm, Float spo2, Float si) {
        UserVitals uservitals = new UserVitals();
        uservitals.setBpm(bpm);
        uservitals.setSpo2(spo2);
        uservitals.setSI(si);
        Intent intent = new Intent(MainActivity.this, Dashboard.class);
        Bundle b = new Bundle();
        b.putSerializable("vitals", uservitals);
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        createCameraSource(selectedModel);
        startCameraSource();
    }

    /** Stops the camera. */
    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    public void checkIfItGetsCalled() {}

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(),
                                    PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            createCameraSource(selectedModel);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission granted: " + permission);
            return true;
        }
        Log.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "Set facing");
        if (cameraSource != null) {
            if (isChecked) {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
            } else {
                cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
            }
        }
        preview.stop();
        startCameraSource();
    }

    private void createCameraSource(String model) {
        // If there's no existing cameraSource, create one.
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
            cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
        }

        try {
            Log.i(TAG, "Using Face Detector Processor");
            cameraSource.setMachineLearningFrameProcessor(faceDetectorProcessor);
        } catch (RuntimeException e) {
          Log.e(TAG, "Can not create image processor: " + model, e);
          Toast.makeText(
            getApplicationContext(),
                "Can not create image processor: " + e.getMessage(),
                Toast.LENGTH_LONG)
                .show();
        }
    }

    /**
     * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }
}