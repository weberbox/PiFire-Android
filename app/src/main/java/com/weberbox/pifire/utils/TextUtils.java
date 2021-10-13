package com.weberbox.pifire.utils;

import android.text.Layout;
import android.widget.TextView;

public class TextUtils {

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
