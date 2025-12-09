package com.example.perbana;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.perbana.model.Gempa;
import com.example.perbana.model.GempaResponse;
import com.example.perbana.model.Infogempa;
import com.example.perbana.repository.InfoGempaRepository;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private final String TAG ="MainActivity";

    private InfoGempaRepository infoGempaRepository;

    //View
    private TextView tvMain;
    private ImageView ivMain;

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

        infoGempaRepository = new InfoGempaRepository();

        infoGempaRepository.getInfoGempa(new Callback<GempaResponse>() {
            @Override
            public void onResponse(Call<GempaResponse> call, Response<GempaResponse> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: info gempa: " + response.body().toString());
                    tvMain.setText(response.body().toString());

                    Glide.with(MainActivity.this)
                            .load("https://static.bmkg.go.id/" + response.body().getInfogempa().getGempa().getShakemap())
                            .into(ivMain);
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
        tvMain = findViewById(R.id.tvMain);
        ivMain = findViewById(R.id.ivMain);
    }
}