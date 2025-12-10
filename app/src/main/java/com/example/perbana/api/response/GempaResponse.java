package com.example.perbana.api.response;

import com.example.perbana.model.InfoGempa;
import com.google.gson.annotations.SerializedName;

public class GempaResponse {
    @SerializedName("InfoGempa")
    private InfoGempa infogempa;

    public InfoGempa getInfogempa() {
        return infogempa;
    }

    public void setInfogempa(InfoGempa infogempa) {
        this.infogempa = infogempa;
    }

    @Override
    public String toString() {
        return "GempaResponse{" +
                "infogempa=" + infogempa +
                '}';
    }
}
