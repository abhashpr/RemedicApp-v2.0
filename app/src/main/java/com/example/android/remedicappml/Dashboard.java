package com.example.android.remedicappml;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import java.util.HashMap;
import java.util.List;

public class Dashboard extends Activity {
    private Intent mainActivity;
    private LinearLayout type_of_reading_user_controls;
    private ImageView heart_beat_selector, spo2_selector,
            stress_index_selector, respiration_rate_selector,
            heart_rate_variability_selector, blood_pressure_selector,
            activeView;
    private TextView graphText, mainReading;
    private final HashMap<String, Integer> mapping = new HashMap<>();
    private final HashMap<String, String> strMap = new HashMap<>();
    private int readingsUserControlCount, activeSelector;
    public MyGraph mygraph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView backArrow;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);

        mainActivity = new Intent(this, MainActivity.class);

        mygraph = findViewById(R.id.myGraph);
        graphText = findViewById(R.id.graph_text);
        mainReading = findViewById(R.id.mainReadingText);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            UserVitals uservitals = (UserVitals) getIntent().getSerializableExtra("vitals");
            //The key argument here must match that used in the other activity
            mainReading.setText(String.valueOf(Math.round(uservitals.getBpm()/2)));
            // Log.d("PassedValue", String.valueOf(uservitals.getBpm()));
        }

        backArrow = findViewById(R.id.back_arrow);
        type_of_reading_user_controls = findViewById(R.id.type_of_reading_user_controls);

        activeSelector = R.id.heart_beat_selector;
        activeView = findViewById(activeSelector);

        mapString2Drawables();

        heart_beat_selector = findViewById(R.id.heart_beat_selector);
        myFunc("heart_beat_selector", heart_beat_selector);

        spo2_selector = findViewById(R.id.spo2_selector);
        myFunc("spo2_selector", spo2_selector);

        stress_index_selector = findViewById(R.id.stress_index_selector);
        myFunc("stress_index_selector", stress_index_selector);

        respiration_rate_selector = findViewById(R.id.respiration_rate_selector);
        myFunc("respiration_rate_selector", respiration_rate_selector);

        heart_rate_variability_selector = findViewById(R.id.heart_rate_variability_selector);
        myFunc("heart_rate_variability_selector", heart_rate_variability_selector);

        blood_pressure_selector = findViewById(R.id.blood_pressure_selector);
        myFunc("blood_pressure_selector", blood_pressure_selector);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(mainActivity);
            }
        });
    }

    private void mapString2Drawables() {
        mapping.put("heart_beat_selector_active", R.drawable.ic_heart_beat_selector_active);
        mapping.put("heart_beat_selector_inactive", R.drawable.ic_heart_beat_selector_inactive);
        mapping.put("spo2_selector_active", R.drawable.ic_spo2_selector_active);
        mapping.put("spo2_selector_inactive", R.drawable.ic_spo2_selector_inactive);
        mapping.put("stress_index_selector_active", R.drawable.ic_stress_index_selector_active);
        mapping.put("stress_index_selector_inactive", R.drawable.ic_stress_index_selector_inactive);
        mapping.put("heart_rate_variability_selector_active", R.drawable.ic_heart_rate_variability_selector_active);
        mapping.put("heart_rate_variability_selector_inactive", R.drawable.ic_heart_rate_variability_selector_inactive);
        mapping.put("respiration_rate_selector_active", R.drawable.ic_respiration_rate_selector_active);
        mapping.put("respiration_rate_selector_inactive", R.drawable.ic_respiration_rate_selector_inactive);
        mapping.put("blood_pressure_selector_active", R.drawable.ic_blood_pressure_selectror_active);
        mapping.put("blood_pressure_selector_inactive", R.drawable.ic_blood_pressure_selector_inactive);


        strMap.put("heart_beat_selector_active", "BPM TRACKER");
        strMap.put("spo2_selector_active", "SPO2 TRACKER");
        strMap.put("stress_index_selector_active", "STRESS INDICATOR TRACKER");
        strMap.put("heart_rate_variability_selector_active", "HEART RATE VARIABILITY TRACKER");
        strMap.put("respiration_rate_selector_active", "RESPIRATION RATE TRACKER");
        strMap.put("blood_pressure_selector_active", "BLOOD PRESSURE TRACKER");
    }
    private void myFunc(String selector, ImageView iv) {
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // make the last view inactive
                activeView = findViewById(activeSelector);
                activeView.setBackground(getDrawable(R.drawable.icon_background));
                Integer res = mapping.get(getResources().getResourceEntryName(activeView.getId()) + "_inactive");
                activeView.setImageResource(res);

                // make current view active
                iv.setBackground(getDrawable(R.drawable.curved_rect_active));
                res = mapping.get(selector + "_active");
                iv.setImageResource(res);

                activeSelector = iv.getId();
                activeView = findViewById(activeSelector);
                mygraph.setParameter();
                mygraph.invalidate();
                graphText.setText(strMap.get(selector + "_active"));
            }
        });
    }
}