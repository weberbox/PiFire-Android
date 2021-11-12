package com.weberbox.pifire.recycler.manager;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.interfaces.OnScrollStopListener;

@SuppressWarnings("unused")
public class PickerLayoutManager extends LinearLayoutManager {

    private float mScaleDownBy = 0.66f;
    private float mScaleDownDistance = 0.9f;
    private boolean mChangeAlpha = true;

    private OnScrollStopListener mOnScrollStopListener;

    public PickerLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        int orientation = getOrientation();
        if (orientation == HORIZONTAL) {
            scaleDownViewHorizontal();
        } else {
            scaleDownViewVertical();
        }
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int orientation = getOrientation();
        if (orientation == HORIZONTAL) {
            int scrolled = super.scrollHorizontallyBy(dx, recycler, state);
            scaleDownViewHorizontal();
            return scrolled;
        } else return 0;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int orientation = getOrientation();
        if (orientation == VERTICAL) {
            int scrolled = super.scrollVerticallyBy(dy, recycler, state);
            scaleDownViewVertical();
            return scrolled;
        } else return 0;
    }

    @SuppressWarnings("ConstantConditions")
    private void scaleDownViewHorizontal() {
        float mid = getWidth() / 2.0f;
        float unitScaleDownDist = mScaleDownDistance * mid;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float childMid = (getDecoratedLeft(child) + getDecoratedRight(child)) / 2.0f;
            float scale = 1.0f + (-1 * mScaleDownBy) * (Math.min(unitScaleDownDist, Math.abs(mid - childMid))) / unitScaleDownDist;
            child.setScaleX(scale);
            child.setScaleY(scale);
            if (mChangeAlpha) {
                child.setAlpha(scale);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void scaleDownViewVertical() {
        float mid = getHeight() / 2.0f;
        float unitScaleDownDist = mScaleDownDistance * mid;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            float childMid = (getDecoratedTop(child) + getDecoratedBottom(child)) / 2.0f;
            float scale = 1.0f + (-1 * mScaleDownBy) * (Math.min(unitScaleDownDist, Math.abs(mid - childMid))) / unitScaleDownDist;
            child.setScaleX(scale);
            child.setScaleY(scale);
            if (mChangeAlpha) {
                child.setAlpha(scale);
            }
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (state == 0) {
            if (mOnScrollStopListener != null) {
                int selected = 0;
                float lastHeight = 0f;
                for (int i = 0; i < getChildCount(); i++) {
                    if (lastHeight < getChildAt(i).getScaleY()) {
                        lastHeight = getChildAt(i).getScaleY();
                        selected = i;
                    }
                }
                mOnScrollStopListener.selectedView(getChildAt(selected));
            }
        }
    }

    public float getScaleDownBy() {
        return mScaleDownBy;
    }

    public void setScaleDownBy(float mScaleDownBy) {
        this.mScaleDownBy = mScaleDownBy;
    }

    public float getScaleDownDistance() {
        return mScaleDownDistance;
    }

    public void setScaleDownDistance(float mScaleDownDistance) {
        this.mScaleDownDistance = mScaleDownDistance;
    }

    public boolean isChangeAlpha() {
        return mChangeAlpha;
    }

    public void setChangeAlpha(boolean changeAlpha) {
        this.mChangeAlpha = changeAlpha;
    }

    public void setOnScrollStopListener(OnScrollStopListener mOnScrollStopListener) {
        this.mOnScrollStopListener = mOnScrollStopListener;
    }
}