package com.weberbox.pifire.ui.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.interfaces.OnSwipeActiveListener;
import com.weberbox.pifire.interfaces.OnStateChangeListener;
import com.weberbox.pifire.interfaces.OnSwipeTouchListener;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.ui.utils.TouchUtils;

public class SwipeButton extends RelativeLayout {

    private static final int ENABLED = 0;
    private static final int DISABLED = 1;

    private ImageView mSwipeButtonInner;
    private float mInitialX;
    private boolean mActive;
    private TextView mCenterText;
    private ViewGroup mBackground;

    private Drawable mDisabledDrawable;
    private Drawable mEnabledDrawable;

    private OnStateChangeListener mOnStateChangeListener;
    private OnSwipeTouchListener mOnSwipeTouchListener;
    private OnSwipeActiveListener mOnSwipeActiveListener;

    private int mCollapsedWidth;

    private LinearLayout mLayer;
    private boolean mTrailEnabled = false;
    private boolean mHasActivationState;

    private float mButtonLeftPadding;
    private float mButtonTopPadding;
    private float mButtonRightPadding;
    private float mButtonBottomPadding;

    public SwipeButton(Context context) {
        super(context);

        init(context, null, -1, -1);
    }

    public SwipeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs, -1, -1);
    }

    public SwipeButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context, attrs, defStyleAttr, -1);
    }

    @SuppressWarnings("unused")
    public SwipeButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        init(context, attrs, defStyleAttr, defStyleRes);
    }

    public boolean isActive() {
        return mActive;
    }

    public void setText(String text) {
        mCenterText.setText(text);
    }

    public void setBackground(Drawable drawable) {
        mBackground.setBackground(drawable);
    }

    @SuppressWarnings("unused")
    public void setSlidingButtonBackground(Drawable drawable) {
        mBackground.setBackground(drawable);
    }

    @SuppressWarnings("unused")
    public void setDisabledDrawable(Drawable drawable) {
        mDisabledDrawable = drawable;

        if (!mActive) {
            mSwipeButtonInner.setImageDrawable(drawable);
        }
    }

    @SuppressWarnings("unused")
    public void setButtonBackground(Drawable buttonBackground) {
        if (buttonBackground != null) {
            mSwipeButtonInner.setBackground(buttonBackground);
        }
    }

    @SuppressWarnings("unused")
    public void setEnabledDrawable(Drawable drawable) {
        mEnabledDrawable = drawable;

        if (mActive) {
            mSwipeButtonInner.setImageDrawable(drawable);
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener onStateChangeListener) {
        this.mOnStateChangeListener = onStateChangeListener;
    }

    @SuppressWarnings("unused")
    public void setOnActiveListener(OnSwipeActiveListener onSwipeActiveListener) {
        this.mOnSwipeActiveListener = onSwipeActiveListener;
    }

    public void setOnSwipeTouchListener(OnSwipeTouchListener onSwipeTouchListener) {
        this.mOnSwipeTouchListener = onSwipeTouchListener;
    }

    @SuppressWarnings("unused")
    public void setInnerTextPadding(int left, int top, int right, int bottom) {
        mCenterText.setPadding(left, top, right, bottom);
    }

    @SuppressWarnings("unused")
    public void setSwipeButtonPadding(int left, int top, int right, int bottom) {
        mSwipeButtonInner.setPadding(left, top, right, bottom);
    }

    @SuppressWarnings("unused")
    public void setHasActivationState(boolean hasActivationState) {
        this.mHasActivationState = hasActivationState;
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        mHasActivationState = true;

        mBackground = new RelativeLayout(context);

        LayoutParams layoutParamsView = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParamsView.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        addView(mBackground, layoutParamsView);

        final TextView centerText = new TextView(context);
        this.mCenterText = centerText;
        centerText.setGravity(Gravity.CENTER);

        LayoutParams layoutParams = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        mBackground.addView(centerText, layoutParams);

        this.mSwipeButtonInner = new ImageView(context);

        if (attrs != null && defStyleAttr == -1 && defStyleRes == -1) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SwipeButton,
                    defStyleAttr, defStyleRes);

            mCollapsedWidth = (int) typedArray.getDimension(R.styleable.SwipeButton_button_image_width,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            int collapsedHeight = (int) typedArray.getDimension(R.styleable.SwipeButton_button_image_height,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            mTrailEnabled = typedArray.getBoolean(R.styleable.SwipeButton_button_trail_enabled,
                    false);
            Drawable trailingDrawable = typedArray.getDrawable(R.styleable.SwipeButton_button_trail_drawable);

            Drawable backgroundDrawable = typedArray.getDrawable(R.styleable.SwipeButton_inner_text_background);

            if (backgroundDrawable != null) {
                mBackground.setBackground(backgroundDrawable);
            } else {
                mBackground.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_swipe_shape_rounded));
            }

            if (mTrailEnabled) {
                mLayer = new LinearLayout(context);

                if (trailingDrawable != null) {
                    mLayer.setBackground(trailingDrawable);
                } else {
                    mLayer.setBackground(typedArray.getDrawable(R.styleable.SwipeButton_button_background));
                }

                mLayer.setGravity(Gravity.START);
                mLayer.setVisibility(View.GONE);
                mBackground.addView(mLayer, layoutParamsView);
            }

            centerText.setText(typedArray.getText(R.styleable.SwipeButton_inner_text));
            centerText.setTextColor(typedArray.getColor(R.styleable.SwipeButton_inner_text_color,
                    Color.WHITE));

            float textSize = ViewUtils.convertPixelsToSp(
                    typedArray.getDimension(R.styleable.SwipeButton_inner_text_size, 0), context);

            if (textSize != 0) {
                centerText.setTextSize(textSize);
            } else {
                centerText.setTextSize(12);
            }

            mDisabledDrawable = typedArray.getDrawable(R.styleable.SwipeButton_button_image_disabled);
            mEnabledDrawable = typedArray.getDrawable(R.styleable.SwipeButton_button_image_enabled);
            float innerTextLeftPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_left_padding, 0);
            float innerTextTopPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_top_padding, 0);
            float innerTextRightPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_right_padding, 0);
            float innerTextBottomPadding = typedArray.getDimension(
                    R.styleable.SwipeButton_inner_text_bottom_padding, 0);

            int initialState = typedArray.getInt(R.styleable.SwipeButton_initial_state, DISABLED);

            if (initialState == ENABLED) {
                LayoutParams layoutParamsButton = new LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

                layoutParamsButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                mSwipeButtonInner.setImageDrawable(mEnabledDrawable);

                addView(mSwipeButtonInner, layoutParamsButton);

                mActive = true;
            } else {
                LayoutParams layoutParamsButton = new LayoutParams(mCollapsedWidth, collapsedHeight);

                layoutParamsButton.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
                layoutParamsButton.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

                mSwipeButtonInner.setImageDrawable(mDisabledDrawable);

                addView(mSwipeButtonInner, layoutParamsButton);

                mActive = false;
            }

            centerText.setPadding((int) innerTextLeftPadding,
                    (int) innerTextTopPadding,
                    (int) innerTextRightPadding,
                    (int) innerTextBottomPadding);

            Drawable buttonBackground = typedArray.getDrawable(R.styleable.SwipeButton_button_background);

            if (buttonBackground != null) {
                mSwipeButtonInner.setBackground(buttonBackground);
            } else {
                mSwipeButtonInner.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_swipe_shape_button));
            }

            mButtonLeftPadding = typedArray.getDimension(R.styleable.SwipeButton_button_left_padding, 0);
            mButtonTopPadding = typedArray.getDimension(R.styleable.SwipeButton_button_top_padding, 0);
            mButtonRightPadding = typedArray.getDimension(R.styleable.SwipeButton_button_right_padding, 0);
            mButtonBottomPadding = typedArray.getDimension(R.styleable.SwipeButton_button_bottom_padding, 0);

            mSwipeButtonInner.setPadding((int) mButtonLeftPadding,
                    (int) mButtonTopPadding,
                    (int) mButtonRightPadding,
                    (int) mButtonBottomPadding);

            mHasActivationState = typedArray.getBoolean(R.styleable.SwipeButton_has_activate_state, true);

            typedArray.recycle();
        }

        setOnTouchListener(getButtonTouchListener());
    }

    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @SuppressLint("ClickableViewAccessibility")
    private OnTouchListener getButtonTouchListener() {
        return (v, event) -> {
            if (mOnSwipeTouchListener != null) {
                mOnSwipeTouchListener.onSwipeTouch(v, event);
            }
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    return !TouchUtils.isTouchOutsideInitialPosition(event, mSwipeButtonInner);
                case MotionEvent.ACTION_MOVE:
                    if (mInitialX == 0) {
                        mInitialX = mSwipeButtonInner.getX();
                    }

                    if (event.getX() > mSwipeButtonInner.getWidth() / 2 &&
                            event.getX() + mSwipeButtonInner.getWidth() / 2 < getWidth()) {
                        mSwipeButtonInner.setX(event.getX() - mSwipeButtonInner.getWidth() / 2);
                        mCenterText.setAlpha(1 - 1.3f * (mSwipeButtonInner.getX() +
                                mSwipeButtonInner.getWidth()) / getWidth());
                        setTrailingEffect();
                    }

                    if (event.getX() + mSwipeButtonInner.getWidth() / 2 > getWidth() &&
                            mSwipeButtonInner.getX() + mSwipeButtonInner.getWidth() / 2 < getWidth()) {
                        mSwipeButtonInner.setX(getWidth() - mSwipeButtonInner.getWidth());
                    }

                    if (event.getX() < mSwipeButtonInner.getWidth() / 2) {
                        mSwipeButtonInner.setX(0);
                    }

                    return true;
                case MotionEvent.ACTION_UP:
                    if (mActive) {
                        collapseButton();
                    } else {
                        if (mSwipeButtonInner.getX() + mSwipeButtonInner.getWidth() > getWidth() * 0.9) {
                            if (mHasActivationState) {
                                expandButton();
                            } else if (mOnSwipeActiveListener != null) {
                                mOnSwipeActiveListener.onActive();
                                moveButtonBack();
                            }
                        } else {
                            moveButtonBack();
                        }
                    }

                    return true;
            }

            return false;
        };
    }

    private void expandButton() {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(mSwipeButtonInner.getX(), 0);
        positionAnimator.addUpdateListener(animation -> {
            float x = (Float) positionAnimator.getAnimatedValue();
            mSwipeButtonInner.setX(x);
        });


        final ValueAnimator widthAnimator = ValueAnimator.ofInt(
                mSwipeButtonInner.getWidth(),
                getWidth());

        widthAnimator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams params = mSwipeButtonInner.getLayoutParams();
            params.width = (Integer) widthAnimator.getAnimatedValue();
            mSwipeButtonInner.setLayoutParams(params);
        });


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                mActive = true;
                mSwipeButtonInner.setImageDrawable(mEnabledDrawable);

                if (mOnStateChangeListener != null) {
                    mOnStateChangeListener.onStateChange(mActive);
                }

                if (mOnSwipeActiveListener != null) {
                    mOnSwipeActiveListener.onActive();
                }
            }
        });

        animatorSet.playTogether(positionAnimator, widthAnimator);
        animatorSet.start();
    }

    private void moveButtonBack() {
        final ValueAnimator positionAnimator =
                ValueAnimator.ofFloat(mSwipeButtonInner.getX(), 0);
        positionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        positionAnimator.addUpdateListener(animation -> {
            float x = (Float) positionAnimator.getAnimatedValue();
            mSwipeButtonInner.setX(x);
            setTrailingEffect();
        });

        positionAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mLayer !=null) {
                    mLayer.setVisibility(View.GONE);
                }
            }
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                mCenterText, "alpha", 1);

        positionAnimator.setDuration(200);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(objectAnimator, positionAnimator);
        animatorSet.start();
    }

    private void collapseButton() {
        int finalWidth;

        if (mCollapsedWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            finalWidth = mSwipeButtonInner.getHeight();
        } else {
            finalWidth = mCollapsedWidth;
        }

        final ValueAnimator widthAnimator = ValueAnimator.ofInt(mSwipeButtonInner.getWidth(), finalWidth);

        widthAnimator.addUpdateListener(animation -> {
            ViewGroup.LayoutParams params = mSwipeButtonInner.getLayoutParams();
            params.width = (Integer) widthAnimator.getAnimatedValue();
            mSwipeButtonInner.setLayoutParams(params);
            setTrailingEffect();
        });

        widthAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                mActive = false;
                mSwipeButtonInner.setImageDrawable(mDisabledDrawable);
                if (mOnStateChangeListener != null) {
                    mOnStateChangeListener.onStateChange(mActive);
                }
                if (mLayer !=null) {
                    mLayer.setVisibility(View.GONE);
                }
            }
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(
                mCenterText, "alpha", 1);

        AnimatorSet animatorSet = new AnimatorSet();

        animatorSet.playTogether(objectAnimator, widthAnimator);
        animatorSet.start();
    }

    @SuppressWarnings("unused")
    public void setEnabledStateNotAnimated() {
        mSwipeButtonInner.setX(0);

        ViewGroup.LayoutParams params = mSwipeButtonInner.getLayoutParams();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mSwipeButtonInner.setLayoutParams(params);

        mSwipeButtonInner.setImageDrawable(mEnabledDrawable);
        mActive = true;
        mCenterText.setAlpha(0);
    }

    @SuppressWarnings("unused")
    public void setDisabledStateNotAnimated() {
        ViewGroup.LayoutParams params = mSwipeButtonInner.getLayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;

        mCollapsedWidth = ViewGroup.LayoutParams.WRAP_CONTENT;

        mSwipeButtonInner.setImageDrawable(mDisabledDrawable);
        mActive = false;
        mCenterText.setAlpha(1);

        mSwipeButtonInner.setPadding((int) mButtonLeftPadding,
                (int) mButtonTopPadding,
                (int) mButtonRightPadding,
                (int) mButtonBottomPadding);

        mSwipeButtonInner.setLayoutParams(params);
    }

    private void setTrailingEffect() {
        if (mTrailEnabled) {
            mLayer.setVisibility(View.VISIBLE);
            mLayer.setLayoutParams(new RelativeLayout.LayoutParams(
                    (int) (mSwipeButtonInner.getX() + mSwipeButtonInner.getWidth() / 3),
                    mCenterText.getHeight()));
        }
    }

    @SuppressWarnings("unused")
    public void setTrailBackground(@NonNull Drawable trailingDrawable) {
        if (mTrailEnabled) {
            mLayer.setBackground(trailingDrawable);
        }
    }

    public void toggleState() {
        if (isActive()) {
            collapseButton();
        } else {
            expandButton();
        }
    }

    @SuppressWarnings("unused")
    public void setCenterTextColor(Context context, int color) {
        mCenterText.setTextColor(ContextCompat.getColor(context, color));
    }

    public void setCenterTextStyle(int style) {
        mCenterText.setTextAppearance(style);
    }
}
