package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemPwmControlBinding;
import com.weberbox.pifire.interfaces.PWMControlCallback;
import com.weberbox.pifire.model.local.PWMControlModel;

import java.util.List;

public class PWMControlAdapter extends RecyclerView.Adapter<PWMControlAdapter.ViewHolder> {

    private final List<PWMControlModel> list;
    private final PWMControlCallback callback;
    private final String units;

    public PWMControlAdapter(final List<PWMControlModel> list, String units,
                             PWMControlCallback callback) {
        this.list = list;
        this.units = units;
        this.callback = callback;
    }

    @NonNull
    @Override
    public PWMControlAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemPwmControlBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item.setOnClickListener(view ->
                callback.onPWMControlEdit(position));
        holder.bindData(list, position, units);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addNewControlItem(Integer temp, int dutyCycle) {
        Integer lastDutyCycle = list.get(list.size() - 1).getDutyCycle();
        list.remove(list.size() - 1);
        list.add(list.size(), new PWMControlModel(temp, lastDutyCycle));
        list.add(list.size(), new PWMControlModel(temp + 1, dutyCycle));
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeControlItem(int position) {
        list.remove(position);
        Integer lastDutyCycle = list.get(list.size() - 1).getDutyCycle();
        Integer lastTemp = list.get(list.size() - 2).getTemp();
        list.set(position - 1, new PWMControlModel(lastTemp + 1, lastDutyCycle));
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateControlItem(int position, Integer temp, int dutyCycle) {
        list.set(position, new PWMControlModel(temp, dutyCycle));
        notifyDataSetChanged();
    }

    public List<PWMControlModel> getControlItems() {
        return list.isEmpty() ? null : list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView range;
        private final TextView duty;
        private final LinearLayout item;

        public ViewHolder(ItemPwmControlBinding binding) {
            super(binding.getRoot());
            range = binding.pwmControlRange;
            duty = binding.pwmControlDuty;
            item = binding.itemPwmContainer;
        }

        public void bindData(final List<PWMControlModel> list, int position, String units) {
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

                String dutyCycle = list.get(position).getDutyCycle() + "%";

                range.setText(temp);
                duty.setText(dutyCycle);
            }
        }
    }
}
