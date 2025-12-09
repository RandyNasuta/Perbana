package com.example.perbana.repository;

import com.example.perbana.model.GempaResponse;
import com.example.perbana.model.Infogempa;
import com.example.perbana.network.ApiClient;
import com.example.perbana.network.ApiService;

import retrofit2.Callback;

public class InfoGempaRepository {
    private ApiService apiService;

    public InfoGempaRepository() {
        this.apiService = ApiClient.getClient();
    }

    public void getInfoGempa(Callback<GempaResponse> callback) {
        apiService.getInfoGempa().enqueue(callback);
    }
}
