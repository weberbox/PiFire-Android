package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemPmodeTableBinding;
import com.weberbox.pifire.model.local.PModeModel;

import java.util.List;


public class PModeViewAdapter extends RecyclerView.Adapter<PModeViewAdapter.ViewHolder> {

    private final List<PModeModel> list;

    public PModeViewAdapter(final List<PModeModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public PModeViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPmodeTableBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView pMode;
        private final TextView augerOn;
        private final TextView augerOff;

        public ViewHolder(ItemPmodeTableBinding binding) {
            super(binding.getRoot());
            pMode = binding.pmodeItemTv;
            augerOn = binding.pmodeItemAon;
            augerOff = binding.pmodeItemAoff;
        }

        public void bindData(final PModeModel model) {
            pMode.setText(model.getPMode());
            augerOn.setText(model.getAugerOn());
            augerOff.setText(model.getAugerOff());
        }
    }
}