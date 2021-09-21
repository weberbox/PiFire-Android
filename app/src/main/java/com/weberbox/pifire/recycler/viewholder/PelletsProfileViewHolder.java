package com.weberbox.pifire.recycler.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.model.PelletProfileModel;

public class PelletsProfileViewHolder extends RecyclerView.ViewHolder {

    private TextView mPelletProfile;
    private TextView mPelletProfileId;

    public PelletsProfileViewHolder(final View itemView) {
        super(itemView);
        mPelletProfile = itemView.findViewById(R.id.profile_item_text_view);
        mPelletProfileId = itemView.findViewById(R.id.profile_item_id);
    }

    public void bindData(final PelletProfileModel viewModel) {
        String item = viewModel.getBrand() + " " + viewModel.getWood();
        mPelletProfile.setText(item);
        mPelletProfileId.setText(viewModel.getId());
    }
}
