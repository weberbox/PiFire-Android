package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.qtalk.recyclerviewfastscroller.RecyclerViewFastScroller;
import com.weberbox.pifire.databinding.ItemPickerTempBinding;
import com.weberbox.pifire.record.TempPickerRecord;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class TempPickerAdapter extends RecyclerView.Adapter<TempPickerAdapter.ViewHolder> implements
        RecyclerViewFastScroller.OnPopupTextUpdate {

    private final List<TempPickerRecord> list;

    public TempPickerAdapter(@NotNull final List<TempPickerRecord> list) {
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
        return list.size();
    }

    @NonNull
    @Override
    public CharSequence onChange(int position) {
        if (position < list.size()) {
            return list.get(position).tempText();
        } else {
            return list.get(position - 1).tempText();
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

        public void bindData(final TempPickerRecord model) {
            tempTextView.setText(model.tempText());
            unitTextView.setText(model.unitText());
        }
    }
}