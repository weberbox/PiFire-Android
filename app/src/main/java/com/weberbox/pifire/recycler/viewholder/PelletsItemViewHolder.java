package com.weberbox.pifire.recycler.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;
import com.weberbox.pifire.recycler.viewmodel.PelletItemViewModel;

public class PelletsItemViewHolder extends RecyclerView.ViewHolder {

    private final TextView mPelletItem;
    private final TextView mPelletId;

    public PelletsItemViewHolder(final View itemView, PelletsCallbackInterface callback) {
        super(itemView);
        mPelletItem = itemView.findViewById(R.id.pellets_item);
        mPelletId = itemView.findViewById(R.id.pellets_item_id);
        ImageView pelletIcon = itemView.findViewById(R.id.pellets_item_delete);

        pelletIcon.setOnClickListener(view -> callback.onItemDelete(mPelletId.getText().toString(),
                mPelletItem.getText().toString(), getAbsoluteAdapterPosition()));
    }

    public void bindData(final PelletItemViewModel viewModel) {
        mPelletItem.setText(viewModel.getPelletItem());
        mPelletId.setText(viewModel.getPelletItemId());
    }
}
