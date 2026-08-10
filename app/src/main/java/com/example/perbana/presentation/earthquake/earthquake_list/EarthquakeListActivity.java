package com.example.perbana.presentation.earthquake.earthquake_list;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.perbana.MainActivity;
import com.example.perbana.R;
import com.example.perbana.adapter.EarthquakeAdapter;
import com.example.perbana.adapter.EarthquakeFeltAdapter;
import com.example.perbana.db.model.Earthquake;
import com.example.perbana.db.model.EarthquakeFelt;
import com.example.perbana.db.repository.GempaRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EarthquakeListActivity extends AppCompatActivity {
    private final String TAG = "EarthquakeListActivity";

    //Variabel
    private final ArrayList<Earthquake> earthquakeList = new ArrayList<>();
    private final ArrayList<EarthquakeFelt> earthquakeFeltList = new ArrayList<>();

    //Adapter
    private EarthquakeAdapter earthquakeAdapter;
    private EarthquakeFeltAdapter earthquakeFeltAdapter;

    //View group Earthquake
    private LinearLayout llHeaderEarthquakeList;
    private LinearLayout llExpandableEarthquakeList;
    private ImageView ivArrowEarthquakeList;
    private RecyclerView rvEarthquake;


    //View Group Earthquake Felt
    private LinearLayout llHeaderEarthquakeListFelt;
    private LinearLayout llExpandableEarthquakeListFelt;
    private ImageView ivArrowEarthquakeListFelt;
    private RecyclerView rvEarthquakeFelt;

    //Repository
    private GempaRepository gempaRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_earthquake_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_earthquake_list), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initView();
        initRepository();

        //Buka tutup list earthquake
        llHeaderEarthquakeList.setOnClickListener(view -> {
            boolean isExpanded = llExpandableEarthquakeList.getVisibility() == VISIBLE;
            AutoTransition transition = new AutoTransition();
            transition.setDuration(300);
            transition.setInterpolator(new OvershootInterpolator(1.2f));

            TransitionManager.beginDelayedTransition((ViewGroup) view, transition);

            Log.i(TAG, "onClick: isExpanded layout: " + isExpanded);
            if (isExpanded) {
                llExpandableEarthquakeList.setVisibility(GONE);
                ivArrowEarthquakeList.animate().rotation(0f).setDuration(300).start();
            } else {
                llExpandableEarthquakeList.setVisibility(VISIBLE);
                ivArrowEarthquakeList.animate().rotation(180f).setDuration(300).start();
            }
        });

        //Buka tutup list earthquake felt
        llHeaderEarthquakeListFelt.setOnClickListener(view -> {
            boolean isExpanded = llExpandableEarthquakeListFelt.getVisibility() == VISIBLE;
            AutoTransition transition = new AutoTransition();
            transition.setDuration(300);
            transition.setInterpolator(new OvershootInterpolator(1.2f));

            TransitionManager.beginDelayedTransition((ViewGroup) view, transition);

            Log.i(TAG, "onClick: isExpanded layout: " + isExpanded);
            if (isExpanded) {
                llExpandableEarthquakeListFelt.setVisibility(GONE);
                ivArrowEarthquakeListFelt.animate().rotation(0f).setDuration(300).start();
            } else {
                llExpandableEarthquakeListFelt.setVisibility(VISIBLE);
            }
        });
    }

    private void initView(){
        llHeaderEarthquakeList = findViewById(R.id.ll_header_earthquake_list);
        llExpandableEarthquakeList = findViewById(R.id.layout_expandable_earthquake_list);
        ivArrowEarthquakeList = findViewById(R.id.iv_arrow_earthquake_list);
        rvEarthquake = findViewById(R.id.rv_earthquake);
        rvEarthquake.setLayoutManager(new LinearLayoutManager(this));
        earthquakeAdapter = new EarthquakeAdapter(EarthquakeListActivity.this, earthquakeList);
        rvEarthquake.setAdapter(earthquakeAdapter);

        llHeaderEarthquakeListFelt = findViewById(R.id.ll_header_earthquake_list_felt);
        llExpandableEarthquakeListFelt = findViewById(R.id.layout_expandable_earthquake_list_felt);
        ivArrowEarthquakeListFelt = findViewById(R.id.iv_arrow_earthquake_list_felt);
        rvEarthquakeFelt = findViewById(R.id.rv_earthquake_felt);
        rvEarthquakeFelt.setLayoutManager(new LinearLayoutManager(this));
        earthquakeFeltAdapter = new EarthquakeFeltAdapter(EarthquakeListActivity.this, earthquakeFeltList);
        rvEarthquakeFelt.setAdapter(earthquakeFeltAdapter);
    }

    private void initRepository() {
        gempaRepository = new GempaRepository();

        gempaRepository.getEarthquakeList(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    JsonObject infoGempa = body.getAsJsonObject("Infogempa");
                    JsonArray gempa = infoGempa.getAsJsonArray("gempa");

                    earthquakeList.clear();

                    for (JsonElement element : gempa) {
                        JsonObject data = element.getAsJsonObject();
                        earthquakeList.add(
                                new Earthquake(
                                        data.get("Tanggal").getAsString(),
                                        data.get("Jam").getAsString(),
                                        data.get("DateTime").getAsString(),
                                        data.get("Coordinates").getAsString(),
                                        data.get("Lintang").getAsString(),
                                        data.get("Bujur").getAsString(),
                                        data.get("Magnitude").getAsString(),
                                        data.get("Kedalaman").getAsString(),
                                        data.get("Wilayah").getAsString(),
                                        data.get("Potensi").getAsString()
                                )
                        );
                    }

                    earthquakeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(EarthquakeListActivity.this, "Terjadi suatu kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: error saat memanggil api auto gempa: " + t.getMessage());
            }
        });

        gempaRepository.getEarthquakeFeltList(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject body = response.body();
                    JsonObject infoGempa = body.getAsJsonObject("Infogempa");
                    JsonArray gempa = infoGempa.getAsJsonArray("gempa");

                    earthquakeFeltList.clear();

                    for (JsonElement element : gempa) {
                        JsonObject data = element.getAsJsonObject();
                        earthquakeFeltList.add(
                                new EarthquakeFelt(
                                        data.get("Tanggal").getAsString(),
                                        data.get("Jam").getAsString(),
                                        data.get("DateTime").getAsString(),
                                        data.get("Coordinates").getAsString(),
                                        data.get("Lintang").getAsString(),
                                        data.get("Bujur").getAsString(),
                                        data.get("Magnitude").getAsString(),
                                        data.get("Kedalaman").getAsString(),
                                        data.get("Wilayah").getAsString(),
                                        data.get("Dirasakan").getAsString()
                                )
                        );
                    }

                    earthquakeFeltAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(EarthquakeListActivity.this, "Terjadi suatu kesalahan: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onFailure: error saat memanggil api auto gempa: " + t.getMessage());
            }
        });
    }
}