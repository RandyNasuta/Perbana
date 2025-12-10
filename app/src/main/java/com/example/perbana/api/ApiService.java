package com.example.perbana.api;

import com.example.perbana.api.response.GempaResponse;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    //Ambil data gempa bumi terbaru
    @GET("DataMKG/TEWS/autogempa.json")
    Call<GempaResponse> getInfoGempa();
}
