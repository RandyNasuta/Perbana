package com.example.perbana.repository;

import com.example.perbana.api.ApiClient;
import com.example.perbana.api.ApiService;

import retrofit2.Callback;

public class GempaRepository {
    private ApiService apiService;

    public GempaRepository() {
        this.apiService = ApiClient.getClient(1);
    }
}
