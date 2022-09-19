package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemGpioDeviceBinding;
import com.weberbox.pifire.model.local.GPIODevicesModel;
import com.weberbox.pifire.utils.StringUtils;

import java.util.List;

public class GPIODevicesAdapter extends RecyclerView.Adapter<GPIODevicesAdapter.ViewHolder> {

    private List<GPIODevicesModel> list;

    public GPIODevicesAdapter(final List<GPIODevicesModel> list) {
        this.list = list;
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
        holder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<GPIODevicesModel> list) {
        this.list = list;
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

        public void bindData(final GPIODevicesModel item) {
            name.setText(StringUtils.capFirstLetter(item.getName()));
            function.setText(item.getFunction());
            pin.setText(item.getPin());
        }
    }
}
