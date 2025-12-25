package com.example.perbana.db.repository;

import com.example.perbana.db.network.api.ApiClient;
import com.example.perbana.db.network.api.ApiService;

public class GempaRepository {
    private ApiService apiService;

    public GempaRepository() {
        this.apiService = ApiClient.getClient(1);
    }
}
