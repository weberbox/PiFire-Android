package com.weberbox.pifire.ui.utils;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;

import com.weberbox.pifire.R;

public class AnimUtils {

    public static void shakeOfflineBanner(Activity activity) {
        if (activity != null) {
            activity.findViewById(R.id.offline_banner).setVisibility(View.VISIBLE);
            shakeView(activity.findViewById(R.id.offline_banner), 20, 15);
        }
    }

    public static void shakeView(View view, int duration, int offset) {
        Animation anim = new TranslateAnimation(-offset,offset,0,0);
        anim.setDuration(duration);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(5);
        view.startAnimation(anim);
    }

    public static void fadeView(View view, float start, float end, int duration) {
        AlphaAnimation anim = new AlphaAnimation(start, end);
        anim.setDuration(duration);
        view.startAnimation(anim);
    }

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

    public static void slideDown(final View view) {
        if (view != null) {
            view.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = 1;
            view.setLayoutParams(layoutParams);

            view.measure(View.MeasureSpec.makeMeasureSpec(Resources.getSystem().getDisplayMetrics().widthPixels,
                    View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(0,
                            View.MeasureSpec.UNSPECIFIED));

            final int height = view.getMeasuredHeight();
            ValueAnimator valueAnimator = ObjectAnimator.ofInt(1, height);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (int) animation.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                    if (height > value) {
                        layoutParams.height = value;
                    } else {
                        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                    view.setLayoutParams(layoutParams);
                }
            });
            valueAnimator.start();
        }
    }

    public static void slideUp(final View view) {
        if (view != null) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    final int height = view.getHeight();
                    ValueAnimator valueAnimator = ObjectAnimator.ofInt(height, 1);
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (int) animation.getAnimatedValue();
                            if (value > 1) {
                                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                                layoutParams.height = value;
                                view.setLayoutParams(layoutParams);
                            } else {
                                view.setVisibility(View.GONE);
                            }
                        }
                    });
                    valueAnimator.start();
                }
            });
        }
    }
}
