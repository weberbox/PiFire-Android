package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemPickerTextBinding;
import com.weberbox.pifire.utils.StringUtils;

import java.util.List;

public class RecipeDiffAdapter extends RecyclerView.Adapter<RecipeDiffAdapter.ViewHolder> {

    private final List<Integer> list;

    public RecipeDiffAdapter(final List<Integer> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public RecipeDiffAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                              final int viewType) {
        return new ViewHolder(ItemPickerTextBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.binding.pickerItemTextView.setText(StringUtils.getDifficultyText(
                list.get(position)));
        holder.binding.pickerItemTextView.setTag(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ItemPickerTextBinding binding;

        public ViewHolder(ItemPickerTextBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
