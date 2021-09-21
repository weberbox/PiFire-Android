package com.weberbox.pifire.ui.utils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.Transition;
import androidx.transition.TransitionValues;

public class Rotate extends Transition {

    private static final String PROPNAME_ROTATION = "android:rotate:rotation";
    private static final String[] sTransitionProperties = {PROPNAME_ROTATION};

    @Nullable
    @Override
    public String[] getTransitionProperties() {
        return sTransitionProperties;
    }

    @Override
    public void captureStartValues(@NonNull TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_ROTATION, transitionValues.view.getRotation());
    }

    @Override
    public void captureEndValues(@NonNull TransitionValues transitionValues) {
        transitionValues.values.put(PROPNAME_ROTATION, transitionValues.view.getRotation());
    }

    @Nullable
    @Override
    public Animator createAnimator(@NonNull ViewGroup sceneRoot, @Nullable TransitionValues startValues,
                                   @Nullable TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return null;
        }
        final View view = endValues.view;
        float startRotation = (Float) startValues.values.get(PROPNAME_ROTATION);
        float endRotation = (Float) endValues.values.get(PROPNAME_ROTATION);
        if (startRotation != endRotation) {
            view.setRotation(startRotation);
            return ObjectAnimator.ofFloat(view, View.ROTATION,
                    startRotation, endRotation);
        }
        return null;
    }
}