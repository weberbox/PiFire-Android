package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller;
import com.weberbox.pifire.databinding.ItemPickerTempBinding;
import com.weberbox.pifire.model.local.TempPickerModel;

import java.util.List;

public class TempPickerAdapter extends RecyclerView.Adapter<TempPickerAdapter.ViewHolder> implements
        RecyclerViewFastScroller.OnPopupTextUpdate {

    private final List<TempPickerModel> list;

    public TempPickerAdapter(final List<TempPickerModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public TempPickerAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                           final int viewType) {
        return new ViewHolder(ItemPickerTempBinding.inflate(LayoutInflater.from(
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

    @NonNull
    @Override
    public CharSequence onChange(int position) {
        if (position < list.size()) {
            return list.get(position).getTempText();
        } else {
            return list.get(position - 1).getTempText();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tempTextView;
        private final TextView unitTextView;

        public ViewHolder(ItemPickerTempBinding binding) {
            super(binding.getRoot());
            tempTextView = binding.tempItemTextView;
            unitTextView = binding.tempItemUnitTextView;
        }

        public void bindData(final TempPickerModel model) {
            tempTextView.setText(model.getTempText());
            unitTextView.setText(model.getUnitText());
        }
    }
}