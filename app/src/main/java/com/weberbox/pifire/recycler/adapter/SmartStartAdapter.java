package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemSmartStartBinding;
import com.weberbox.pifire.record.SmartStartRecord;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SmartStartAdapter extends RecyclerView.Adapter<SmartStartAdapter.ViewHolder> {

    private final List<SmartStartRecord> list;
    private final SmartStartCallback callback;
    private final String units;

    public SmartStartAdapter(@NotNull final List<SmartStartRecord> list, @NotNull String units,
                             @NotNull SmartStartCallback callback) {
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
        return list.size();
    }

    public void addNewSmartStartItem(@NotNull Integer temp, @NotNull Integer startUp,
                                     @NotNull Integer augerOn, @NotNull Integer pMode) {
        int lastPosition = list.size() - 1;
        Integer lastStartUp = list.get(lastPosition).startUp();
        Integer lastAugerOn = list.get(lastPosition).augerOn();
        Integer lastPMode = list.get(lastPosition).pMode();
        list.set(lastPosition, new SmartStartRecord(temp, lastStartUp, lastAugerOn, lastPMode));
        list.add(list.size(), new SmartStartRecord(temp + 1, startUp, augerOn, pMode));
        notifyItemRangeChanged(lastPosition, 2);
    }

    public void removeSmartStartItem(int position) {
        list.remove(position);
        int lastPosition = list.size() - 1;
        Integer lastTemp = list.get(list.size() - 2).temp();
        Integer lastStartUp = list.get(lastPosition).startUp();
        Integer lastAugerOn = list.get(lastPosition).augerOn();
        Integer lastPMode = list.get(lastPosition).pMode();
        list.set(position - 1, new SmartStartRecord(lastTemp + 1, lastStartUp, lastAugerOn,
                lastPMode));
        notifyItemRemoved(position);
        notifyItemRangeChanged(position - 1, 2);
    }

    public void updateSmartStartItem(int position, @NotNull Integer temp, @NotNull Integer startUp,
                                     @NotNull Integer augerOn, @NotNull Integer pMode) {
        list.set(position, new SmartStartRecord(temp, startUp, augerOn, pMode));
        notifyItemRangeChanged(position, 2);
    }

    public List<SmartStartRecord> getSmartStartItems() {
        return list;
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

        public void bindData(final List<SmartStartRecord> list, int position, String units) {
            int rangeSize = list.size();
            if (rangeSize > 0) {
                String temp;
                if (position == 0) {
                    temp = "< " + list.get(position).temp() + " " + units;
                } else if (position == rangeSize - 1) {
                    temp = "> " + (list.get(position).temp() - 1) + " " + units;
                } else {
                    temp = list.get(position - 1).temp() + "-" +
                            list.get(position).temp() + " " + units;
                }

                String start = list.get(position).startUp() + "s";
                String auger = list.get(position).augerOn() + "s";

                range.setText(temp);
                startUp.setText(start);
                augerOn.setText(auger);
                pMode.setText(String.valueOf(list.get(position).pMode()));

            }
        }
    }

    public interface SmartStartCallback {
        void onSmartStartEdit(int position);
    }
}

