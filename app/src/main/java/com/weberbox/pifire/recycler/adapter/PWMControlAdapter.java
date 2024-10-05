package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemPwmControlAddBinding;
import com.weberbox.pifire.databinding.ItemPwmControlBinding;
import com.weberbox.pifire.interfaces.PWMControlCallback;
import com.weberbox.pifire.model.local.PWMControlModel;

import java.util.List;

import timber.log.Timber;

public class PWMControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_VIEW = 1;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FOOTER_VIEW) {
            return new FooterViewHolder(ItemPwmControlAddBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        } else {
            return new ItemsViewHolder(ItemPwmControlBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ItemsViewHolder vh) {
                vh.edit.setOnClickListener(view ->
                        callback.onPWMControlEdit(position));
                vh.delete.setOnClickListener(view ->
                        callback.onPWMControlDelete(position));
                vh.bindData(list, position, units);
            } else if (holder instanceof FooterViewHolder vh) {
                vh.addItem.setOnClickListener(view ->
                        callback.onPWMControlAdd());
            }
        } catch (Exception e) {
            Timber.e(e, "onBindViewHolder Error");
        }
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        } else if (list.isEmpty()) {
            return 1;
        } else {
            return list.size() + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == list.size()) {
            return FOOTER_VIEW;
        } else {
            return super.getItemViewType(position);
        }
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

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {

        private final TextView range;
        private final TextView duty;
        private final ImageButton edit;
        private final ImageButton delete;

        public ItemsViewHolder(ItemPwmControlBinding binding) {
            super(binding.getRoot());
            range = binding.pwmControlRange;
            duty = binding.pwmControlDuty;
            edit = binding.pwmControlEdit;
            delete = binding.pwmControlDelete;
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

                if (position == 0 || position == 1 || position < rangeSize - 1) {
                    delete.setEnabled(false);
                    delete.setAlpha((float) 0.6);
                } else {
                    delete.setEnabled(true);
                    delete.setAlpha((float) 1.0);
                }
            }
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        private final ImageButton addItem;

        public FooterViewHolder(ItemPwmControlAddBinding binding) {
            super(binding.getRoot());
            addItem = binding.pwmControlAddNew;
        }
    }
}
