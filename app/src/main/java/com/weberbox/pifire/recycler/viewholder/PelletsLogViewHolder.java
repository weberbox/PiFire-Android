package com.weberbox.pifire.recycler.viewholder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;
import com.weberbox.pifire.recycler.viewmodel.PelletLogViewModel;

public class PelletsLogViewHolder extends RecyclerView.ViewHolder {

    private final TextView mPelletDate;
    private final TextView mPelletName;
    private final TextView mPelletRating;
    private String mPelletID;

    public PelletsLogViewHolder(final View itemView, PelletsCallbackInterface callback) {
        super(itemView);
        RelativeLayout pelletItem = itemView.findViewById(R.id.pellets_items_holder);
        mPelletDate = itemView.findViewById(R.id.pellets_log_date);
        mPelletName = itemView.findViewById(R.id.pellets_log_name);
        mPelletRating = itemView.findViewById(R.id.pellets_log_rating);

        pelletItem.setOnLongClickListener(v -> {
            callback.onLogLongClick(mPelletID, getAbsoluteAdapterPosition());
            return true;
        });

    }

    public void bindData(final PelletLogViewModel viewModel) {
        mPelletID = viewModel.getPelletID();
        mPelletDate.setText(viewModel.getPelletDate());
        mPelletName.setText(viewModel.getPelletName());
        mPelletRating.setText(viewModel.getPelletRating());
    }
}