package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller;
import com.weberbox.pifire.R;
import com.weberbox.pifire.recycler.viewholder.TempPickerViewHolder;
import com.weberbox.pifire.recycler.viewmodel.TempPickerViewModel;

import java.util.List;

public class TempPickerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements
        RecyclerViewFastScroller.OnPopupTextUpdate{

    private final List<TempPickerViewModel> mModel;

    public TempPickerAdapter(final List<TempPickerViewModel> viewModel) {
        mModel = viewModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new TempPickerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((TempPickerViewHolder) holder).bindData(mModel.get(position));
    }

    @Override
    public int getItemCount() {
        return mModel.size();
    }

    @Override
    public int getItemViewType(final int position) {
        return R.layout.item_picker_temp;
    }

    @NonNull
    @Override
    public CharSequence onChange(int position) {
        if (position < mModel.size()) {
            return mModel.get(position).getTempText();
        } else {
            return mModel.get(position - 1).getTempText();
        }
    }
}