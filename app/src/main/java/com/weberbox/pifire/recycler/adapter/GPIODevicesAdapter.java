package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemGpioDeviceBinding;
import com.weberbox.pifire.record.GPIODevicesRecord;
import com.weberbox.pifire.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class GPIODevicesAdapter extends RecyclerView.Adapter<GPIODevicesAdapter.ViewHolder> {

    private final HashMap<String, HashMap<String, String>> list;
    private ArrayList<GPIODevicesRecord> devices;

    public GPIODevicesAdapter() {
        this.list = new HashMap<>();
        this.devices = new ArrayList<>();
    }

    @NonNull
    @Override
    public GPIODevicesAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                          final int viewType) {
        return new ViewHolder(ItemGpioDeviceBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bindData(devices.get(position));
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    @SuppressWarnings("NotifyDataSetChanged")
    public void setDevicesList(HashMap<String, HashMap<String, String>> devicesList) {
        list.clear();
        devices.clear();
        list.putAll(devicesList);
        devices = new ArrayList<>(devicesList.size());
        AtomicInteger cycle = new AtomicInteger();
        list.forEach((key, value) -> {
            cycle.getAndSet(1);
            value.forEach((function, pin) -> {
                if (cycle.intValue() == 1) {
                    cycle.getAndIncrement();
                    devices.add(new GPIODevicesRecord(key, function, pin));
                } else {
                    devices.add(new GPIODevicesRecord("", function, pin));
                }
                cycle.getAndSet(0);
            });
        });
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final TextView function;
        private final TextView pin;

        public ViewHolder(ItemGpioDeviceBinding binding) {
            super(binding.getRoot());
            name = binding.gpioDeviceName;
            function = binding.gpioDeviceFunction;
            pin = binding.gpioDevicePin;
        }

        public void bindData(final GPIODevicesRecord item) {
            name.setText(StringUtils.capFirstLetter(item.name()));
            function.setText(item.function());
            pin.setText(item.pin());
        }
    }
}
