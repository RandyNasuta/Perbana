package com.example.perbana.presentation.weather.weather_warning_list;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perbana.R;
import com.example.perbana.adapter.WeatherWarningAdapter;
import com.example.perbana.db.model.WeatherWarning;
import com.example.perbana.util.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WeatherWarningListActivity extends AppCompatActivity {
    private final String TAG = "WeatherWarningListActivity";

    //Variabel
    private List<WeatherWarning> weatherWarningList = new ArrayList<>();
    private String lastBuildDate = "";
    private WeatherWarningAdapter adapter = null;

    //View Group
    private RecyclerView rvWeatherWarningList = null;
    private TextView tvWeatherWarningLastDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather_warning_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null) {
            Serializable serializableData = getIntent().getSerializableExtra("EXTRA_WEATHER_WARNING_LIST");

            if (serializableData instanceof ArrayList) {
                @SuppressWarnings("unchecked")
                ArrayList<WeatherWarning> dataReceived = (ArrayList<WeatherWarning>) serializableData;
                weatherWarningList.addAll(dataReceived);
            }
            lastBuildDate = getIntent().getStringExtra("EXTRA_LAST_BUILD_DATE");
        }

        initView();
    }

    private void initView() {
        rvWeatherWarningList = findViewById(R.id.rv_weather_warning_list);
        tvWeatherWarningLastDate = findViewById(R.id.tv_weather_warning_last_date);

        tvWeatherWarningLastDate.setText("Waktu pemutakhiran data: " + DateUtil.weatherWarningDate(lastBuildDate));

        adapter = new WeatherWarningAdapter(weatherWarningList, WeatherWarningListActivity.this);
        rvWeatherWarningList.setAdapter(adapter);
        rvWeatherWarningList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }
}