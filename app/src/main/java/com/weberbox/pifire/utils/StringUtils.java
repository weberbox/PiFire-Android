package com.weberbox.pifire.utils;

import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class StringUtils {

    public static @NotNull String capFirstLetter(String str) {
        return str.isEmpty() ? "" : str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static String formatTemp(double temp, boolean fahrenheit) {
        if (fahrenheit) {
            return String.format(Locale.US, "%02d %s", (int) temp, "°");
        } else {
            return String.format(Locale.US, "%s %s", temp, "°");
        }
    }

    public static String formatPercentage(Integer percent) {
        return String.format(Locale.US, "%s %s", percent, "%");
    }

    public static int getRatingText(Integer rating) {
        return switch (rating) {
            case 1 -> R.string.item_rating_1;
            case 2 -> R.string.item_rating_2;
            case 3 -> R.string.item_rating_3;
            case 4 -> R.string.item_rating_4;
            case 5 -> R.string.item_rating_5;
            default -> R.string.item_rating_error;
        };
    }

    public static Integer getRatingInt(String rating) {
        return switch (rating) {
            case Constants.ITEM_RATING_2 -> 2;
            case Constants.ITEM_RATING_3 -> 3;
            case Constants.ITEM_RATING_4 -> 4;
            case Constants.ITEM_RATING_5 -> 5;
            default -> 1;
        };
    }
}
