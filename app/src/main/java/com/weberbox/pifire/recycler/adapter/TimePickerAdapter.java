package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemPickerTimerBinding;
import com.weberbox.pifire.model.local.TimePickerModel;

import java.util.List;

public class TimePickerAdapter extends RecyclerView.Adapter<TimePickerAdapter.ViewHolder> {

    private final List<TimePickerModel> list;

    public TimePickerAdapter(final List<TimePickerModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public TimePickerAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                      final int viewType) {
        return new ViewHolder(ItemPickerTimerBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView simpleTextView;

        public ViewHolder(ItemPickerTimerBinding binding) {
            super(binding.getRoot());
            simpleTextView = binding.timerItemTextView;
        }

        public void bindData(final TimePickerModel viewModel) {
            simpleTextView.setText(viewModel.getTimeText());
        }
    }
}