package com.example.perbana.db.model;

public class RegionCode {
    private String code;
    private String region;

    public RegionCode(String code, String region) {
        this.code = code;
        this.region = region;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return region;
    }
}
