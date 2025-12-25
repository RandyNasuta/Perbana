package com.example.perbana.db.repository;

import android.content.Context;

import com.example.perbana.db.local.PerbanaPreferences;

public class RegionRepository {
    private Context context;
    private PerbanaPreferences pref;

    public RegionRepository(Context context) {
        if (context != null) {
            this.context = context;
            pref = new PerbanaPreferences(this.context);
        }
    }

    public void setKeyRegionCode(String regionCode) {
        pref.setKeyRegionCode(regionCode);
    }

    public String getKeyRegionCode() {
        return pref.getKeyRegionCode();
    }
}
