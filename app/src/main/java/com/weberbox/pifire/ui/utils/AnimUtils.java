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
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
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
            case Constants.FADE_IN:
                fadeView(view, 0.0f, 1.0f, duration);
                view.setVisibility(View.VISIBLE);
                break;
            case Constants.FADE_OUT:
                fadeView(view, 1.0f, 0.0f, duration);
                view.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public static void fadeViewGone(View view, int duration, int direction) {
        switch (direction) {
            case Constants.FADE_IN:
                if (view.getVisibility() == View.GONE) {
                    fadeView(view, 0.0f, 1.0f, duration);
                    view.setVisibility(View.VISIBLE);
                }
                break;
            case Constants.FADE_OUT:
                if (view.getVisibility() == View.VISIBLE) {
                    fadeView(view, 1.0f, 0.0f, duration);
                    view.setVisibility(View.GONE);
                }
                break;
        }
    }

    public static void fadeView(View view, float start, float end, int duration) {
        AlphaAnimation anim = new AlphaAnimation(start, end);
        anim.setDuration(duration);
        view.startAnimation(anim);
    }

    public static void fadeAnimation(View view, int duration, int direction) {
        switch (direction) {
            case Constants.FADE_IN:
                fadeInAnimation(view, duration);
                break;
            case Constants.FADE_OUT:
                fadeOutAnimation(view, duration);
                break;
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

    public static void fabHideAnimation(View view) {
        Animation scaleDown = new ScaleAnimation(1f, 0f, 1f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleDown.setInterpolator(new LinearInterpolator());
        scaleDown.setDuration(150);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(scaleDown);
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

    public static void fabShowAnimation(View view) {
        Animation scaleUp = new ScaleAnimation(0f, 1f, 0f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleUp.setInterpolator(new LinearInterpolator());
        scaleUp.setDuration(150);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(scaleUp);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
            }
        });
        view.startAnimation(animation);
    }

    public static void fabFromBottomAnim(FloatingActionButton fab) {
        AnimationSet animation = new AnimationSet(true);
        Animation alpha = new AlphaAnimation(0f, 1f);
        Animation trans = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        Animation scale = new ScaleAnimation(Animation.RELATIVE_TO_SELF, 0.8f,
                Animation.RELATIVE_TO_SELF, 0.8f, 0.5f, 0.5f);
        scale.setInterpolator(new LinearInterpolator());
        trans.setDuration(300);
        alpha.setDuration(500);
        animation.addAnimation(trans);
        animation.addAnimation(scale);
        animation.addAnimation(alpha);
        animation.setFillAfter(true);
        fab.setVisibility(View.VISIBLE);
        fab.setClickable(true);
        fab.startAnimation(animation);
    }

    public static void fabToBottomAnim(FloatingActionButton fab) {
        AnimationSet animation = new AnimationSet(true);
        Animation alpha = new AlphaAnimation(1f, 0f);
        Animation trans = new TranslateAnimation(Animation.RELATIVE_TO_SELF,0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f);
        Animation scaleUp = new ScaleAnimation(0.8f, 0.8f, 0.8f, 0.8f, 0.5f, 0.5f);
        scaleUp.setInterpolator(new LinearInterpolator());
        trans.setDuration(300);
        alpha.setDuration(100);
        animation.addAnimation(trans);
        animation.addAnimation(scaleUp);
        animation.addAnimation(alpha);
        animation.setFillAfter(false);
        fab.startAnimation(animation);
        fab.setVisibility(View.GONE);
        fab.setClickable(false);
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
