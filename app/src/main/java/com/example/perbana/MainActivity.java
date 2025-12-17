package com.example.perbana;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import com.example.perbana.model.RegionCode;
import com.example.perbana.model.Weather;
import com.example.perbana.repository.GempaRepository;
import com.example.perbana.util.CsvReader;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG ="MainActivity";

    private GempaRepository gempaRepository;

    // Variabel
    private WeatherAdapter weatherAdapter = null;
    private MainMenuAdapter mainMenuAdapter = null;
    private AlertDialog.Builder dialog = null;
    private ArrayList<RegionCode> regionCodeList = new ArrayList<>();
    private CsvReader csvReader = null;

    //View
    private RecyclerView rvMainWeather;
    private RecyclerView rvMainMenu;
    private TextView tvLocation;

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

        // Untuk membaca data kode wilayah
        csvReader = new CsvReader(getResources().openRawResource(R.raw.kode_wilayah));
        for (String[] data : csvReader.read()) {
            regionCodeList.add(new RegionCode(data[0], data[1]));
        }

        initView();

        //Recycler View Weather
        weatherAdapter = new WeatherAdapter(new ArrayList<Weather>());
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

        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new MaterialAlertDialogBuilder(MainActivity.this);
                LayoutInflater inflater = getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_choose_region, null);
                dialog.setView(dialogView);
                dialog.setCancelable(true);
                dialog.setTitle("Pilih daerah");

                ArrayList<RegionCode> provinceRegionList = new ArrayList<>();
                ArrayList<RegionCode> regencyRegionList = new ArrayList<>();
                ArrayList<RegionCode> subDistrictRegionList = new ArrayList<>();
                ArrayList<RegionCode> villageRegionList = new ArrayList<>();

                for (RegionCode data : regionCodeList) {
                    if ((data.getCode().length() - data.getCode().replace(".", "").length()) == 0) {
                        provinceRegionList.add(data);
                    } else if ((data.getCode().length() - data.getCode().replace(".", "").length()) == 1) {
                        regencyRegionList.add(data);
                    } else if ((data.getCode().length() - data.getCode().replace(".", "").length()) == 2) {
                       subDistrictRegionList.add(data);
                    } else if ((data.getCode().length() - data.getCode().replace(".", "").length()) == 3) {
                        villageRegionList.add(data);
                    }
                }

                ArrayAdapter provinceAdapter = new ArrayAdapter(MainActivity.this, R.layout.item_region, provinceRegionList);
                AutoCompleteTextView autoProvince = dialogView.findViewById(R.id.autoProvince);
                autoProvince.setAdapter(provinceAdapter);

                autoProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                ArrayAdapter regencyAdapter = new ArrayAdapter(MainActivity.this, R.layout.item_region, regencyRegionList);
                AutoCompleteTextView autoRegency = dialogView.findViewById(R.id.autoRegency);
                autoRegency.setAdapter(regencyAdapter);

                ArrayAdapter subDistrictAdapter = new ArrayAdapter(MainActivity.this, R.layout.item_region, subDistrictRegionList);
                AutoCompleteTextView autoSubdistrict = dialogView.findViewById(R.id.autoSubdistrict);
                autoSubdistrict.setAdapter(subDistrictAdapter);

                ArrayAdapter villageAdapter = new ArrayAdapter(MainActivity.this, R.layout.item_region, villageRegionList);
                AutoCompleteTextView autoVillage = dialogView.findViewById(R.id.autoVillage);
                autoVillage.setAdapter(villageAdapter);

                dialog.setPositiveButton("Pilih", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                dialog.show();
            }
        });
    }

    private void initView() {
        rvMainWeather = findViewById(R.id.rvMainWeather);
        rvMainMenu = findViewById(R.id.rvMainMenu);
        tvLocation = findViewById(R.id.tvLocation);
    }
}