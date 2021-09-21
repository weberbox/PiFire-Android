package com.weberbox.pifire.recycler.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.recycler.viewmodel.PelletLogViewModel;

public class PelletsLogViewHolder extends RecyclerView.ViewHolder {

    private TextView mPelletDate;
    private TextView mPelletName;
    private TextView mPelletRating;

    public PelletsLogViewHolder(final View itemView) {
        super(itemView);
        mPelletDate = itemView.findViewById(R.id.pellets_log_date);
        mPelletName = itemView.findViewById(R.id.pellets_log_name);
        mPelletRating = itemView.findViewById(R.id.pellets_log_rating);
    }

    public void bindData(final PelletLogViewModel viewModel) {
        mPelletDate.setText(viewModel.getPelletDate());
        mPelletName.setText(viewModel.getPelletName());
        mPelletRating.setText(viewModel.getPelletRating());
    }
}