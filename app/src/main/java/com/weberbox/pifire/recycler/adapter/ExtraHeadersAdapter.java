package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemExtraHeadersBinding;
import com.weberbox.pifire.model.local.ExtraHeadersModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ExtraHeadersAdapter extends RecyclerView.Adapter<ExtraHeadersAdapter.ViewHolder> {
    private final ArrayList<ExtraHeadersModel> list;
    private final ExtraHeadersCallback callback;

    public ExtraHeadersAdapter(@NotNull final ArrayList<ExtraHeadersModel> list,
                               @NotNull ExtraHeadersCallback callback) {
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
        return list.size();
    }

    public void addNewHeaderItem(String key, String value) {
        list.add(list.size(), new ExtraHeadersModel(key, value));
        notifyItemInserted(list.size() - 1);
    }

    public void removeHeaderItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    public void updateHeaderItem(int position, String key, String value) {
        list.set(position, new ExtraHeadersModel(key, value));
        notifyItemChanged(position);
    }

    public ArrayList<ExtraHeadersModel> getHeaderItems() {
        return list;
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

    public interface ExtraHeadersCallback {
        void onHeaderEdit(int position);
    }
}
