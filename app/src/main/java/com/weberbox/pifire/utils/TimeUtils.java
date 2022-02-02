package com.weberbox.pifire.utils;

import android.text.format.DateFormat;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

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

    public static Float getTimeInSeconds(String hours, String minutes) {
        long seconds = TimeUnit.HOURS.toSeconds(Long.parseLong(hours));
        long fullSecs = seconds + TimeUnit.MINUTES.toSeconds(Long.parseLong(minutes));
        return (float) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + fullSecs;
    }
}
