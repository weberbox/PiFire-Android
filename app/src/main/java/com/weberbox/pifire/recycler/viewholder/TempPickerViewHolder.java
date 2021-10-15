package com.weberbox.pifire.recycler.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.recycler.viewmodel.TempPickerViewModel;

public class TempPickerViewHolder extends RecyclerView.ViewHolder {

    private final TextView tempTextView;
    private final TextView unitTextView;

    public TempPickerViewHolder(final View itemView) {
        super(itemView);
        tempTextView = itemView.findViewById(R.id.temp_item_text_view);
        unitTextView = itemView.findViewById(R.id.temp_item_unit_text_view);
    }

    public void bindData(final TempPickerViewModel viewModel) {
        tempTextView.setText(viewModel.getTempText());
        unitTextView.setText(viewModel.getUnitText());
    }
}
