package com.weberbox.pifire.recycler.callback;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;

abstract public class SwipeToDeleteCallback extends ItemTouchHelper.Callback {

    private final Paint mClearPaint;
    private final GradientDrawable mBackground;

    public SwipeToDeleteCallback() {
        mBackground = new GradientDrawable();
        mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder
            viewHolder) {
        return makeMovementFlags(0, ItemTouchHelper.RIGHT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            clearCanvas(c, itemView.getRight() + dX, (float) itemView.getTop(),
                    (float) itemView.getRight(), (float) itemView.getBottom());
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, false);
            return;
        }

        Drawable drawable = ContextCompat.getDrawable(recyclerView.getContext(), R.drawable.ic_delete);

        int mIntrinsicWidth = drawable != null ? drawable.getIntrinsicWidth() : 0;
        int mIntrinsicHeight = drawable != null ? drawable.getIntrinsicHeight() : 0;

        mBackground.setColor(recyclerView.getContext().getColor(R.color.colorRedButton));
        mBackground.setBounds(itemView.getLeft(), itemView.getTop(),
                itemView.getLeft() + (int) dX, itemView.getBottom());
        mBackground.draw(c);

        int deleteIconTop = itemView.getTop() + (itemHeight - mIntrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - mIntrinsicHeight) / 2;
        int deleteIconLeft = itemView.getLeft() + deleteIconMargin;
        int deleteIconRight = itemView.getLeft() + deleteIconMargin + mIntrinsicWidth;
        int deleteIconBottom = deleteIconTop + mIntrinsicHeight;

        if (drawable != null) {
            drawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
            drawable.draw(c);
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

    }

    private void clearCanvas(Canvas c, Float left, Float top, Float right, Float bottom) {
        c.drawRect(left, top, right, bottom, mClearPaint);
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return 0.7f;
    }
}
