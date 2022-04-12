package com.weberbox.pifire.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.weberbox.pifire.R;

public class MaxHeightLinearLayout extends LinearLayout {

    private int maxHeight;

    @SuppressWarnings("FieldCanBeLocal")
    private final int defaultHeight = 200;

    public MaxHeightLinearLayout(Context context) {
        super(context);
    }

    public MaxHeightLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    public MaxHeightLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (!isInEditMode()) {
            init(context, attrs);
        }
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            @SuppressLint("CustomViewStyleable")
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
                    R.styleable.MaxHeightLinearLayout);
            maxHeight = styledAttrs.getDimensionPixelSize(
                    R.styleable.MaxHeightLinearLayout_max_height, defaultHeight);

            styledAttrs.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
