package com.weberbox.pifire.ui.utils;

import android.app.Activity;
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
}
