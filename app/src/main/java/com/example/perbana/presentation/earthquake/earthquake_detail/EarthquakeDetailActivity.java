package com.example.perbana.presentation.earthquake.earthquake_detail;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.perbana.R;
import com.example.perbana.db.model.AutoEarthquake;

public class EarthquakeDetailActivity extends AppCompatActivity {
    private final String TAG = "EarthquakeDetailActivity";

    //Variabel
    private AutoEarthquake autoEarthquake = null;

    //View Group

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_earthquake_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_earthquake_detail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null) {
            autoEarthquake = getIntent().getParcelableExtra("EXTRA_GEMPA");
        }

    }
}