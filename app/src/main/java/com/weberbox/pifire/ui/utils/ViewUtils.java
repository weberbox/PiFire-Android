package com.weberbox.pifire.ui.utils;

import android.content.Context;
import android.content.res.Resources;

import com.weberbox.pifire.R;

public final class ViewUtils {

    public static boolean isTablet(Context context){
        return context.getResources().getBoolean(R.bool.is_tablet);
    }

    public static float convertPixelsToSp(float px, Context context) {
        return px / context.getResources().getDisplayMetrics().scaledDensity;
    }

    public static int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) ((dp * density) + 0.5f);
    }
}
