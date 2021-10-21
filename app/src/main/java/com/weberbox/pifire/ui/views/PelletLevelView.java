package com.weberbox.pifire.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.weberbox.pifire.R;

import timber.log.Timber;

@SuppressWarnings("unused")
public class PelletLevelView extends View {

    private int mPrimaryColor;
    private int mSecondaryColor;
    private int mLevel;
    private int mDotRadius;

    Paint mPaint = new Paint();

    public PelletLevelView(Context context) {
        super (context);
    }

    public PelletLevelView(Context context, AttributeSet attrs) {
        super (context, attrs);
        init(context, attrs);

    }

    public PelletLevelView(Context context, AttributeSet attrs, int style) {
        super (context, attrs, style);
        init(context, attrs);
    }

    public void setPrimaryColor(int color) {
        mPrimaryColor = color;
        reDrawView();
    }

    public void setSecondaryColor(int color) {
        mSecondaryColor = color;
        reDrawView();
    }

    public void setLevel(int level) {
        if (level >= 0 && level != mLevel) {
            mLevel = level;
            reDrawView();
        }
    }

    private void reDrawView() {
        invalidate();
        requestLayout();
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PelletLevelView);

            mPrimaryColor = a.getColor(R.styleable.PelletLevelView_primary_color, Color.WHITE);
            mSecondaryColor = a.getColor(R.styleable.PelletLevelView_secondary_color,
                    Color.TRANSPARENT);
            mDotRadius = dpToPx(a.getInt(R.styleable.PelletLevelView_dot_radius, 4));
            setLevel(a.getInt (R.styleable.PelletLevelView_initial_value, 0));

            a.recycle();
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int numDots = 10;
        int numValue = Math.max(mLevel / 10, 1);

        int width = getWidth();
        int height = getHeight();
        float vwf = (float) width;
        float vhf = (float) height;

        int rColor;

        rColor = mPrimaryColor;

        mPaint.setColor(rColor);

        for (int i = 0; i < numDots; i++){
            for (int j = 0; j < numDots; j++) {
                if (j < numValue) {
                    rColor = mPrimaryColor;
                } else {
                    rColor = mSecondaryColor;
                }

                mPaint.setColor(rColor);

                canvas.drawCircle((i + 0.5f) / numDots * vwf,
                        (j + 0.5f) / numDots * vhf, mDotRadius, mPaint);
            }
        }
    }

    private int measureDimension(int desiredSize, int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = desiredSize;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }

        if (result < desiredSize) {
            Timber.d("The view is too small, the content might get cut");
        }
        return result;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Timber.d("onMeasure w %s", MeasureSpec.toString(widthMeasureSpec));
        Timber.d("onMeasure h %s", MeasureSpec.toString(heightMeasureSpec));

        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec),
                measureDimension(desiredHeight, heightMeasureSpec));
    }

}
