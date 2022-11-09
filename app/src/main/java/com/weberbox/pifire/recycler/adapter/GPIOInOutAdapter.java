package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemGpioInOutBinding;
import com.weberbox.pifire.model.local.GPIOInOutModel;
import com.weberbox.pifire.utils.StringUtils;

import java.util.List;

public class GPIOInOutAdapter extends RecyclerView.Adapter<GPIOInOutAdapter.ViewHolder> {

    private List<GPIOInOutModel> list;

    public GPIOInOutAdapter(final List<GPIOInOutModel> list) {
        this.list = list;
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
        holder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setList(List<GPIOInOutModel> list) {
        this.list = list;
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

        public void bindData(final GPIOInOutModel item) {
            name.setText(StringUtils.capFirstLetter(item.getName()));
            pin.setText(item.getPin());
        }
    }
}