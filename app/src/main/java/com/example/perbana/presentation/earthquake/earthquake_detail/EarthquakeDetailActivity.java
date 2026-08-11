package com.example.perbana.presentation.earthquake.earthquake_detail;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.perbana.MainActivity;
import com.example.perbana.R;
import com.example.perbana.db.model.AutoEarthquake;

public class EarthquakeDetailActivity extends AppCompatActivity {
    private final String TAG = "EarthquakeDetailActivity";

    //Variabel
    private AutoEarthquake autoEarthquake = null;

    //View Group
    private ImageView ivDetailShakemap = null;
    private TextView tvDetailMagnitude = null;
    private TextView tvDetailStatus = null;
    private TextView tvDetailRegion = null;
    private TextView tvDetailTime = null;
    private TextView tvDetailDepth = null;
    private TextView tvDetailCoordinat = null;
    private TextView tvDetailLatitudeLongitude = null;
    private TextView tvDetailFelt = null;
    private LinearLayout llDirasakanContainer = null;


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

        initView();
    }

    private void initView() {
        ivDetailShakemap = findViewById(R.id.iv_detail_shakemap);
        tvDetailMagnitude = findViewById(R.id.tv_detail_magnitude);
        tvDetailStatus = findViewById(R.id.tv_detail_status);
        tvDetailRegion = findViewById(R.id.tv_detail_region);
        tvDetailTime = findViewById(R.id.tv_detail_time);
        tvDetailDepth = findViewById(R.id.tv_detail_depth);
        tvDetailCoordinat = findViewById(R.id.tv_detail_coordinat);
        tvDetailLatitudeLongitude = findViewById(R.id.tv_detail_latitude_longitude);
        tvDetailFelt = findViewById(R.id.tv_detail_felt);
        llDirasakanContainer = findViewById(R.id.ll_dirasakan_container);

        Glide.with(EarthquakeDetailActivity.this)
                .load("https://static.bmkg.go.id/" + autoEarthquake.getShakemap())
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
                .into(ivDetailShakemap);

        double magnitudeValue = Double.parseDouble(autoEarthquake.getMagnitude());
        tvDetailMagnitude.setText("M " + magnitudeValue);
        tvDetailStatus.setText(autoEarthquake.getPotensi());
        tvDetailRegion.setText(autoEarthquake.getWilayah());
        tvDetailTime.setText(autoEarthquake.getTanggal() + " • " + autoEarthquake.getJam());
        tvDetailDepth.setText(autoEarthquake.getKedalaman());
        tvDetailCoordinat.setText(autoEarthquake.getCoordinates());
        tvDetailLatitudeLongitude.setText(autoEarthquake.getLintang() + " / " + autoEarthquake.getBujur());
        tvDetailFelt.setText(autoEarthquake.getDirasakan());
        llDirasakanContainer.setVisibility(autoEarthquake.getDirasakan().isEmpty() ? LinearLayout.GONE : LinearLayout.VISIBLE);

        if (magnitudeValue >= 6.0) {
            tvDetailMagnitude.setTextColor(ContextCompat.getColor(EarthquakeDetailActivity.this, R.color.danger));
            tvDetailStatus.setBackgroundResource(R.drawable.bg_badge_danger);
        } else if (magnitudeValue >= 5.0) {
            tvDetailMagnitude.setTextColor(ContextCompat.getColor(EarthquakeDetailActivity.this, R.color.warning));
            tvDetailStatus.setBackgroundResource(R.drawable.bg_badge_warning);
        } else {
            tvDetailMagnitude.setTextColor(ContextCompat.getColor(EarthquakeDetailActivity.this, R.color.accent_blue));
            tvDetailStatus.setBackgroundResource(R.drawable.bg_badge_soft);
        }
    }
}