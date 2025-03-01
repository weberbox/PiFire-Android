package com.weberbox.pifire.utils;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static String getFormattedDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    @SuppressLint("SimpleDateFormat")
    public static String parseDate(String date, String inputFormat, String outputFormat)
            throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(inputFormat);
        Date newDate = sdf.parse(date);
        sdf = new SimpleDateFormat(outputFormat);
        if (newDate != null) {
            return sdf.format(newDate);
        } else {
            return "";
        }
    }

    @SuppressLint("DefaultLocale")
    public static String formatSeconds(Integer totalSeconds) {
        if (totalSeconds != null) {
            int hours = totalSeconds / 3600;
            int minutes = (totalSeconds % 3600) / 60;
            int seconds = totalSeconds % 60;

            if (hours > 0) {
                return String.format("%02d:%02d:%02d", hours, minutes, seconds);
            } else {
                return String.format("%02d:%02d", minutes, seconds);
            }
        }
        return null;
    }

    @SuppressLint("DefaultLocale")
    public static String formatMinutes(Integer totalMinutes) {
        if (totalMinutes != null && totalMinutes > 0) {
            int hours = totalMinutes / 60;
            int minutes = totalMinutes % 60;

            return String.format("%d:%02d", hours, minutes);
        }
        return "--:--";
    }
}
