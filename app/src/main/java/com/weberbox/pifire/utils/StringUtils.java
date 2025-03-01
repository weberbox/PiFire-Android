package com.weberbox.pifire.utils;

import android.text.Layout;
import android.text.SpannableString;
import android.text.style.BulletSpan;
import android.widget.TextView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;

import org.jetbrains.annotations.NotNull;

import java.util.List;
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
            return String.format(Locale.US, "%02d %s", (int) temp, "°");
        } else {
            return String.format(Locale.US, "%s %s", temp, "°");
        }
    }

    public static String formatPercentage(Integer percent) {
        return String.format(Locale.US, "%s %s", percent, "%");
    }

    public static CharSequence toBulletedList(List<String> list) {
        SpannableString spannableString = new SpannableString(String.join("\n", list));
        int acc = 0;
        for (int index = 0; index < list.size(); index++) {
            String span = list.get(index);
            int end = acc + span.length() + (index != list.size() - 1 ? 1 : 0);
            spannableString.setSpan(new BulletSpan(16), acc, end, 0);
            acc = end;
        }
        return spannableString;
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

    public static boolean hasEllipsis(TextView textView) {
        if (textView == null) {
            return false;
        }
        boolean hasLongContent = false;
        Layout descriptionLayout = (textView).getLayout();
        if (descriptionLayout != null) {
            int lines = descriptionLayout.getLineCount();
            if (lines > 0) {
                hasLongContent = descriptionLayout.getEllipsisCount(lines - 1) > 0;
            }
        }
        return hasLongContent;
    }
}
