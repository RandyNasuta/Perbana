package com.example.perbana.presentation.weather.weather_warning_detail;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.perbana.R;
import com.example.perbana.db.model.Area;
import com.example.perbana.db.model.Cap;
import com.example.perbana.db.model.DetailWeatherWarning;
import com.example.perbana.db.repository.WeatherWarningRepository;
import com.example.perbana.util.AppConstants;
import com.example.perbana.util.DateUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherWarningDetailActivity extends AppCompatActivity {

    private final String TAG = "WeatherWarningDetailActivity";

    //Repository
    private WeatherWarningRepository weatherWarningRepository;

    //Variabel
    private String link = "";

    //View Group
    private ImageView ivDetailInfographic = null;
    private TextView tvDetailEvent = null, tvDetailStatus = null, tvDetailHeadline = null, tvDetailTimeRange = null;
    private TextView tvDetailUrgency = null, tvDetailSaverity = null, tvDetailCetainty = null;
    private TextView tvDetailDescription = null, tvDetailIdentifier = null, tvDetailSender = null;
    private TextView tvDetailSenderName = null, tvDetailArea = null;
    private ScrollView main = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather_warning_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (getIntent() != null) {
            link = getIntent().getStringExtra("EXTRA_LINK");
        }

        initView();
        initRepository();
    }

    private void initRepository() {
        weatherWarningRepository = new WeatherWarningRepository();
        Log.i(TAG, "initRepository: Link: " + link);
        if (!link.isEmpty()){
            weatherWarningRepository.getWeatherWarningDetail(link, new Callback<Cap>() {
                @Override
                public void onResponse(Call<Cap> call, Response<Cap> response) {
                    Cap capData = response.body();
                    Log.e(TAG, "onResponse: capData: " + capData);
                    if (capData != null && capData.getInfo() != null) {
                        Log.e(TAG, "onResponse: detail data cap: " + capData.getInfo().toString());
                        DetailWeatherWarning detailWeatherWarning = capData.getInfo();
                        Area area = detailWeatherWarning.getArea();

                        tvDetailEvent.setText(detailWeatherWarning.getEvent());
                        tvDetailStatus.setText(capData.getStatus());
                        tvDetailHeadline.setText(detailWeatherWarning.getHeadline());

                        String effectiveDate = DateUtil.parseDate("yyyy-MM-dd'T'HH:mm:ssXXX", "EEE, dd MMM yyyy • HH:mm 'WIB'", detailWeatherWarning.getEffective());
                        String expiresDate = DateUtil.parseDate("yyyy-MM-dd'T'HH:mm:ssXXX", "EEE, dd MMM yyyy • HH:mm 'WIB'", detailWeatherWarning.getExpires());
                        tvDetailTimeRange.setText(effectiveDate + " - " + expiresDate);

                        tvDetailUrgency.setText(detailWeatherWarning.getUrgency());
                        tvDetailSaverity.setText(detailWeatherWarning.getSeverity());
                        tvDetailCetainty.setText(detailWeatherWarning.getCertainty());
                        tvDetailDescription.setText(detailWeatherWarning.getDescription());
                        tvDetailIdentifier.setText(capData.getIdentifier());
                        tvDetailSender.setText(capData.getSender());
                        tvDetailSenderName.setText(detailWeatherWarning.getSenderName());

                        if (area != null && !area.getAreaDesc().isEmpty()) {
                            tvDetailArea.setText(area.getAreaDesc());
                        } else {
                            tvDetailArea.setText("Tidak diketahui");
                        }


                        String image = detailWeatherWarning.getWeb();
                        Glide.with(WeatherWarningDetailActivity.this)
                                .load(image)
                                .placeholder(R.drawable.missing_image)
                                .error(R.drawable.missing_image)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .diskCacheStrategy(DiskCacheStrategy.DATA)
                                .listener(new RequestListener<Drawable>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                        Log.e(TAG, "onLoadFailed: Gagal load gambar peringatan cuaca: " + e.getMessage());
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                        Log.i(TAG, "onResourceReady: Berhasil load gambar peringatan cuaca:");
                                        return false;
                                    }
                                })
                                .into(ivDetailInfographic);
                    }
                }

                @Override
                public void onFailure(Call<Cap> call, Throwable t) {
                    Log.e(TAG, "onFailure: Saat mengambil data detail peringatan cuaca: " + t.getMessage());
                }
            });
        }
    }

    private void initView() {
        main = findViewById(R.id.main);
        ivDetailInfographic = findViewById(R.id.iv_detail_infographic);
        tvDetailEvent = findViewById(R.id.tv_detail_event);
        tvDetailStatus = findViewById(R.id.tv_detail_status);
        tvDetailHeadline = findViewById(R.id.tv_detail_headline);
        tvDetailTimeRange = findViewById(R.id.tv_detail_time_range);
        tvDetailUrgency = findViewById(R.id.tv_detail_urgency);
        tvDetailSaverity = findViewById(R.id.tv_detail_saverity);
        tvDetailCetainty = findViewById(R.id.tv_detail_cetainty);
        tvDetailDescription = findViewById(R.id.tv_detail_description);
        tvDetailIdentifier = findViewById(R.id.tv_detail_identifier);
        tvDetailSender = findViewById(R.id.tv_detail_sender);
        tvDetailSenderName = findViewById(R.id.tv_detail_sender_name);
        tvDetailArea = findViewById(R.id.tv_detail_area);
        main.setBackgroundResource(AppConstants.weatherBackgroundResource);
    }
}