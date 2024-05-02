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
}
