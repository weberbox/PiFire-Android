package com.weberbox.pifire.interfaces;

import androidx.recyclerview.widget.RecyclerView;

public interface ItemTouchHelperCallback {
    void onRowMoved(int fromPosition, int toPosition);
    void onRowSelected(RecyclerView.ViewHolder myViewHolder);
    void onRowClear(RecyclerView.ViewHolder myViewHolder);
}
