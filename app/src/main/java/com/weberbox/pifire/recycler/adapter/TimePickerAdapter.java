package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.recycler.viewholder.TimerPickerViewHolder;
import com.weberbox.pifire.recycler.viewmodel.TimePickerViewModel;

import java.util.List;

public class TimePickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<TimePickerViewModel> mModel;

    public TimePickerAdapter(final List<TimePickerViewModel> viewModel) {
        mModel = viewModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new TimerPickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((TimerPickerViewHolder) holder).bindData(mModel.get(position));
    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_picker_timer;
    }
}