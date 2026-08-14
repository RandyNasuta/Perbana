package com.example.perbana.db.repository;

import com.example.perbana.db.model.Cap;
import com.example.perbana.db.network.api.ApiClient;
import com.example.perbana.db.network.api.ApiService;
import com.example.perbana.db.model.Rss;
import retrofit2.Callback;

public class WeatherWarningRepository {
    private ApiService apiService;

    public WeatherWarningRepository() {
        this.apiService = ApiClient.getClient(3);
    }

    public void getWeatherWarning(Callback<Rss> callback) {
        apiService.getWeatherWarningList().enqueue(callback);
    }

    public void getWeatherWarningDetail(String link, Callback<Cap> callback) {
        apiService.getWeatherWarningDetail(link).enqueue(callback);
    }
}
