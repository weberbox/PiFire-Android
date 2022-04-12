package com.weberbox.pifire.utils;

import android.annotation.SuppressLint;
import android.text.format.DateFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TimeUtils {

    private static final String FORMAT_HOURS = "%2d h %2d min";
    private static final String FORMAT_MINUTES = "%2d min";

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

    public static Long getTimeInMillis(String hours, String minutes) {
        return TimeUnit.HOURS.toMillis(Long.parseLong(hours)) +
                TimeUnit.MINUTES.toMillis(Long.parseLong(minutes));
    }

    public static Integer getHoursMillis(long milliseconds) {
        return (int) TimeUnit.MILLISECONDS.toHours(milliseconds);
    }

    public static Integer getMinutesMillis(long milliseconds) {
        return Math.toIntExact(TimeUnit.MILLISECONDS.toMinutes(milliseconds) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliseconds)));
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

    public static String parseRecipeTime(long milliseconds) {
        if (TimeUnit.MILLISECONDS.toHours(milliseconds) >= 1) {
            return String.format(Locale.getDefault(), FORMAT_HOURS,
                    TimeUnit.MILLISECONDS.toHours(milliseconds),
                    TimeUnit.MILLISECONDS.toMinutes(milliseconds) - TimeUnit.HOURS.toMinutes(
                            TimeUnit.MILLISECONDS.toHours(milliseconds)));
        } else {
            return String.format(Locale.getDefault(), FORMAT_MINUTES,
                    TimeUnit.MILLISECONDS.toMinutes(milliseconds));
        }
    }
}
