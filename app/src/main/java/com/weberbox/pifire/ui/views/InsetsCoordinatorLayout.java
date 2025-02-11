package com.weberbox.pifire.ui.views;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

// Derived from DavidMarquezF @ https://github.com/material-components/material-components-android/issues/1310#issuecomment-2352509577

public class InsetsCoordinatorLayout extends CoordinatorLayout {

    public InsetsCoordinatorLayout(Context context) {
        super(context);
    }

    public InsetsCoordinatorLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InsetsCoordinatorLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            super.dispatchApplyWindowInsets(insets);
            if (insets.isConsumed()) {
                return insets;
            }
            int count = getChildCount();
            for (int i = 0; i < count; i++) {
                getChildAt(i).dispatchApplyWindowInsets(insets);
            }
        } else {
            return super.dispatchApplyWindowInsets(insets);
        }
        return insets;
    }
}