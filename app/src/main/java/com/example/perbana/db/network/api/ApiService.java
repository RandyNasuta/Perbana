package com.example.perbana.db.network.api;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("prakiraan-cuaca")
    Call<JsonObject> getWeatherPrediction(@Query("adm4") String regionCode);

    @GET("DataMKG/TEWS/autogempa.json")
    Call<JsonObject> getAutoGempa();
}
