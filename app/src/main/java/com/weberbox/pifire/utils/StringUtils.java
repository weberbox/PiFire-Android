package com.weberbox.pifire.utils;

import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringUtils {

    public static @NotNull String capFirstLetter(String str) {
        return str.isEmpty() ? "" : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String cleanStrings(String[] strings, String delimiter) {
        return Stream.of(strings).filter(s -> s != null && !s.isEmpty())
                .collect(Collectors.joining(delimiter));
    }

    public static String formatTemp(double temp, boolean fahrenheit) {
        if (fahrenheit) {
            return String.format(Locale.US, "%02d %s", (int) temp, "\u00B0");
        } else {
            return String.format(Locale.US, "%s %s", temp, "\u00B0");
        }
    }

    public static String formatPercentage(Integer percent) {
        return String.format(Locale.US, "%s %s", percent, "\u0025");
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

    public static int getRatingText(Integer rating) {
        switch (rating) {
            case 1:
                return R.string.item_rating_1;
            case 2:
                return R.string.item_rating_2;
            case 3:
                return R.string.item_rating_3;
            case 4:
                return R.string.item_rating_4;
            case 5:
                return R.string.item_rating_5;
            default:
                return R.string.item_rating_error;
        }
    }

    public static Integer getRatingInt(String rating) {
        switch (rating) {
            case Constants.ITEM_RATING_1:
                return 1;
            case Constants.ITEM_RATING_2:
                return 2;
            case Constants.ITEM_RATING_3:
                return 3;
            case Constants.ITEM_RATING_4:
                return 4;
            case Constants.ITEM_RATING_5:
                return 5;
        }
        return 1;
    }

    public static int getDifficultyText(Integer difficulty) {
        switch (difficulty) {
            case Constants.RECIPE_DIF_BEGIN:
                return R.string.recipes_level_beginner;
            case Constants.RECIPE_DIF_EASY:
                return R.string.recipes_level_easy;
            case Constants.RECIPE_DIF_MOD:
                return R.string.recipes_level_mod;
            case Constants.RECIPE_DIF_HARD:
                return R.string.recipes_level_hard;
            case Constants.RECIPE_DIF_V_HARD:
                return R.string.recipes_level_v_hard;
            default:
                return R.string.item_rating_error;
        }
    }
}
