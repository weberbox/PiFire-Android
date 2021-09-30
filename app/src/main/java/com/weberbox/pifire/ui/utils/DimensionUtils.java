package com.weberbox.pifire.ui.utils;

import android.content.Context;

public final class DimensionUtils {

    public DimensionUtils() {
    }

    public static float convertPixelsToSp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }
}
