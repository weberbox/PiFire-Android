package com.weberbox.pifire.ui.utils;

import androidx.transition.ChangeBounds;
import androidx.transition.Fade;
import androidx.transition.TransitionSet;

public class FadeTransition extends TransitionSet {
    private void BannerTransition() {
        setOrdering(TransitionSet.ORDERING_TOGETHER);

        addTransition(new ChangeBounds()).addTransition(new Fade());
    }

    public FadeTransition() {
        BannerTransition();
    }
}

