package com.example.perbana.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    public static String weatherWarningDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return "-";
        }

        SimpleDateFormat inputFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("EEE, dd MMM yyyy • HH:mm 'WIB'", new Locale("id", "ID"));

        outputFormat.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));

        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e(TAG, "weatherWarningDate: error: " + e.getMessage());
            return dateString;
        }
    }
}
