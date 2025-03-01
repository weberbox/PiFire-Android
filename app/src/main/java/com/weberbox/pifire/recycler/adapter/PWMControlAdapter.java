package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemPwmControlBinding;
import com.weberbox.pifire.record.PWMControlRecord;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PWMControlAdapter extends RecyclerView.Adapter<PWMControlAdapter.ViewHolder> {

    private final List<PWMControlRecord> list;
    private final PWMControlCallback callback;
    private final String units;

    public PWMControlAdapter(@NotNull final List<PWMControlRecord> list, @NotNull String units,
                             @NotNull PWMControlCallback callback) {
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
        return list.size();
    }

    public void addNewControlItem(@NotNull Integer temp, int dutyCycle) {
        int lastPosition = list.size() - 1;
        Integer lastDutyCycle = list.get(lastPosition).dutyCycle();
        list.set(lastPosition, new PWMControlRecord(temp, lastDutyCycle));
        list.add(list.size(), new PWMControlRecord(temp + 1, dutyCycle));
        notifyItemRangeChanged(lastPosition, 2);
    }

    public void removeControlItem(int position) {
        list.remove(position);
        int lastPosition = list.size() - 1;
        Integer lastDutyCycle = list.get(lastPosition).dutyCycle();
        Integer lastTemp = list.get(list.size() - 2).temp();
        list.set(position - 1, new PWMControlRecord(lastTemp + 1, lastDutyCycle));
        notifyItemRangeChanged(position - 1, 2);
    }

    public void updateControlItem(int position, @NotNull Integer temp, int dutyCycle) {
        list.set(position, new PWMControlRecord(temp, dutyCycle));
        notifyItemRangeChanged(position , 2);
    }

    public List<PWMControlRecord> getControlItems() {
        return list;
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

        public void bindData(final List<PWMControlRecord> list, int position, String units) {
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

                String dutyCycle = list.get(position).dutyCycle() + "%";

                range.setText(temp);
                duty.setText(dutyCycle);
            }
        }
    }

    public interface PWMControlCallback {
        void onPWMControlEdit(int position);
    }
}
