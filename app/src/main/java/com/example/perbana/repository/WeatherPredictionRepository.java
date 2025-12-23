package com.example.perbana.repository;

import com.example.perbana.api.ApiClient;
import com.example.perbana.api.ApiService;
import com.google.gson.JsonObject;

import retrofit2.Callback;

public class WeatherPredictionRepository {
    private ApiService apiService;

    public WeatherPredictionRepository() {
        this.apiService = ApiClient.getClient(2);
    }

    public void getWeatherPrediction(String regionCode, Callback<JsonObject> callback) {
        apiService.getWeatherPrediction(regionCode).enqueue(callback);
    }
}
