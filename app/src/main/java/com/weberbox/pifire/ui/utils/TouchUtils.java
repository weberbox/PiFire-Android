package com.weberbox.pifire.ui.utils;

import android.view.MotionEvent;
import android.view.View;

public final class TouchUtils {
    public TouchUtils() {

    }

    public static boolean isTouchOutsideInitialPosition(MotionEvent event, View view) {
        return event.getX() > view.getX() + view.getWidth();
    }
}
