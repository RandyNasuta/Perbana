package com.example.perbana.db.repository;

import com.example.perbana.db.network.api.ApiClient;
import com.example.perbana.db.network.api.ApiService;
import com.example.perbana.db.network.util.RssResponse;
import retrofit2.Callback;

public class WeatherWarningRepository {
    private ApiService apiService;

    public WeatherWarningRepository() {
        this.apiService = ApiClient.getClient(3);
    }

    public void getWeatherWarning(Callback<RssResponse> callback) {
        apiService.getWeatherWarningList().enqueue(callback);
    }
}
