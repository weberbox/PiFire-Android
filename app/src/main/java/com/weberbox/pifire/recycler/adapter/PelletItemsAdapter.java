package com.weberbox.pifire.recycler.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.databinding.ItemPelletsListBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogPelletsProfileCallback;
import com.weberbox.pifire.model.local.PelletItemModel;

import java.util.List;

public class PelletItemsAdapter extends RecyclerView.Adapter<PelletItemsAdapter.ViewHolder> {

    private final DialogPelletsProfileCallback callback;
    private final List<PelletItemModel> list;
    private boolean limited;

    public PelletItemsAdapter(final List<PelletItemModel> list, DialogPelletsProfileCallback callback,
                              boolean limited) {
        this.list = list;
        this.callback = callback;
        this.limited = limited;
    }

    @NonNull
    @Override
    public PelletItemsAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                            final int viewType) {
        return new ViewHolder(ItemPelletsListBinding.inflate(LayoutInflater.from(
                parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bindData(list.get(position));
        holder.pelletIcon.setOnClickListener(view -> callback.onItemDelete(
                holder.pelletId,
                holder.pelletItem.getText().toString(), holder.getAbsoluteAdapterPosition()));
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

        private final TextView pelletItem;
        private final ImageView pelletIcon;
        private String pelletId;

        public ViewHolder(ItemPelletsListBinding binding) {
            super(binding.getRoot());
            pelletItem = binding.pelletsItem;
            pelletIcon = binding.pelletsItemDelete;
        }

        public void bindData(final PelletItemModel viewModel) {
            pelletItem.setText(viewModel.getPelletItem());
            pelletId = viewModel.getPelletItemId();
        }
    }
}
