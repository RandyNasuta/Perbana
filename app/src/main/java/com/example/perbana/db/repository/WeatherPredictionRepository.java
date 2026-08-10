package com.example.perbana.db.repository;

import com.example.perbana.db.network.api.ApiClient;
import com.example.perbana.db.network.api.ApiService;
import com.google.gson.JsonObject;

import retrofit2.Callback;

public class WeatherPredictionRepository {
    private final ApiService apiService;

    public WeatherPredictionRepository() {
        this.apiService = ApiClient.getClient(2);
    }

    public void getWeatherPrediction(String regionCode, Callback<JsonObject> callback) {
        apiService.getWeatherPrediction(regionCode).enqueue(callback);
    }
}
