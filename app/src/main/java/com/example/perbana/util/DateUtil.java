package com.example.perbana.util;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    private static final String TAG = "DateUtil";

    public static String getCurrentDate(String format) {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat sdf = new SimpleDateFormat(format, new Locale("id", "ID"));

        return sdf.format(calendar.getTime());
    }

    public static String parseDate(String formatInput, String formatOutput, String dateData) {
        SimpleDateFormat inputFormat = new SimpleDateFormat(formatInput, new Locale("id", "ID"));
        SimpleDateFormat outputFormat = new SimpleDateFormat(formatOutput, new Locale("id", "ID"));
        try {
            Date date = inputFormat.parse(dateData);
            return outputFormat.format(date != null ? date : "");
        } catch (Exception e) {
            Log.e(TAG, "onResponse: error date parse: " + e.getMessage());
        }
        return null;
    }
}
