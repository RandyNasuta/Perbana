package com.example.perbana.model;

import com.google.gson.annotations.SerializedName;

public class GempaResponse {
    @SerializedName("Infogempa")
    private Infogempa infogempa;

    public Infogempa getInfogempa() {
        return infogempa;
    }

    public void setInfogempa(Infogempa infogempa) {
        this.infogempa = infogempa;
    }

    @Override
    public String toString() {
        return "GempaResponse{" +
                "infogempa=" + infogempa +
                '}';
    }
}
