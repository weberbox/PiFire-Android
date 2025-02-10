package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemExtraHeadersBinding;
import com.weberbox.pifire.interfaces.ExtraHeadersCallback;
import com.weberbox.pifire.model.local.ExtraHeadersModel;

import java.util.ArrayList;

public class ExtraHeadersAdapter extends RecyclerView.Adapter<ExtraHeadersAdapter.ViewHolder> {
    private final ArrayList<ExtraHeadersModel> list;
    private final ExtraHeadersCallback callback;

    public ExtraHeadersAdapter(final ArrayList<ExtraHeadersModel> list, ExtraHeadersCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ExtraHeadersAdapter.ViewHolder(ItemExtraHeadersBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> callback.onHeaderEdit(position));
        holder.bindData(list, position);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addNewHeaderItem(String key, String value) {
        list.add(list.size(), new ExtraHeadersModel(key, value));
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeHeaderItem(int position) {
        list.remove(position);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateHeaderItem(int position, String key, String value) {
        list.set(position, new ExtraHeadersModel(key, value));
        notifyDataSetChanged();
    }

    public ArrayList<ExtraHeadersModel> getHeaderItems() {
        return list.isEmpty() ? null : list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView key;
        private final TextView value;

        public ViewHolder(ItemExtraHeadersBinding binding) {
            super(binding.getRoot());
            key = binding.extraHeadersKey;
            value = binding.extraHeadersValue;
        }

        public void bindData(final ArrayList<ExtraHeadersModel> list, int position) {
            if (!list.isEmpty()) {
                key.setText(list.get(position).getHeaderKey());
                value.setText(list.get(position).getHeaderValue());
            }
        }
    }
}
