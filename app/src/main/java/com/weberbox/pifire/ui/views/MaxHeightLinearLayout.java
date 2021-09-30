package com.weberbox.pifire.ui.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.weberbox.pifire.R;

public class MaxHeightLinearLayout extends LinearLayout {

    private int mMaxHeight;
    private final int mDefaultHeight = 200;

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
            TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.MaxHeightLinearLayout);
            mMaxHeight = styledAttrs.getDimensionPixelSize(R.styleable.MaxHeightLinearLayout_maxHeight, mDefaultHeight);

            styledAttrs.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(mMaxHeight, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
