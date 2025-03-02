package com.weberbox.pifire.recycler.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.databinding.ItemLicenseListBinding;
import com.weberbox.pifire.databinding.ItemViewMoreBinding;
import com.weberbox.pifire.model.local.LicensesModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import timber.log.Timber;

public class LicensesListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOTER_VIEW = 1;
    private final List<LicensesModel> list;
    private final int limitAmount;
    private boolean limitEnabled;
    private RecyclerView recyclerView;

    @SuppressWarnings("unused")
    public LicensesListAdapter(boolean limitEnabled, int limitAmount) {
        this.list = new ArrayList<>();
        this.limitEnabled = limitEnabled;
        this.limitAmount = limitAmount;
    }

    public LicensesListAdapter(boolean limitEnabled) {
        this.list = new ArrayList<>();
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
            return new ItemsViewHolder(ItemLicenseListBinding.inflate(LayoutInflater.from(
                    parent.getContext()), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder,
                                 final int position) {
        try {
            if (holder instanceof ItemsViewHolder vh) {
                vh.bindData(list.get(position));
                vh.root.setOnClickListener(v -> {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(vh.projectLicense.getText().toString()));
                    vh.itemView.getContext().startActivity(i);
                });
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
    public void setLicenseList(List<LicensesModel> licenceList) {
        list.clear();
        list.addAll(licenceList);
        notifyDataSetChanged();
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
        private final int[] colors;
        private final Random random;
        private final CardView cardView;
        private final TextView projectIcon;
        private final TextView projectText;
        private final TextView projectLicense;

        public ItemsViewHolder(ItemLicenseListBinding binding) {
            super(binding.getRoot());
            colors = binding.getRoot().getContext().getResources()
                    .getIntArray(R.array.licenses_color_list);
            random = new Random();
            root = binding.getRoot();
            cardView = binding.licenseItemViewContainer;
            projectIcon = binding.licenseIconHolder;
            projectText = binding.licenseProjectName;
            projectLicense = binding.licenseTextHolder;
        }

        public void bindData(final LicensesModel license) {
            int randomColor = random.nextInt(colors.length);
            cardView.setCardBackgroundColor(colors[randomColor]);
            projectIcon.setBackgroundColor(colors[randomColor]);
            projectIcon.setText(license.getProject().substring(0, 1).toUpperCase());
            projectText.setText(license.getProject());
            projectLicense.setText(license.getLicense());
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
}
