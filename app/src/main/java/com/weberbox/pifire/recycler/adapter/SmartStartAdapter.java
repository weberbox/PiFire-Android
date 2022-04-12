package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemSmartStartBinding;
import com.weberbox.pifire.model.local.SmartStartModel;

import java.util.List;

public class SmartStartAdapter extends RecyclerView.Adapter<SmartStartAdapter.ViewHolder> {

    private final List<SmartStartModel> list;

    public SmartStartAdapter(final List<SmartStartModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public SmartStartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemSmartStartBinding.inflate(LayoutInflater.from(
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

        private final TextView range;
        private final TextView startUp;
        private final TextView augerOn;
        private final TextView pMode;

        public ViewHolder(ItemSmartStartBinding binding) {
            super(binding.getRoot());
            range = binding.smartStartRange;
            startUp = binding.smartStartStartup;
            augerOn = binding.smartStartAugerOn;
            pMode = binding.smartStartPMode;
        }

        public void bindData(final SmartStartModel model) {
            String start = model.getStartUp() + "s";
            String auger = model.getAugerOn() + "s";
            range.setText(model.getRange());
            startUp.setText(start);
            augerOn.setText(auger);
            pMode.setText(String.valueOf(model.getPMode()));
        }
    }
}
