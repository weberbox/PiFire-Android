package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.ItemPelletsListEditBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogPelletsProfileCallback;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;

import java.util.List;

public class PelletProfileEditAdapter extends
        RecyclerView.Adapter<PelletProfileEditAdapter.ViewHolder> {

    private final List<PelletProfileModel> list;
    private final DialogPelletsProfileCallback callback;
    private boolean limited;

    public PelletProfileEditAdapter(final List<PelletProfileModel> list,
                                    DialogPelletsProfileCallback callback, boolean limited) {
        this.list = list;
        this.callback = callback;
        this.limited = limited;
    }

    @NonNull
    @Override
    public PelletProfileEditAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                                  final int viewType) {
        return new ViewHolder(ItemPelletsListEditBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bindData(list.get(position));

        holder.deleteIcon.setOnClickListener(view -> callback.onItemDelete(Constants.PELLET_PROFILE,
                holder.pelletProfileId, holder.getAbsoluteAdapterPosition()));

        holder.profileView.setOnClickListener(view ->
                callback.onProfileOpen(list.get(position), position));
    }

    @Override
    public int getItemCount() {
        if (limited) {
            return Math.min(list.size(), 3);
        } else {
            return list == null ? 0 : list.size();
        }
    }

    public void setLimitEnabled(boolean enabled) {
        limited = enabled;
    }

    public boolean getLimitEnabled() {
        return limited;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final View profileView;
        private final TextView pelletProfile;
        private final ImageView deleteIcon;
        private String pelletProfileId;


        public ViewHolder(ItemPelletsListEditBinding binding) {
            super(binding.getRoot());

            pelletProfile = binding.pelletsItem;
            deleteIcon = binding.pelletsItemDelete;

            profileView = binding.pelletEditCardHeader;
        }

        public void bindData(final PelletProfileModel model) {
            String item = model.getBrand() + " " + model.getWood();
            pelletProfile.setText(item);
            pelletProfileId = model.getId();
        }
    }
}