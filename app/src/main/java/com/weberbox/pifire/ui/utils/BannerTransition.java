package com.weberbox.pifire.ui.utils;

import android.view.Gravity;

import androidx.transition.ChangeBounds;
import androidx.transition.Slide;
import androidx.transition.TransitionSet;

public class BannerTransition extends TransitionSet {
    private void BannerTransition() {
        setOrdering(TransitionSet.ORDERING_TOGETHER);

        addTransition(new ChangeBounds()).addTransition(new Slide(Gravity.TOP));
    }

    public BannerTransition() {
        BannerTransition();
    }
}
