package com.example.perbana;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.perbana.adapter.WeatherAdapter;
import com.example.perbana.db.model.RegionCode;
import com.example.perbana.db.model.Weather;
import com.example.perbana.db.repository.GempaRepository;
import com.example.perbana.db.repository.RegionRepository;
import com.example.perbana.db.repository.WeatherPredictionRepository;
import com.example.perbana.util.CsvReader;
import com.example.perbana.util.DateUtil;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private final String TAG ="MainActivity";

    // Repository
    private GempaRepository gempaRepository;
    private WeatherPredictionRepository weatherPredictionRepository;
    private RegionRepository regionRepository;

    // Variabel
    private WeatherAdapter weatherAdapter = null;
    private AlertDialog.Builder dialog = null;
    private final ArrayList<RegionCode> regionCodeList = new ArrayList<>();
    private CsvReader csvReader = null;

    //List untuk menampung data kode daerah;
    private ArrayList<RegionCode> provinceRegionList = null;
    private ArrayList<RegionCode> regencyRegionList = null;
    private ArrayList<RegionCode> subDistrictRegionList = null;
    private ArrayList<RegionCode> villageRegionList = null;
    private String choosenRegion = "";

    //View
    private RecyclerView rvMainWeather;
    private TextView tvLocation;
    private AutoCompleteTextView autoProvince = null;
    private AutoCompleteTextView autoRegency = null;
    private AutoCompleteTextView autoSubdistrict = null;
    private AutoCompleteTextView autoVillage = null;
    private TextInputLayout tilProvince = null;
    private TextInputLayout tilRegency = null;
    private TextInputLayout tilSubdistrict = null;
    private TextInputLayout tilVilage = null;
    private ProgressBar pbMain = null;
    private ImageView ivCurrentWeather = null;
    private MaterialCardView cardWeatherList = null;
    private TextView tvMainCurrentPlace;
    private TextView tvMainCurrentWeather;
    private TextView tvMainCurrentTime;
    private ImageView ivAutoGempa;

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

        initRepository();
        initView();
        initLaunched();
    }

    private void currentChoosenRegion(String choosenRegion) {
        //Tampilkan progress bar
        pbMain.setVisibility(VISIBLE);

        //Ambil data dari API
        weatherPredictionRepository.getWeatherPrediction(choosenRegion, new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    JsonObject data = body.getAsJsonArray("data").get(0).getAsJsonObject();

                    JsonObject lokasi = data.getAsJsonObject("lokasi");

                    //Cuaca saat ini
                    JsonArray cuaca = data.getAsJsonArray("cuaca").get(0).getAsJsonArray();
                    JsonObject cuacaCurrent = cuaca.get(0).getAsJsonObject();

                    //Data untuk informasi cuaca sekarang
                    tvLocation.setText(lokasi.get("provinsi").getAsString() + ", " + lokasi.get("kotkab").getAsString() + ", " + lokasi.get("kecamatan").getAsString() + ", " + lokasi.get("desa").getAsString());
                    tvMainCurrentPlace.setText(cuacaCurrent.get("t").getAsString() + "℃");
                    tvMainCurrentWeather.setText(lokasi.get("desa").getAsString() + " (" + cuacaCurrent.get("weather_desc").getAsString() + ")");
                    tvMainCurrentTime.setText(DateUtil.parseDate("yyyy-MM-dd HH:mm:ss", "dd MMMM yyyy (HH:mm)", cuacaCurrent.get("local_datetime").getAsString()));

                    RequestBuilder<PictureDrawable> requestBuilder = GlideToVectorYou
                            .init()
                            .with(MainActivity.this)
                            .withListener(new GlideToVectorYouListener() {
                                @Override
                                public void onLoadFailed() {
                                    Log.e(TAG, "onLoadFailed: Gagal load gambar cuaca");
                                }

                                @Override
                                public void onResourceReady() {
                                    Log.i(TAG, "onResourceReady: Berhasil load gambar cuaca");
                                }
                            })
                            .setPlaceHolder(R.drawable.missing_image, R.drawable.missing_image)
                            .getRequestBuilder();

                    requestBuilder
                            .load(Uri.parse(cuacaCurrent.get("image").getAsString()))
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .into(ivCurrentWeather);

                    JsonArray cuacaArray = data.getAsJsonArray("cuaca");

                    ArrayList<Weather> weathers = new ArrayList<>();
                    for (JsonElement jsonElement : cuacaArray) {
                        JsonArray innerCuaca = jsonElement.getAsJsonArray();
                        for (JsonElement jsonElement1 : innerCuaca) {
                            Weather weather = new Weather(
                                    jsonElement1.getAsJsonObject().get("t").getAsString(),
                                    jsonElement1.getAsJsonObject().get("weather_desc").getAsString(),
                                    jsonElement1.getAsJsonObject().get("image").getAsString(),
                                    jsonElement1.getAsJsonObject().get("local_datetime").getAsString()
                            );
                            weathers.add(weather);
                        }
                    }

                    weatherAdapter.updateData(weathers);

                    if (weatherAdapter.getItemCount() != 0) {
                        rvMainWeather.setVisibility(VISIBLE);
                        cardWeatherList.setVisibility(GONE);
                    } else {
                        rvMainWeather.setVisibility(GONE);
                        cardWeatherList.setVisibility(VISIBLE);
                    }
                }

                //Tutup Progress Bar
                pbMain.setVisibility(GONE);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "onFailure: error saat memanggil api kondisi cuaca saat ini: " + t.getMessage());
            }
        });
    }

    private void initRepository() {
        gempaRepository = new GempaRepository();
        weatherPredictionRepository = new WeatherPredictionRepository();
        regionRepository = new RegionRepository(MainActivity.this);

        gempaRepository.autoGempa(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    JsonObject infoGempa = body.getAsJsonObject("Infogempa");
                    JsonObject gempa = infoGempa.getAsJsonObject("gempa");
                    String image = gempa.get("Shakemap").getAsString();

                    Glide.with(MainActivity.this)
                            .load("https://static.bmkg.go.id/" + image)
                            .placeholder(R.drawable.missing_image)
                            .error(R.drawable.missing_image)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .listener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                    Log.e(TAG, "onLoadFailed: Gagal load gambar auto gempa: " + e.getMessage());
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                    Log.i(TAG, "onResourceReady: Berhasil load gambar auto gempa");
                                    return false;
                                }
                            })
                            .into(ivAutoGempa);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Terjadi suatu kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: error saat memanggil api auto gempa: " + t.getMessage());
            }
        });
    }

    private void initView() {
        rvMainWeather = findViewById(R.id.rvMainWeather);
        tvLocation = findViewById(R.id.tvLocation);
        tvMainCurrentPlace = findViewById(R.id.tvMainCurrentPlace);
        tvMainCurrentWeather = findViewById(R.id.tvMainCurrentWeather);
        tvMainCurrentTime = findViewById(R.id.tvMainCurrentTime);
        pbMain = findViewById(R.id.pbMain);
        ivCurrentWeather = findViewById(R.id.ivCurrentWeather);
        cardWeatherList = findViewById(R.id.cardWeatherList);

        ivAutoGempa = findViewById(R.id.ivAutoGempa);

        //Recycler View Weather
        weatherAdapter = new WeatherAdapter(new ArrayList<Weather>());
        rvMainWeather.setAdapter(weatherAdapter);
        rvMainWeather.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        if (weatherAdapter.getItemCount() != 0) {
            rvMainWeather.setVisibility(VISIBLE);
            cardWeatherList.setVisibility(GONE);
        } else {
            rvMainWeather.setVisibility(GONE);
            cardWeatherList.setVisibility(VISIBLE);
        }

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

        //Atur fungsi tekan di sini
        tvLocation.setOnClickListener(this);
    }

    private void initLaunched() {
        //Cek apakah user sudah memilih daerah, jika sudah maka panggil api untuk cek kondisi cuaca saat ini
        if (!Objects.equals(regionRepository.getKeyRegionCode(), "")) {
            currentChoosenRegion(regionRepository.getKeyRegionCode());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvLocation) {
            dialog = new MaterialAlertDialogBuilder(MainActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_choose_region, null);
            dialog.setView(dialogView);
            dialog.setCancelable(true);
            dialog.setTitle("Pilih daerah");

            autoProvince = dialogView.findViewById(R.id.autoProvince);
            autoRegency = dialogView.findViewById(R.id.autoRegency);
            autoSubdistrict = dialogView.findViewById(R.id.autoSubdistrict);
            autoVillage = dialogView.findViewById(R.id.autoVillage);

            tilProvince = dialogView.findViewById(R.id.tilProvince);
            tilRegency = dialogView.findViewById(R.id.tilRegency);
            tilSubdistrict = dialogView.findViewById(R.id.tilSubdistrict);
            tilVilage = dialogView.findViewById(R.id.tilVilage);

            provinceRegionList = new ArrayList<>();
            regencyRegionList = new ArrayList<>();
            subDistrictRegionList = new ArrayList<>();
            villageRegionList = new ArrayList<>();

            for (RegionCode data : regionCodeList) {
                if ((data.getCode().length() - data.getCode().replace(".", "").length()) == 0) {
                    provinceRegionList.add(data);
                }
            }

            ArrayAdapter<RegionCode> provinceAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.item_region, provinceRegionList);
            autoProvince.setAdapter(provinceAdapter);

            autoProvince.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    autoRegency.setText("Kabupaten/Kota");
                    autoRegency.clearListSelection();
                    autoRegency.dismissDropDown();
                    tilRegency.setVisibility(VISIBLE);

                    tilSubdistrict.setVisibility(GONE); //Reset ke gone
                    tilVilage.setVisibility(GONE);

                    regencyRegionList.clear();
                    subDistrictRegionList.clear();
                    villageRegionList.clear();

                    for (RegionCode data : regionCodeList) {
                        if (((data.getCode().length() - data.getCode().replace(".", "").length()) == 1) && (data.getCode().contains(provinceRegionList.get(i).getCode()))) {
                            regencyRegionList.add(data);
                        }
                    }

                    ArrayAdapter<RegionCode> regencyAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.item_region, regencyRegionList);
                    autoRegency.setAdapter(regencyAdapter);
                }
            });

            autoRegency.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    autoSubdistrict.setText("Kecamatan");
                    autoSubdistrict.clearListSelection();
                    autoSubdistrict.dismissDropDown();
                    tilSubdistrict.setVisibility(VISIBLE);

                    tilVilage.setVisibility(GONE); //Reset ke gone

                    subDistrictRegionList.clear();
                    villageRegionList.clear();

                    for (RegionCode data : regionCodeList) {
                        if (((data.getCode().length() - data.getCode().replace(".", "").length()) == 2) && (data.getCode().contains(regencyRegionList.get(i).getCode()))) {
                            subDistrictRegionList.add(data);
                        }
                    }

                    ArrayAdapter<RegionCode> subDistrictAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.item_region, subDistrictRegionList);
                    autoSubdistrict.setAdapter(subDistrictAdapter);
                }
            });

            autoSubdistrict.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    autoVillage.setText("Desa/Kelurahan");
                    autoVillage.clearListSelection();
                    autoVillage.dismissDropDown();
                    tilVilage.setVisibility(VISIBLE);

                    villageRegionList.clear();

                    for (RegionCode data : regionCodeList) {
                        if (((data.getCode().length() - data.getCode().replace(".", "").length()) == 3) && (data.getCode().contains(subDistrictRegionList.get(i).getCode()))) {
                            villageRegionList.add(data);
                        }
                    }

                    ArrayAdapter<RegionCode> villageAdapter = new ArrayAdapter<>(MainActivity.this, R.layout.item_region, villageRegionList);
                    autoVillage.setAdapter(villageAdapter);
                }
            });

            autoVillage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    choosenRegion = villageRegionList.get(i).getCode();
                }
            });

            dialog.setPositiveButton("Pilih", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Simpan data daerah terpilih di preferences
                    regionRepository.setKeyRegionCode(choosenRegion);

                    currentChoosenRegion(choosenRegion);
                }
            });

            dialog.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.i(TAG, "onClick: Tidak ada daerah yang dipilih");
                }
            });

            dialog.show();
        }
    }
}