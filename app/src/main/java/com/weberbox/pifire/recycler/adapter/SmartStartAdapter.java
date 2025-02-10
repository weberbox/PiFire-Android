package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemSmartStartBinding;
import com.weberbox.pifire.interfaces.SmartStartCallback;
import com.weberbox.pifire.model.local.SmartStartModel;

import java.util.List;

public class SmartStartAdapter extends RecyclerView.Adapter<SmartStartAdapter.ViewHolder> {

    private final List<SmartStartModel> list;
    private final SmartStartCallback callback;
    private final String units;

    public SmartStartAdapter(final List<SmartStartModel> list, String units,
                             SmartStartCallback callback) {
        this.list = list;
        this.units = units;
        this.callback = callback;
    }

    @NonNull
    @Override
    public SmartStartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemSmartStartBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item.setOnClickListener(view ->
                callback.onSmartStartEdit(position));
        holder.bindData(list, position, units);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addNewSmartStartItem(Integer temp, Integer startUp, Integer augerOn,
                                     Integer pMode) {
        Integer lastStartUp = list.get(list.size() - 1).getStartUp();
        Integer lastAugerOn = list.get(list.size() - 1).getAugerOn();
        Integer lastPMode = list.get(list.size() - 1).getPMode();
        list.remove(list.size() - 1);
        list.add(list.size(), new SmartStartModel(temp, lastStartUp, lastAugerOn, lastPMode));
        list.add(list.size(), new SmartStartModel(temp + 1, startUp, augerOn, pMode));
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeSmartStartItem(int position) {
        list.remove(position);
        Integer lastTemp = list.get(list.size() - 2).getTemp();
        Integer lastStartUp = list.get(list.size() - 1).getStartUp();
        Integer lastAugerOn = list.get(list.size() - 1).getAugerOn();
        Integer lastPMode = list.get(list.size() - 1).getPMode();
        list.set(position - 1, new SmartStartModel(lastTemp + 1, lastStartUp, lastAugerOn,
                lastPMode));
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateSmartStartItem(int position, Integer temp, Integer startUp, Integer augerOn,
                                     Integer pMode) {
        list.set(position, new SmartStartModel(temp, startUp, augerOn, pMode));
        notifyDataSetChanged();
    }

    public List<SmartStartModel> getSmartStartItems() {
        return list.isEmpty() ? null : list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView range;
        private final TextView startUp;
        private final TextView augerOn;
        private final TextView pMode;
        private final LinearLayout item;

        public ViewHolder(ItemSmartStartBinding binding) {
            super(binding.getRoot());
            range = binding.smartStartRange;
            startUp = binding.smartStartStartup;
            augerOn = binding.smartStartAugerOn;
            pMode = binding.smartStartPMode;
            item = binding.itemSmartStartContainer;
        }

        public void bindData(final List<SmartStartModel> list, int position, String units) {
            int rangeSize = list.size();
            if (rangeSize > 0) {
                String temp;
                if (position == 0) {
                    temp = "< " + list.get(position).getTemp() + " " + units;
                } else if (position == rangeSize - 1) {
                    temp = "> " + (list.get(position).getTemp() - 1) + " " + units;
                } else {
                    temp = list.get(position - 1).getTemp() + "-" +
                            list.get(position).getTemp() + " " + units;
                }

                String start = list.get(position).getStartUp() + "s";
                String auger = list.get(position).getAugerOn() + "s";

                range.setText(temp);
                startUp.setText(start);
                augerOn.setText(auger);
                pMode.setText(String.valueOf(list.get(position).getPMode()));

            }
        }
    }
}

