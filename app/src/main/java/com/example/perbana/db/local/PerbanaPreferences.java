package com.example.perbana.db.local;

import android.content.Context;
import android.content.SharedPreferences;

public class PerbanaPreferences {
    private static final String PREFERENCES_NAME = "PerbanaPreferences";
    private static final int PRIVATE_MODE = 0;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    private static final String KEY_REGION_CODE = "";

    public PerbanaPreferences(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setKeyRegionCode(String regionCode) {
        editor.putString(KEY_REGION_CODE, regionCode);
        editor.apply();
    }

    public String getKeyRegionCode() {
        return pref.getString(KEY_REGION_CODE, "");
    }
}
