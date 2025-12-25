package com.example.perbana.db.model;

import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("t")
    private String temperatur;
    @SerializedName("weather_desc")
    private String weatherDesc;
    @SerializedName("image")
    private String image;
    @SerializedName("local_datetime")
    private String localDateTime;

    public Weather(String temperatur, String weatherDesc, String image, String localDateTime) {
        this.temperatur = temperatur;
        this.weatherDesc = weatherDesc;
        this.image = image;
        this.localDateTime = localDateTime;
    }

    public String getTemperatur() {
        return temperatur;
    }

    public void setTemperatur(String temperatur) {
        this.temperatur = temperatur;
    }

    public String getWeatherDesc() {
        return weatherDesc;
    }

    public void setWeatherDesc(String weatherDesc) {
        this.weatherDesc = weatherDesc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(String localDateTime) {
        this.localDateTime = localDateTime;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "temperatur='" + temperatur + '\'' +
                ", weatherDesc='" + weatherDesc + '\'' +
                ", image='" + image + '\'' +
                ", localDateTime='" + localDateTime + '\'' +
                '}';
    }
}
