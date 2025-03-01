package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.databinding.ItemPickerTextBinding;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PelletProfileAdapter extends RecyclerView.Adapter<PelletProfileAdapter.ViewHolder> {

    private final List<PelletProfileModel> list;

    public PelletProfileAdapter(@NotNull final List<PelletProfileModel> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public PelletProfileAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                              final int viewType) {
        return new ViewHolder(ItemPickerTextBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bindData(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView pelletProfile;
        private final TextView pelletProfileId;

        public ViewHolder(ItemPickerTextBinding binding) {
            super(binding.getRoot());
            pelletProfile = binding.pickerItemTextView;
            pelletProfileId = binding.pickerItemId;
        }

        public void bindData(final PelletProfileModel profile) {
            String item = profile.getBrand() + " " + profile.getWood();
            pelletProfile.setText(item);
            pelletProfileId.setText(profile.getId());
        }
    }
}