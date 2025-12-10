package com.example.perbana;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.perbana.adapter.MainMenuAdapter;
import com.example.perbana.adapter.WeatherAdapter;
import com.example.perbana.api.response.GempaResponse;
import com.example.perbana.model.MainMenu;
import com.example.perbana.model.Weather;
import com.example.perbana.repository.GempaRepository;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG ="MainActivity";

    private GempaRepository gempaRepository;

    // Variabel
    private WeatherAdapter weatherAdapter = null;
    private MainMenuAdapter mainMenuAdapter = null;

    //View
    private RecyclerView rvMainWeather;
    private RecyclerView rvMainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();

        //Recycler View Weather
        ArrayList<Weather> weathers = new ArrayList<>();
        weathers.add(new Weather(
                "24\u2103",
                "Hujan Ringan",
                R.drawable.hujan_ringan,
                "02.00"
        ));
        weathers.add(new Weather(
                "24\u2103",
                "Berawan",
                R.drawable.berawan,
                "05.00"
        ));
        weathers.add(new Weather(
                "28\u2103",
                "Cerah Berawan",
                R.drawable.cerah_berawan,
                "08.00"
        ));
        weathers.add(new Weather(
                "30\u2103",
                "Cerah Berawan",
                R.drawable.cerah_berawan,
                "11.00"
        ));
        weathers.add(new Weather(
                "29\u2103",
                "Cerah Berawan",
                R.drawable.cerah_berawan,
                "14.00"
        ));

        weatherAdapter = new WeatherAdapter(weathers);
        rvMainWeather.setAdapter(weatherAdapter);

        rvMainWeather.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        //Recycler View Menu
        ArrayList<MainMenu> menus = new ArrayList<>();
        menus.add(new MainMenu(
                R.drawable.cloudy,
                R.string.data_prakiraan_cuaca
        ));
        menus.add(new MainMenu(
                R.drawable.thunder,
                R.string.peringatan_dini_cuaca
        ));
        menus.add(new MainMenu(
                R.drawable.earthquake,
                R.string.data_gempa_bumi
        ));

        mainMenuAdapter = new MainMenuAdapter(menus);
        rvMainMenu.setAdapter(mainMenuAdapter);

        // Atur ukuran item
        int itemWidth = getResources().getDimensionPixelSize(R.dimen.item_width);

        // Set lebay layar
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        // Hitung span count
        int spanCount = screenWidth / itemWidth;
        if (spanCount < 1) {
            spanCount = 1;
        }

        rvMainMenu.setLayoutManager(new GridLayoutManager(this, spanCount, LinearLayoutManager.VERTICAL, false));

        gempaRepository = new GempaRepository();

        gempaRepository.getInfoGempa(new Callback<GempaResponse>() {
            @Override
            public void onResponse(Call<GempaResponse> call, Response<GempaResponse> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: info gempa: " + response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<GempaResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: error karena " + t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initView() {
        rvMainWeather = findViewById(R.id.rvMainWeather);
        rvMainMenu = findViewById(R.id.rvMainMenu);
    }
}