package com.example.android.remedicappml;

import android.media.FaceDetector;

import com.google.mlkit.vision.face.Face;

import java.nio.ByteBuffer;
import java.util.List;

public class DataModel {
    private float beats_per_minute;
    private float oxygen_saturation;
    private float stress_index;

    public DataModel() {}

    // getters
    public float get_beats_per_minute() { return beats_per_minute; }
    public float get_oxygen_saturation() { return oxygen_saturation; }
    public float get_stress_index() { return stress_index; }

    // setters
    public void set_beats_per_minute(float bpm) { this.beats_per_minute = bpm; }
    public void set_oxygen_saturation(float spo2) { this.oxygen_saturation = spo2; }
    public void set_stress_index(float si) { this.stress_index = si; }

}
