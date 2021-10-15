package com.weberbox.pifire.ui.utils;

import android.view.Gravity;

import androidx.transition.ChangeBounds;
import androidx.transition.Slide;
import androidx.transition.TransitionSet;

public class TextTransition extends TransitionSet {

    private void BeginTransition() {
        setOrdering(TransitionSet.ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).addTransition(new Slide(Gravity.START));
    }

    public TextTransition() {
        BeginTransition();
    }
}

