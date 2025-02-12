package com.weberbox.pifire.ui.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import com.weberbox.pifire.R;

@SuppressWarnings("unused")
public final class ViewUtils {

    public static boolean isTablet(Context context){
        return context.getResources().getBoolean(R.bool.is_tablet);
    }

    public static boolean isLandscape(Context context) {
        return context.getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE;
    }

    public static float convertPixelsToSp(float px) {
        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();
        float scaledDensity;
        if (Build.VERSION.SDK_INT >= 34)
            scaledDensity = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm);
        else
            scaledDensity = dm.scaledDensity;
        return px / scaledDensity;
    }

    public static int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) ((dp * density) + 0.5f);
    }
}
