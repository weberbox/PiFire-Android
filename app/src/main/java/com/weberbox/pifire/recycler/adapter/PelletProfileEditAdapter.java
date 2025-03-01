package com.weberbox.pifire.recycler.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.ItemPelletsListEditBinding;
import com.weberbox.pifire.databinding.ItemViewMoreBinding;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class PelletProfileEditAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_VIEW = 1;
    private final List<PelletProfileModel> list;
    private final PelletProfileEditCallback callback;
    private final int limitAmount;
    private boolean limitEnabled;

    @SuppressWarnings("unused")
    public PelletProfileEditAdapter(@NotNull PelletProfileEditCallback callback,
                                    boolean limitEnabled, int limitAmount) {
        this.list = new ArrayList<>();
        this.callback = callback;
        this.limitEnabled = limitEnabled;
        this.limitAmount = limitAmount;
    }

    public PelletProfileEditAdapter(@NotNull PelletProfileEditCallback callback,
                                    boolean limitEnabled) {
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
            return new ItemsViewHolder(ItemPelletsListEditBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,
                                 final int position) {
        try {
            if (holder instanceof ItemsViewHolder vh) {
                vh.bindData(list.get(position));
                vh.deleteIcon.setOnClickListener(view -> callback.onPelletProfileDeleted(
                        vh.pelletProfileId, Constants.PELLET_PROFILE,
                        vh.getAbsoluteAdapterPosition()));
                vh.profileView.setOnClickListener(view ->
                        callback.onPelletProfileOpen(list.get(position),
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
    public void setPelletProfiles(@NotNull Map<String, PelletProfileModel> profiles) {
        list.clear();
        for (PelletProfileModel profile : profiles.values()) {
            if (profile != null) {
                PelletProfileModel profileEditList = new PelletProfileModel(
                        profile.getBrand(),
                        profile.getWood(),
                        profile.getRating(),
                        profile.getComments(),
                        profile.getId()
                );
                list.add(profileEditList);
            }
        }
        notifyDataSetChanged();
    }

    public List<PelletProfileModel> getPelletProfiles() {
        return list;
    }

    @SuppressWarnings("unused")
    public void addPelletProfile(PelletProfileModel profile) {
        this.limitEnabled = false;
        list.add(list.size(), profile);
        notifyItemInserted(list.size() - 1);
    }

    public void removePelletProfile(int position) {
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

        private final View profileView;
        private final TextView pelletProfile;
        private final ImageView deleteIcon;
        private String pelletProfileId;


        public ItemsViewHolder(ItemPelletsListEditBinding binding) {
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

    public interface PelletProfileEditCallback {
        void onPelletProfileDeleted(String item, String type, int position);

        void onPelletProfileOpen(PelletProfileModel profile, int position);
    }
}