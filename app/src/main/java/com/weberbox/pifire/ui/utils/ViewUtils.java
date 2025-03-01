package com.weberbox.pifire.ui.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;

import androidx.core.content.ContextCompat;

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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            scaledDensity = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, dm);
        else
            scaledDensity = dm.scaledDensity;
        return px / scaledDensity;
    }

    public static int dpToPx(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) ((dp * density) + 0.5f);
    }

    public static void setViewVisible(View view) {
        if (view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void setViewGone(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.GONE);
        }
    }

    public static void setBackground(Context context, View view, int background) {
        Drawable drawable = ContextCompat.getDrawable(context, background);
        if (!view.getBackground().equals(drawable)) {
            view.setBackground(drawable);
        }
    }

    public static void startAnimation(View view, Animation animation) {
        if (view.getAnimation() == null) {
            view.post(() -> view.startAnimation(animation));
        }
    }

    public static void stopAnimation(View view, Animation animation) {
        if (view.getAnimation() != null) {
            view.post(() -> {
                view.clearAnimation();
                animation.cancel();
                animation.reset();
                view.animate().translationX(0).translationY(0);
            });
        }
    }
}
