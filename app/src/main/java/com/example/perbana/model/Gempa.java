package com.example.perbana.model;

import com.google.gson.annotations.SerializedName;

public class Gempa {
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

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getJam() {
        return jam;
    }

    public void setJam(String jam) {
        this.jam = jam;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
    }

    public String getLintang() {
        return lintang;
    }

    public void setLintang(String lintang) {
        this.lintang = lintang;
    }

    public String getBujur() {
        return bujur;
    }

    public void setBujur(String bujur) {
        this.bujur = bujur;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }

    public String getKedalaman() {
        return kedalaman;
    }

    public void setKedalaman(String kedalaman) {
        this.kedalaman = kedalaman;
    }

    public String getWilayah() {
        return wilayah;
    }

    public void setWilayah(String wilayah) {
        this.wilayah = wilayah;
    }

    public String getPotensi() {
        return potensi;
    }

    public void setPotensi(String potensi) {
        this.potensi = potensi;
    }

    public String getDirasakan() {
        return dirasakan;
    }

    public void setDirasakan(String dirasakan) {
        this.dirasakan = dirasakan;
    }

    public String getShakemap() {
        return shakemap;
    }

    public void setShakemap(String shakemap) {
        this.shakemap = shakemap;
    }

    @Override
    public String toString() {
        return "Gempa{" +
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
