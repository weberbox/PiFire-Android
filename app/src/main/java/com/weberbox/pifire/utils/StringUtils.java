package com.weberbox.pifire.utils;

import android.text.format.DateFormat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class StringUtils {

    public static String formatTemp(Integer temp) {
        return temp + "\u00B0";
    }

    public static String formatTemp(double temp, boolean fahrenheit) {
        if (fahrenheit) {
            return (int) temp + "\u00B0";
        } else {
            return temp + "\u00B0";
        }
    }

    public static String formatPercentage(Integer percent) {
        return percent + "\u0025";
    }

    public static String formatDate(String dateInMilliseconds, String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static int getRatingText(Integer rating) {
        switch (rating) {
            case 1:
                return R.string.pellets_rating_1;
            case 2:
                return R.string.pellets_rating_2;
            case 3:
                return R.string.pellets_rating_3;
            case 4:
                return R.string.pellets_rating_4;
            case 5:
                return R.string.pellets_rating_5;
            default:
                return R.string.pellets_rating_error;
        }
    }

    public static Integer getRatingInt(String rating) {
        switch (rating) {
            case Constants.PELLET_RATING_1:
                return 1;
            case Constants.PELLET_RATING_2:
                return 2;
            case Constants.PELLET_RATING_3:
                return 3;
            case Constants.PELLET_RATING_4:
                return 4;
            case Constants.PELLET_RATING_5:
                return 5;
        }
        return 1;
    }

    public static String streamToString(InputStream stream) throws IOException {
        int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(stream, StandardCharsets.UTF_8);
        for (int numRead; (numRead = in.read(buffer, 0, buffer.length)) > 0; ) {
            out.append(buffer, 0, numRead);
        }
        return out.toString();
    }
}
