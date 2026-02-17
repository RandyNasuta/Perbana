package com.example.perbana.db.repository;

import com.example.perbana.db.network.api.ApiClient;
import com.example.perbana.db.network.api.ApiService;
import com.google.gson.JsonObject;

import retrofit2.Callback;

public class GempaRepository {
    private ApiService apiService;

    public GempaRepository() {
        this.apiService = ApiClient.getClient(1);
    }

    public void autoGempa(Callback<JsonObject> callback) {
        apiService.getAutoGempa().enqueue(callback);
    }
}
