package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemAppriseLocationAddBinding;
import com.weberbox.pifire.databinding.ItemAppriseLocationBinding;
import com.weberbox.pifire.interfaces.AppriseCallback;

import java.util.List;

import timber.log.Timber;

public class AppriseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_VIEW = 1;
    private final List<String> list;
    private final AppriseCallback callback;

    public AppriseAdapter(final List<String> list, AppriseCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FOOTER_VIEW) {
            return new FooterViewHolder(ItemAppriseLocationAddBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        } else {
            return new ItemsViewHolder(ItemAppriseLocationBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            if (holder instanceof ItemsViewHolder vh) {
                vh.edit.setOnClickListener(view ->
                        callback.onLocationEdit(position));
                vh.delete.setOnClickListener(view ->
                        callback.onLocationDelete(position));
                vh.bindData(list, position);
            } else if (holder instanceof FooterViewHolder vh) {
                vh.addItem.setOnClickListener(view ->
                        callback.onLocationAdd());
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

    public void addLocation(String location) {
        list.add(list.size(), location);
        notifyItemInserted(list.size());
    }

    public void removeLocation(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void updateLocation(int position, String location) {
        list.set(position, location);
        notifyItemChanged(position);
    }

    public List<String> getLocations() {
        return list;
    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {

        private final TextView location;
        private final ImageButton edit;
        private final ImageButton delete;

        public ItemsViewHolder(ItemAppriseLocationBinding binding) {
            super(binding.getRoot());
            location = binding.appriseLocation;
            edit = binding.appriseLocationEdit;
            delete = binding.appriseLocationDelete;
        }

        public void bindData(final List<String> list, int position) {
            if (list.size() > 0) {
                location.setText(list.get(position));
            }
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        private final ImageButton addItem;

        public FooterViewHolder(ItemAppriseLocationAddBinding binding) {
            super(binding.getRoot());
            addItem = binding.appriseLocationAdd;
        }
    }
}
