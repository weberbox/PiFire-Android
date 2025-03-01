package com.weberbox.pifire.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.databinding.ItemPelletsListBinding;
import com.weberbox.pifire.databinding.ItemViewMoreBinding;
import com.weberbox.pifire.record.PelletItemRecord;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class PelletItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_VIEW = 1;
    private final PelletItemsCallback callback;
    private final List<PelletItemRecord> list;
    private final int limitAmount;
    private boolean limitEnabled;

    @SuppressWarnings("unused")
    public PelletItemsAdapter(@NotNull PelletItemsCallback callback, boolean limitEnabled,
                              int limitAmount) {
        this.list = new ArrayList<>();
        this.callback = callback;
        this.limitEnabled = limitEnabled;
        this.limitAmount = limitAmount;
    }

    public PelletItemsAdapter(@NotNull PelletItemsCallback callback, boolean limitEnabled) {
        this.list = new ArrayList<>();
        this.callback = callback;
        this.limitEnabled = limitEnabled;
        this.limitAmount = AppConfig.RECYCLER_LIMIT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                            final int viewType) {
        if (viewType == FOOTER_VIEW) {
            return new FooterViewHolder(ItemViewMoreBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        } else {
            return new ItemsViewHolder(ItemPelletsListBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        try {
            if (holder instanceof ItemsViewHolder vh) {
                vh.bindData(list.get(position));
                vh.pelletIcon.setOnClickListener(view -> callback.onPelletItemDeleted(
                        vh.pelletItem.getText().toString(), vh.pelletType,
                        vh.getAbsoluteAdapterPosition()));
            } else if (holder instanceof FooterViewHolder vh) {
                vh.bindData(vh.itemView.getContext(), limitEnabled);
                vh.viewAll.setOnClickListener(view -> toggleViewAll());

            }
        } catch (Exception e) {
            Timber.e(e, "onBindViewHolder Error");
        }
    }

    @Override
    public int getItemCount() {
        if (limitEnabled) {
            return Math.min(list.size(), limitAmount + 1);
        } else {
            if (list.size() > limitAmount) {
                return list.size() + 1;
            } else {
                return list.size();
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (limitEnabled) {
            if (position == limitAmount) {
                return FOOTER_VIEW;
            } else {
                return super.getItemViewType(position);
            }
        } else {
            if (position == list.size()) {
                return FOOTER_VIEW;
            } else {
                return super.getItemViewType(position);
            }
        }
    }

    @SuppressWarnings("NotifyDataSetChanged")
    public void setPelletItems(@NotNull List<String> items, @NotNull String type) {
        list.clear();
        for (int i = 0; i < items.size(); i++) {
            PelletItemRecord itemRecord = new PelletItemRecord(
                    items.get(i), type
            );
            list.add(itemRecord);
        }
        notifyDataSetChanged();
    }

    public List<String> getPelletItems() {
        List<String> itemList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            itemList.add(list.get(i).pelletItem());
        }
        return itemList;
    }

    public void addNewPelletItem(String item, String type) {
        this.limitEnabled = false;
        list.add(list.size(), new PelletItemRecord(item, type));
        notifyItemInserted(list.size() - 1);
    }

    public void removePelletItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @SuppressWarnings("NotifyDataSetChanged")
    private void toggleViewAll() {
        this.limitEnabled = !limitEnabled;
        notifyDataSetChanged();
    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {

        private final TextView pelletItem;
        private final ImageView pelletIcon;
        private String pelletType;

        public ItemsViewHolder(ItemPelletsListBinding binding) {
            super(binding.getRoot());
            pelletItem = binding.pelletsItem;
            pelletIcon = binding.pelletsItemDelete;
        }

        public void bindData(final PelletItemRecord record) {
            pelletItem.setText(record.pelletItem());
            pelletType = record.pelletType();
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {

        private final TextView viewAll;

        public FooterViewHolder(ItemViewMoreBinding binding) {
            super(binding.getRoot());
            viewAll = binding.viewAllButton;
        }

        public void bindData(Context context, boolean limitEnabled) {
            if (limitEnabled) {
                viewAll.setText(context.getString(R.string.view_all));
            } else {
                viewAll.setText(context.getString(R.string.view_less));
            }
        }

    }

    public interface PelletItemsCallback {
        void onPelletItemDeleted(@NotNull String item, @NotNull String type, int position);
    }
}
