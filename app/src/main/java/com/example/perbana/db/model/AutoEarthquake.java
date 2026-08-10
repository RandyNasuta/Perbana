package com.example.perbana.db.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class AutoEarthquake implements Parcelable {
    @SerializedName("Tanggal")
    private String tanggal;

    @SerializedName("Jam")
    private String jam;

    @SerializedName("DateTime")
    private String dateTime;

    @SerializedName("Coordinates")
    private String coordinates;

    @SerializedName("Lintang")
    private String lintang;

    @SerializedName("Bujur")
    private String bujur;

    @SerializedName("Magnitude")
    private String magnitude;

    @SerializedName("Kedalaman")
    private String kedalaman;

    @SerializedName("Wilayah")
    private String wilayah;

    @SerializedName("Potensi")
    private String potensi;

    @SerializedName("Dirasakan")
    private String dirasakan;

    @SerializedName("Shakemap")
    private String shakemap;

    public AutoEarthquake() {
    }

    public AutoEarthquake(String tanggal, String jam, String dateTime, String coordinates, String lintang, String bujur, String magnitude, String kedalaman, String wilayah, String potensi, String dirasakan, String shakemap) {
        this.tanggal = tanggal;
        this.jam = jam;
        this.dateTime = dateTime;
        this.coordinates = coordinates;
        this.lintang = lintang;
        this.bujur = bujur;
        this.magnitude = magnitude;
        this.kedalaman = kedalaman;
        this.wilayah = wilayah;
        this.potensi = potensi;
        this.dirasakan = dirasakan;
        this.shakemap = shakemap;
    }

    public static final Creator<AutoEarthquake> CREATOR = new Creator<AutoEarthquake>() {

        @Override
        public AutoEarthquake createFromParcel(Parcel parcel) {
            return new AutoEarthquake(parcel);
        }

        @Override
        public AutoEarthquake[] newArray(int i) {
            return new AutoEarthquake[i];
        }
    };

    public AutoEarthquake(Parcel parcel) {
        tanggal = parcel.readString();
        jam = parcel.readString();
        dateTime = parcel.readString();
        coordinates = parcel.readString();
        lintang = parcel.readString();
        bujur = parcel.readString();
        magnitude = parcel.readString();
        kedalaman = parcel.readString();
        wilayah = parcel.readString();
        potensi = parcel.readString();
        dirasakan = parcel.readString();
        shakemap = parcel.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(tanggal);
        parcel.writeString(jam);
        parcel.writeString(dateTime);
        parcel.writeString(coordinates);
        parcel.writeString(lintang);
        parcel.writeString(bujur);
        parcel.writeString(magnitude);
        parcel.writeString(kedalaman);
        parcel.writeString(wilayah);
        parcel.writeString(potensi);
        parcel.writeString(dirasakan);
        parcel.writeString(shakemap);
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getJam() {
        return jam;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getLintang() {
        return lintang;
    }

    public String getBujur() {
        return bujur;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public String getKedalaman() {
        return kedalaman;
    }

    public String getWilayah() {
        return wilayah;
    }

    public String getPotensi() {
        return potensi;
    }

    public String getDirasakan() {
        return dirasakan;
    }

    public String getShakemap() {
        return shakemap;
    }

    @Override
    public String toString() {
        return "AutoEarthquake{" +
                "tanggal='" + tanggal + '\'' +
                ", jam='" + jam + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", coordinates='" + coordinates + '\'' +
                ", lintang='" + lintang + '\'' +
                ", bujur='" + bujur + '\'' +
                ", magnitude='" + magnitude + '\'' +
                ", kedalaman='" + kedalaman + '\'' +
                ", wilayah='" + wilayah + '\'' +
                ", potensi='" + potensi + '\'' +
                ", dirasakan='" + dirasakan + '\'' +
                ", shakemap='" + shakemap + '\'' +
                '}';
    }
}
