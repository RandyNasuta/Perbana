package com.example.perbana.db.network.api;

import com.example.perbana.db.model.Cap;
import com.example.perbana.db.model.Rss;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface ApiService {
    @GET("prakiraan-cuaca")
    Call<JsonObject> getWeatherPrediction(@Query("adm4") String regionCode);

    @GET("DataMKG/TEWS/autogempa.json")
    Call<JsonObject> getAutoGempa();

    @GET("DataMKG/TEWS/gempaterkini.json")
    Call<JsonObject> getEarthquakeList();

    @GET("DataMKG/TEWS/gempadirasakan.json")
    Call<JsonObject> getEarthquakeFeltList();

    @GET("alerts/nowcast/id")
    Call<Rss> getWeatherWarningList();

    @GET
    Call<Cap> getWeatherWarningDetail(@Url String link);
}
