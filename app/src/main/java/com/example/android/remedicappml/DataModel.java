package com.example.android.remedicappml;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

public class DataModel {
    private float beats_per_minute;
    private float oxygen_saturation;
    private float stress_index;
    private ArrayList<Integer> fps;

    private ArrayList<Double> BlueAvg;
    private ArrayList<Double> GreenAvg;
    private ArrayList<Double> RedAvg;

    public DataModel() {}

    // getters
    public float get_beats_per_minute() { return beats_per_minute; }
    public float get_oxygen_saturation() { return oxygen_saturation; }
    public float get_stress_index() { return stress_index; }
    public ArrayList<Integer> get_fps() { return fps; }

    public ArrayList<Double> get_blue_avg() { return BlueAvg; }
    public ArrayList<Double> get_green_avg() { return GreenAvg; }
    public ArrayList<Double> get_red_avg() { return RedAvg; }


    // setters
    public void set_beats_per_minute(float bpm) { this.beats_per_minute = bpm; }
    public void set_oxygen_saturation(float spo2) { this.oxygen_saturation = spo2; }
    public void set_stress_index(float si) { this.stress_index = si; }
    public void set_fps(ArrayList<Integer> fps) { this.fps = fps; }

    public void set_data_model(ArrayList<Double> blueAvg, ArrayList<Double> greenAvg, ArrayList<Double> redAvg) {
        this.BlueAvg = blueAvg;
        this.GreenAvg = greenAvg;
        this.RedAvg = redAvg;
    }

}
