package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemPmodeDialogBinding;
import com.weberbox.pifire.record.PModeRecord;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PModeViewAdapter extends RecyclerView.Adapter<PModeViewAdapter.ViewHolder> {

    private final List<PModeRecord> list;

    public PModeViewAdapter(@NotNull final List<PModeRecord> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public PModeViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPmodeDialogBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView pMode;
        private final TextView augerOn;
        private final TextView augerOff;

        public ViewHolder(ItemPmodeDialogBinding binding) {
            super(binding.getRoot());
            pMode = binding.pmodeItemTv;
            augerOn = binding.pmodeItemAon;
            augerOff = binding.pmodeItemAoff;
        }

        public void bindData(final PModeRecord model) {
            pMode.setText(model.pMode());
            augerOn.setText(model.augerOn());
            augerOff.setText(model.augerOff());
        }
    }
}