package com.weberbox.pifire.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static String getTimeFormatted(float value, String format) {
        LocalTime localTime = LocalTime.ofSecondOfDay((long) value);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(localTime);
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTime(String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(System.currentTimeMillis());
    }
}
