package com.weberbox.pifire.ui.utils;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.weberbox.pifire.R;

@SuppressWarnings("unused")
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

    public static void toggleStatusBarColor(Activity activity, int colorFrom, int colorTo) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorTransparent));
        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(),
                ContextCompat.getColor(activity, colorFrom),
                ContextCompat.getColor(activity, colorTo));
        anim.setDuration(405);
        anim.addUpdateListener(animation -> window.setStatusBarColor((int) anim.getAnimatedValue()));
        anim.start();
    }

    public static void toggleStatusBarColor(Activity activity, boolean actionModeVisible) {
        int colorFrom;
        int colorTo;
        int duration;

        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.colorTransparent));
        TypedValue colorPrimaryTypedValue = new TypedValue();
        TypedValue actionModeBackgroundTypedValue = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorPrimary, colorPrimaryTypedValue, true);
        activity.getTheme().resolveAttribute(R.attr.actionModeBackground,
                actionModeBackgroundTypedValue, true);

        if (actionModeVisible) {
            colorFrom = colorPrimaryTypedValue.data;
            colorTo = actionModeBackgroundTypedValue.data;
            duration = 350;
        } else {
            colorFrom = actionModeBackgroundTypedValue.data;
            colorTo = colorPrimaryTypedValue.data;
            duration = 300;
        }


        ValueAnimator anim = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        anim.setDuration(duration);
        anim.addUpdateListener(animator ->
                window.setStatusBarColor((int) animator.getAnimatedValue()));
        anim.start();
    }
}
