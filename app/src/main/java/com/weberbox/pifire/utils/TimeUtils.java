package com.weberbox.pifire.utils;

import android.text.format.DateFormat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeUtils {

    public static String getTimeFormatted(float value, String format) {
        LocalTime localTime = LocalTime.ofSecondOfDay((long) value);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(localTime);
    }

    public static String getFormattedDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static String getFormattedDate(Long dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, dateInMilliseconds).toString();
    }
}
