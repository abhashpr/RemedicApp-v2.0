package com.example.android.remedicappml;
import java.io.Serializable;

public class UserVitals implements Serializable {
    private Float beats_per_minute;
    private Float oxygen_saturation;
    private Float stress_index;

    public Float getBpm() {
        return beats_per_minute;
    }
    public void setBpm(Float bpm) {
        this.beats_per_minute = bpm;
    }

    public Float getSpo2() {
        return oxygen_saturation;
    }
    public void setSpo2(Float spo2) {
        this.oxygen_saturation = spo2;
    }

    public Float getStressIndex() {
        return stress_index;
    }
    public void setSI(Float si) {
        this.stress_index = si;
    }
}
