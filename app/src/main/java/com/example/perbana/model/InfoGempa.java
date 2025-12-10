package com.example.perbana.model;

import com.google.gson.annotations.SerializedName;

public class InfoGempa {
    @SerializedName("gempa")
    private Gempa gempa;

    public Gempa getGempa() {
        return gempa;
    }

    public void setGempa(Gempa gempa) {
        this.gempa = gempa;
    }

    @Override
    public String toString() {
        return "InfoGempa{" +
                "gempa=" + gempa +
                '}';
    }
}
