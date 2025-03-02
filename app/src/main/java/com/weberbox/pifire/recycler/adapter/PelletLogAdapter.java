package com.weberbox.pifire.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.databinding.ItemPelletsListLogBinding;
import com.weberbox.pifire.databinding.ItemViewMoreBinding;
import com.weberbox.pifire.model.local.PelletLogModel;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PelletLogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_VIEW = 1;
    private final List<PelletLogModel> list;
    private final PelletLogCallback callback;
    private final int limitAmount;
    private boolean limitEnabled;
    private RecyclerView recyclerView;

    @SuppressWarnings("unused")
    public PelletLogAdapter(@NotNull PelletLogCallback callback, boolean limitEnabled,
                            int limitAmount) {
        this.list = new ArrayList<>();
        this.callback = callback;
        this.limitEnabled = limitEnabled;
        this.limitAmount = limitAmount;
    }

    public PelletLogAdapter(@NotNull PelletLogCallback callback, boolean limitEnabled) {
        this.list = new ArrayList<>();
        this.callback = callback;
        this.limitEnabled = limitEnabled;
        this.limitAmount = AppConfig.RECYCLER_LIMIT;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent,
                                                          final int viewType) {
        if (viewType == FOOTER_VIEW) {
            return new FooterViewHolder(ItemViewMoreBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        } else {
            return new ItemsViewHolder(ItemPelletsListLogBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,
                                 final int position) {
        try {
            if (holder instanceof ItemsViewHolder vh) {
                vh.bindData(list.get(position));
                vh.root.setOnClickListener(view -> callback.onPelletLogDeleted(
                        vh.pelletID, vh.getAbsoluteAdapterPosition()));
            } else if (holder instanceof FooterViewHolder vh) {
                vh.bindData(vh.itemView.getContext(), limitEnabled);
                vh.viewAll.setOnClickListener(view -> toggleViewAll(vh.itemView.getContext()));

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
    public void setPelletLogs(@NotNull Map<String, String> logs,
                              @NotNull Map<String, PelletProfileModel> profiles) {
        list.clear();
        for (String date : logs.keySet()) {
            String logPelletId = logs.get(date);
            PelletProfileModel pelletProfile = profiles.get(logPelletId);

            if (pelletProfile != null) {
                PelletLogModel logList = new PelletLogModel(
                        date, pelletProfile.getBrand() + " " + pelletProfile.getWood(),
                        pelletProfile.getRating()
                );
                list.add(logList);
            }
        }
        notifyDataSetChanged();
    }

    public void removeLogItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    @SuppressWarnings("NotifyDataSetChanged")
    private void toggleViewAll(Context context) {
        LayoutAnimationController animation;
        if (limitEnabled) {
            animation = AnimationUtils.loadLayoutAnimation(context,
                    R.anim.fall_down_animation);
        } else {
            animation = AnimationUtils.loadLayoutAnimation(context,
                    R.anim.slide_up_animation);
        }
        recyclerView.setLayoutAnimation(animation);
        this.limitEnabled = !limitEnabled;
        notifyDataSetChanged();
    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {

        private final View root;
        private final TextView pelletDate;
        private final TextView pelletName;
        private final RatingBar pelletRating;
        private String pelletID;

        public ItemsViewHolder(ItemPelletsListLogBinding binding) {
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

    public interface PelletLogCallback {
        void onPelletLogDeleted(@NotNull String logDate, int position);
    }
}

