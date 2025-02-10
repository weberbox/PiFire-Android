package com.weberbox.pifire.recycler.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemAppriseLocationBinding;
import com.weberbox.pifire.interfaces.AppriseCallback;

import java.util.List;

public class AppriseAdapter extends RecyclerView.Adapter<AppriseAdapter.ViewHolder> {

    private final List<String> list;
    private final AppriseCallback callback;

    public AppriseAdapter(final List<String> list, AppriseCallback callback) {
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public AppriseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemAppriseLocationBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item.setOnClickListener(view ->
                callback.onLocationEdit(position));
        holder.bindData(list, position);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addLocation(String location) {
        list.add(list.size(), location);
        notifyDataSetChanged();
//        notifyItemInserted(list.size());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeLocation(int position) {
        list.remove(position);
        notifyDataSetChanged();
//        notifyItemRemoved(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateLocation(int position, String location) {
        list.set(position, location);
        notifyDataSetChanged();
//        notifyItemChanged(position);
    }

    public List<String> getLocations() {
        return list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView location;
        private final ConstraintLayout item;

        public ViewHolder(ItemAppriseLocationBinding binding) {
            super(binding.getRoot());
            location = binding.appriseLocation;
            item = binding.itemAppriseContainer;
        }

        public void bindData(final List<String> list, int position) {
            if (!list.isEmpty()) {
                location.setText(list.get(position));
            }
        }
    }
}
