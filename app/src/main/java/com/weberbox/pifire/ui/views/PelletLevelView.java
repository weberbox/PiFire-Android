package com.weberbox.pifire.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.weberbox.pifire.R;
import com.weberbox.pifire.ui.utils.ViewUtils;

import timber.log.Timber;

@SuppressWarnings("unused")
public class PelletLevelView extends View {

    private int primaryColor;
    private int secondaryColor;
    private int level;
    private int dotRadius;

    Paint paint = new Paint();

    public PelletLevelView(Context context, AttributeSet attrs) {
        super (context, attrs);
        init(context, attrs);

    }

    public PelletLevelView(Context context, AttributeSet attrs, int style) {
        super (context, attrs, style);
        init(context, attrs);
    }

    public void setPrimaryColor(int color) {
        primaryColor = color;
        reDrawView();
    }

    public void setSecondaryColor(int color) {
        secondaryColor = color;
        reDrawView();
    }

    public void setLevel(int level) {
        if (level >= 0 && level != this.level) {
            this.level = level;
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

            primaryColor = a.getColor(R.styleable.PelletLevelView_primary_color, Color.WHITE);
            secondaryColor = a.getColor(R.styleable.PelletLevelView_secondary_color,
                    Color.TRANSPARENT);
            dotRadius = ViewUtils.dpToPx(a.getInt(R.styleable.PelletLevelView_dot_radius, 4));
            setLevel(a.getInt (R.styleable.PelletLevelView_initial_value, 0));

            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int numDots = 10;
        int numValue = Math.max(level / 10, 1);

        int width = getWidth();
        int height = getHeight();
        float vwf = (float) width;
        float vhf = (float) height;

        int rColor;

        rColor = primaryColor;

        paint.setColor(rColor);

        for (int i = 0; i < numDots; i++){
            for (int j = 0; j < numDots; j++) {
                if (j < numValue) {
                    rColor = primaryColor;
                } else {
                    rColor = secondaryColor;
                }

                paint.setColor(rColor);

                canvas.drawCircle((i + 0.5f) / numDots * vwf,
                        (j + 0.5f) / numDots * vhf, dotRadius, paint);
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
        int desiredWidth = getSuggestedMinimumWidth() + getPaddingLeft() + getPaddingRight();
        int desiredHeight = getSuggestedMinimumHeight() + getPaddingTop() + getPaddingBottom();

        setMeasuredDimension(measureDimension(desiredWidth, widthMeasureSpec),
                measureDimension(desiredHeight, heightMeasureSpec));
    }

}
