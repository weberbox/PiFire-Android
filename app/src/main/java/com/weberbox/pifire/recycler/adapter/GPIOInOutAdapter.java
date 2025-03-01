package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemGpioInOutBinding;
import com.weberbox.pifire.record.GPIOInOutRecord;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

public class GPIOInOutAdapter extends RecyclerView.Adapter<GPIOInOutAdapter.ViewHolder> {

    private final HashMap<String, String> list;
    private ArrayList<GPIOInOutRecord> outputs;

    public GPIOInOutAdapter() {
        this.list = new HashMap<>();
        this.outputs = new ArrayList<>();
    }

    @NonNull
    @Override
    public GPIOInOutAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                              final int viewType) {
        return new ViewHolder(ItemGpioInOutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bindData(outputs.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressWarnings("NotifyDataSetChanged")
    public void setInOutList(@NotNull HashMap<String, String> inOutList, boolean dcFan) {
        list.clear();
        outputs.clear();
        list.putAll(inOutList);
        outputs = new ArrayList<>(inOutList.size());
        list.forEach((key, value) -> {
            if (key.contains("dc_fan") || key.contains("pwm")) {
                if (dcFan) {
                    outputs.add(new GPIOInOutRecord(key, value));
                }
            } else {
                outputs.add(new GPIOInOutRecord(key, value));
            }
        });
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView pin;

        public ViewHolder(ItemGpioInOutBinding binding) {
            super(binding.getRoot());
            name = binding.gpioName;
            pin = binding.gpioPin;
        }

        public void bindData(final GPIOInOutRecord item) {
            name.setText(item.name());
            pin.setText(item.pin());
        }
    }
}