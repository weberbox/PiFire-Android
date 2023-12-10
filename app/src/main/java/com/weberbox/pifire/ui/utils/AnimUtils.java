package com.weberbox.pifire.ui.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;

import androidx.core.view.ViewCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.weberbox.pifire.constants.Constants;

public class AnimUtils {

    public static void rotateFabBackwards(FloatingActionButton fab) {
        ViewCompat.animate(fab)
                .rotation(180)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(5.0F))
                .start();
    }

    public static void rotateFabForwards(FloatingActionButton fab) {
        ViewCompat.animate(fab)
                .rotation(0)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(5.0F))
                .start();
    }

    public static void fadeViewInvisible(View view, int duration, int direction) {
        switch (direction) {
            case Constants.FADE_IN -> {
                fadeView(view, 0.0f, 1.0f, duration);
                view.setVisibility(View.VISIBLE);
            }
            case Constants.FADE_OUT -> {
                fadeView(view, 1.0f, 0.0f, duration);
                view.setVisibility(View.INVISIBLE);
            }
        }
    }

    public static void fadeView(View view, float start, float end, int duration) {
        AlphaAnimation anim = new AlphaAnimation(start, end);
        anim.setDuration(duration);
        view.startAnimation(anim);
    }

    public static void fadeAnimation(View view, int duration, int direction) {
        switch (direction) {
            case Constants.FADE_IN -> fadeInAnimation(view, duration);
            case Constants.FADE_OUT -> fadeOutAnimation(view, duration);
        }
    }

    public static void fadeInAnimation(View view, int duration) {
        if (view.getVisibility() == View.GONE || view.getVisibility() == View.INVISIBLE) {
            Animation fadeIn = new AlphaAnimation(0f, 1f);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(duration);
            AnimationSet animation = new AnimationSet(false);
            animation.addAnimation(fadeIn);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    view.setVisibility(View.VISIBLE);
                }
            });
            view.startAnimation(animation);
        }
    }

    public static void fadeOutAnimation(View view, int duration) {
        if (view.getVisibility() == View.VISIBLE) {
            Animation fadeOut = new AlphaAnimation(1f, 0f);
            fadeOut.setInterpolator(new DecelerateInterpolator());
            fadeOut.setDuration(duration);
            AnimationSet animation = new AnimationSet(false);
            animation.addAnimation(fadeOut);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    view.setVisibility(View.INVISIBLE);
                }
            });
            view.startAnimation(animation);
        }
    }

    @SuppressWarnings("unused")
    public static void slideDownFromTop(ViewGroup view, int duration) {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.25f);
        view.setLayoutAnimation(controller);
    }

    @SuppressWarnings("unused")
    public static void slideUpFromBottom(ViewGroup view, int duration) {
        AnimationSet set = new AnimationSet(true);
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(duration);
        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.25f);
        view.setLayoutAnimation(controller);
    }

    public static void slideOpen(final View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = 1;
            view.setLayoutParams(layoutParams);

            view.measure(View.MeasureSpec.makeMeasureSpec(
                    Resources.getSystem().getDisplayMetrics().widthPixels,
                    View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            final int height = view.getMeasuredHeight();
            ValueAnimator valueAnimator = ObjectAnimator.ofInt(1, height);
            valueAnimator.addUpdateListener(animation -> {
                int value = (int) animation.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams1 = view.getLayoutParams();
                if (height > value) {
                    layoutParams1.height = value;
                } else {
                    layoutParams1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
                view.setLayoutParams(layoutParams1);
            });
            valueAnimator.start();
        }
    }

    public static void slideClosed(final View view) {
        if (view != null) {
            view.post(() -> {
                final int height = view.getHeight();
                ValueAnimator valueAnimator = ObjectAnimator.ofInt(height, 1);
                valueAnimator.addUpdateListener(animation -> {
                    int value = (int) animation.getAnimatedValue();
                    if (value > 1) {
                        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                        layoutParams.height = value;
                        view.setLayoutParams(layoutParams);
                    } else {
                        view.setVisibility(View.GONE);
                    }
                });
                valueAnimator.start();
            });
        }
    }
}
