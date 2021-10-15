package com.weberbox.pifire.recycler.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.recycler.viewmodel.TimePickerViewModel;

public class TimerPickerViewHolder extends RecyclerView.ViewHolder {

    private final TextView simpleTextView;

    public TimerPickerViewHolder(final View itemView) {
        super(itemView);
        simpleTextView = itemView.findViewById(R.id.timer_item_text_view);
    }

    public void bindData(final TimePickerViewModel viewModel) {
        simpleTextView.setText(viewModel.getTimeText());
    }
}