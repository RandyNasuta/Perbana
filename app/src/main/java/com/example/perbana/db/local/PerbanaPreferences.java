package com.example.perbana.db.local;

import android.content.Context;
import android.content.SharedPreferences;

public class PerbanaPreferences {
    private static final String PREFERENCES_NAME = "PerbanaPreferences";

    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;

    private static final String KEY_REGION_CODE = "key_region_code";
    private static final String KEY_LATITUDE = "key_latitude";
    private static final String KEY_LONGITUDE = "key_longitude";
    private static final String KEY_LAST_EARTHQUAKE_DATE = "key_last_gempa_date";

    public PerbanaPreferences(Context context) {
        pref = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void setKeyRegionCode(String regionCode) {
        editor.putString(KEY_REGION_CODE, regionCode);
        editor.apply();
    }

    public void setLatitude(double latitude) {
        editor.putFloat(KEY_LATITUDE, (float) latitude);
        editor.apply();
    }

    public void setLongitude(double longitude) {
        editor.putFloat(KEY_LONGITUDE, (float) longitude);
        editor.apply();
    }

    public void setLastEarthquakeDate(String date) {
        editor.putString(KEY_LAST_EARTHQUAKE_DATE, date);
        editor.apply();
    }

    public String getKeyRegionCode() {
        return pref.getString(KEY_REGION_CODE, "");
    }

    public double getLatitude() {
        return pref.getFloat(KEY_LATITUDE, 0);
    }

    public double getLongitude() {
        return pref.getFloat(KEY_LONGITUDE, 0);
    }

    public String getLastEarthquakeDate() {
        return pref.getString(KEY_LAST_EARTHQUAKE_DATE, "");
    }
}
