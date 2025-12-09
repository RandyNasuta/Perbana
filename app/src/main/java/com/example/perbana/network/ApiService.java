package com.example.perbana.network;

import com.example.perbana.model.GempaResponse;
import com.example.perbana.model.Infogempa;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    //Ambil data gempa bumi terbaru
    @GET("DataMKG/TEWS/autogempa.json")
    Call<GempaResponse> getInfoGempa();
}
