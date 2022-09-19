package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemSmartStartAddBinding;
import com.weberbox.pifire.databinding.ItemSmartStartBinding;
import com.weberbox.pifire.interfaces.SmartStartCallback;
import com.weberbox.pifire.model.local.SmartStartModel;

import java.util.List;

import timber.log.Timber;

public class SmartStartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_VIEW = 1;
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
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FOOTER_VIEW) {
            return new FooterViewHolder(ItemSmartStartAddBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        } else {
            return new ItemsViewHolder(ItemSmartStartBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ItemsViewHolder) {
                ItemsViewHolder vh = (ItemsViewHolder) holder;
                vh.edit.setOnClickListener(view ->
                        callback.onSmartStartEdit(position));
                vh.delete.setOnClickListener(view ->
                        callback.onSmartStartDelete(position));
                vh.bindData(list, position, units);
            } else if (holder instanceof FooterViewHolder) {
                FooterViewHolder vh = (FooterViewHolder) holder;
                vh.addItem.setOnClickListener(view ->
                        callback.onSmartStartAdd());
            }
        } catch (Exception e) {
            Timber.e(e, "onBindViewHolder Error");
        }
    }

    @Override
    public int getItemCount() {
        if (list == null) {
            return 0;
        } else if (list.size() == 0) {
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
        return list.size() == 0 ? null : list;
    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {

        private final TextView range;
        private final TextView startUp;
        private final TextView augerOn;
        private final TextView pMode;
        private final ImageButton edit;
        private final ImageButton delete;

        public ItemsViewHolder(ItemSmartStartBinding binding) {
            super(binding.getRoot());
            range = binding.smartStartRange;
            startUp = binding.smartStartStartup;
            augerOn = binding.smartStartAugerOn;
            pMode = binding.smartStartPMode;
            edit = binding.smartStartEdit;
            delete = binding.smartStartDelete;
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

        public FooterViewHolder(ItemSmartStartAddBinding binding) {
            super(binding.getRoot());
            addItem = binding.smartStartAddNew;
        }
    }
}

