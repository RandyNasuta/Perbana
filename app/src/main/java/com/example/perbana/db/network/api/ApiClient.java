package com.example.perbana.db.network.api;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String TAG = "ApiClient";
    private static final String BASE_URL1 = "https://data.bmkg.go.id/";
    private static final String BASE_URL2 = "https://api.bmkg.go.id/publik/";
    private static Retrofit retrofit1 = null;
    private static Retrofit retrofit2 = null;

    private static OkHttpClient getHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        return client;
    }

    public static ApiService getClient(int baseUrl) {
        switch (baseUrl) {
            case 1:
                if (retrofit1 == null) {
                    retrofit1 = new Retrofit.Builder()
                            .baseUrl(BASE_URL1)
                            .client(getHttpClient())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
                return retrofit1.create(ApiService.class);
            case 2:
                if (retrofit2 == null) {
                    retrofit2 = new Retrofit.Builder()
                            .baseUrl(BASE_URL2)
                            .client(getHttpClient())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
                return retrofit2.create(ApiService.class);
            default:
                Log.e(TAG, "getClient: Tidak ada kode base yang sesuai");
                return null;
        }
    }
}
