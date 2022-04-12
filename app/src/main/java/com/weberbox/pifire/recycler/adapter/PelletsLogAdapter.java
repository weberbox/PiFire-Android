package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.databinding.ItemPelletsListLogBinding;
import com.weberbox.pifire.interfaces.PelletsProfileCallback;
import com.weberbox.pifire.model.local.PelletLogModel;

import java.util.List;

public class PelletsLogAdapter extends RecyclerView.Adapter<PelletsLogAdapter.ViewHolder> {

    private final List<PelletLogModel> list;
    private final PelletsProfileCallback callback;
    private boolean limited;

    public PelletsLogAdapter(final List<PelletLogModel> list, PelletsProfileCallback callback,
                             boolean limit) {
        this.list = list;
        this.callback = callback;
        this.limited = limit;
    }

    @NonNull
    @Override
    public PelletsLogAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                           final int viewType) {
        return new ViewHolder(ItemPelletsListLogBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bindData(list.get(position));
        holder.root.setOnLongClickListener(v -> {
            callback.onLogLongClick(holder.pelletID, holder.getAbsoluteAdapterPosition());
            return true;
        });
    }

    @Override
    public int getItemCount() {
        if (limited) {
            return Math.min(list.size(), AppConfig.RECYCLER_LIMIT);
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

        private final View root;
        private final TextView pelletDate;
        private final TextView pelletName;
        private final RatingBar pelletRating;
        private String pelletID;

        public ViewHolder(ItemPelletsListLogBinding binding) {
            super(binding.getRoot());
            root = binding.getRoot();
            pelletDate = binding.pelletsLogDate;
            pelletName = binding.pelletsLogName;
            pelletRating = binding.pelletsLogRating;
        }

        public void bindData(final PelletLogModel model) {
            pelletID = model.getPelletID();
            pelletDate.setText(model.getPelletDate());
            pelletName.setText(model.getPelletName());
            pelletRating.setRating((float) model.getPelletRating());
        }
    }
}

