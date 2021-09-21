package com.weberbox.pifire.recycler.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.recycler.viewmodel.PModeViewModel;


public class PModeViewHolder extends RecyclerView.ViewHolder {
    private TextView mPMode;
    private TextView mAugerOn;
    private TextView mAugerOff;

    public PModeViewHolder(View itemView) {
        super(itemView);
        mPMode = itemView.findViewById(R.id.pmode_item_tv);
        mAugerOn = itemView.findViewById(R.id.pmode_item_aon);
        mAugerOff = itemView.findViewById(R.id.pmode_item_aoff);
    }

    public void bindData(final PModeViewModel viewModel) {
        mPMode.setText(viewModel.getPMode());
        mAugerOn.setText(viewModel.getAugerOn());
        mAugerOff.setText(viewModel.getAugerOff());
    }
}
