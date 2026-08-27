package com.example.perbana.presentation.weather.weather_warning_list;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.perbana.MainActivity;
import com.example.perbana.R;
import com.example.perbana.adapter.WeatherWarningAdapter;
import com.example.perbana.db.model.Rss;
import com.example.perbana.db.model.WeatherWarning;
import com.example.perbana.db.repository.WeatherWarningRepository;
import com.example.perbana.util.AppConstants;
import com.example.perbana.util.DateUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherWarningListActivity extends AppCompatActivity {
    private final String TAG = "WeatherWarningListActivity";

    //Repository
    private WeatherWarningRepository weatherWarningRepository;

    //Variabel
    private List<WeatherWarning> weatherWarningList = new ArrayList<>();
    private String lastBuildDate = "";
    private WeatherWarningAdapter adapter = null;

    //View Group
    private RecyclerView rvWeatherWarningList = null;
    private TextView tvWeatherWarningLastDate = null;
    private SwipeRefreshLayout main = null;

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
        initRepository();

        tvWeatherWarningLastDate.setText("Waktu pemutakhiran data: " + DateUtil.weatherWarningDate(lastBuildDate));

        main.setOnRefreshListener(() -> {
            getWeatherWarningList();
            main.setRefreshing(false);
        });
    }

    private void initView() {
        rvWeatherWarningList = findViewById(R.id.rv_weather_warning_list);
        tvWeatherWarningLastDate = findViewById(R.id.tv_weather_warning_last_date);
        main = findViewById(R.id.main);
        main.setBackgroundResource(AppConstants.weatherBackgroundResource);

        adapter = new WeatherWarningAdapter(weatherWarningList, WeatherWarningListActivity.this);
        rvWeatherWarningList.setAdapter(adapter);
        rvWeatherWarningList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initRepository() {
        weatherWarningRepository = new WeatherWarningRepository();
    }

    private void getWeatherWarningList() {
        weatherWarningRepository.getWeatherWarning(new Callback<Rss>() {
            @Override
            public void onResponse(Call<Rss> call, Response<Rss> response) {
                Rss rssData = response.body();

                Log.i(TAG, "onResponse: Nilai rss: " + rssData.getChannel().getItemList().get(0));

                if (rssData.getChannel() != null && rssData.getChannel().getItemList() != null) {
                    List<WeatherWarning> newList = rssData.getChannel().getItemList();

                    Log.i(TAG, "onResponse: data baru weather warning: " + newList.toString());
                    if (!newList.isEmpty()) {
                        weatherWarningList = newList;
                        adapter.updateData(weatherWarningList);
                    } else {
                        weatherWarningList.clear();
                        adapter.updateData(weatherWarningList);
                    }
                }

                tvWeatherWarningLastDate.setText("Waktu pemutakhiran data: " + DateUtil.weatherWarningDate(rssData.getChannel().getLastBuildDate()));
            }

            @Override
            public void onFailure(Call<Rss> call, Throwable t) {
                Log.e(TAG, "onFailure: error saat memanggil api rss: " + t.getMessage());
            }
        });
    }
}