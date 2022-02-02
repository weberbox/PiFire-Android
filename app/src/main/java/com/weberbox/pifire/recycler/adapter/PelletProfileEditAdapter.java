package com.weberbox.pifire.recycler.adapter;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.skydoves.powerspinner.DefaultSpinnerAdapter;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.databinding.ItemPelletsListEditBinding;
import com.weberbox.pifire.databinding.LayoutPelletsEditCardBinding;
import com.weberbox.pifire.interfaces.PelletsProfileCallback;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.RotateUtils;
import com.weberbox.pifire.utils.StringUtils;

import java.util.List;

import timber.log.Timber;

public class PelletProfileEditAdapter extends RecyclerView.Adapter<PelletProfileEditAdapter.ViewHolder> {

    private final List<PelletProfileModel> list;
    private final List<String> brandsList;
    private final List<String> woodsList;
    private final PelletsProfileCallback callback;
    private final Activity activity;
    private boolean limited;

    public PelletProfileEditAdapter(Activity activity, final List<String> brands, List<String> woods,
                                    final List<PelletProfileModel> list,
                                    PelletsProfileCallback callback, boolean limited) {
        this.list = list;
        this.callback = callback;
        this.brandsList = brands;
        this.woodsList = woods;
        this.activity = activity;
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
                holder.pelletProfileId.getText().toString(), holder.getAbsoluteAdapterPosition()));

        holder.profileView.setOnClickListener(view -> {
            AnimUtils.fadeAnimation(holder.deleteIcon, 300,
                    holder.deleteIcon.getVisibility() ==
                            View.VISIBLE ? Constants.FADE_OUT : Constants.FADE_IN);
            holder.toggleCardView();
        });

        holder.pelletProfileRating.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);

        holder.pelletProfileBrand.setSpinnerOutsideTouchListener((view, motionEvent) ->
                holder.pelletProfileBrand.dismiss());

        holder.pelletProfileWood.setSpinnerOutsideTouchListener((view, motionEvent) ->
                holder.pelletProfileWood.dismiss());

        holder.pelletProfileRating.setSpinnerOutsideTouchListener((view, motionEvent) ->
                holder.pelletProfileRating.dismiss());

        holder.pelletProfileBrand.setOnSpinnerItemSelectedListener(
                (OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) ->
                        Timber.d("New Item %s", newItem));

        holder.pelletProfileWood.setOnSpinnerItemSelectedListener(
                (OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) ->
                        Timber.d("New Item %s", newItem));

        holder.pelletProfileRating.setOnSpinnerItemSelectedListener(
                (OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) ->
                        Timber.d("New Item %s", newItem));

        holder.deleteButton.setOnClickListener(view -> {
            holder.toggleCardView();
            holder.deleteIcon.setVisibility(View.VISIBLE);
            if (holder.pelletProfileId.getText() != null) {
                callback.onProfileDelete(holder.pelletProfileId.getText().toString(),
                        holder.getAbsoluteAdapterPosition());
            }
        });

        holder.saveButton.setOnClickListener(view -> {
            holder.toggleCardView();
            holder.deleteIcon.setVisibility(View.VISIBLE);
            if (holder.pelletProfileId.getText() != null) {
                callback.onProfileEdit(
                        new PelletProfileModel(
                                holder.pelletProfileBrand.getText().toString(),
                                holder.pelletProfileWood.getText().toString(),
                                StringUtils.getRatingInt(holder.pelletProfileRating.getText().toString()),
                                holder.pelletProfileComments.getText().toString(),
                                holder.pelletProfileId.getText().toString()
                        )
                );
            }
        });

        DefaultSpinnerAdapter brandsSpinnerAdapter = new DefaultSpinnerAdapter(holder.pelletProfileBrand);
        holder.pelletProfileBrand.setSpinnerAdapter(brandsSpinnerAdapter);
        holder.pelletProfileBrand.setItems(brandsList);
        holder.pelletProfileBrand.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);
        setPowerSpinnerMaxHeight(brandsSpinnerAdapter, holder.pelletProfileBrand.getSpinnerRecyclerView());

        DefaultSpinnerAdapter woodsSpinnerAdapter = new DefaultSpinnerAdapter(holder.pelletProfileWood);
        holder.pelletProfileWood.setSpinnerAdapter(woodsSpinnerAdapter);
        holder.pelletProfileWood.setItems(woodsList);
        holder.pelletProfileWood.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);
        setPowerSpinnerMaxHeight(woodsSpinnerAdapter, holder.pelletProfileWood.getSpinnerRecyclerView());

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
        private final TextView pelletProfileId;
        private final PowerSpinnerView pelletProfileBrand;
        private final PowerSpinnerView pelletProfileWood;
        private final PowerSpinnerView pelletProfileRating;
        private final TextView pelletProfileComments;
        private final ImageView expandIcon;
        private final ImageView deleteIcon;
        private final ConstraintLayout cardView;
        private final LinearLayout editCardView;
        private final AppCompatButton deleteButton;
        private final AppCompatButton saveButton;


        public ViewHolder(ItemPelletsListEditBinding binding) {
            super(binding.getRoot());

            pelletProfile = binding.pelletsItem;
            pelletProfileId = binding.pelletsItemId;
            deleteIcon = binding.pelletsItemDelete;

            LayoutPelletsEditCardBinding profileEditBinding =
                    binding.pelletsProfileEditLayout.pelletEditContainer;

            pelletProfileBrand = profileEditBinding.pelletEditBrandText;
            pelletProfileWood = profileEditBinding.pelletEditWoodText;
            pelletProfileRating = profileEditBinding.pelletEditRatingText;
            pelletProfileComments = profileEditBinding.pelletEditCommentsText;
            profileView = binding.pelletEditCardHeader;
            expandIcon = binding.pelletEditExpandIcon;
            cardView = binding.pelletEditCardView;
            editCardView = binding.pelletEditCardContainer;

            deleteButton = binding.pelletsProfileEditLayout.pelletEditDelete;
            saveButton = binding.pelletsProfileEditLayout.pelletEditSave;
        }

        public void toggleCardView() {
            boolean visibility = editCardView.getVisibility() == View.VISIBLE;
            if (visibility) {
                AnimUtils.slideUp(editCardView);
            } else {
                AnimUtils.slideDown(editCardView);
            }
            TransitionManager.beginDelayedTransition(cardView, new RotateUtils());
            expandIcon.setRotation(visibility ? 0 : 180);
        }

        public void bindData(final PelletProfileModel model) {
            String item = model.getBrand() + " " + model.getWood();
            pelletProfile.setText(item);
            pelletProfileId.setText(model.getId());
            pelletProfileBrand.setText(model.getBrand());
            pelletProfileWood.setText(model.getWood());
            pelletProfileRating.setText(StringUtils.getRatingText(model.getRating()));
            pelletProfileComments.setText(model.getComments());

        }
    }

    private void setPowerSpinnerMaxHeight(DefaultSpinnerAdapter adapter, RecyclerView recyclerView) {
        if (adapter != null) {
            if (adapter.getItemCount() > 6) {
                Display display = activity.getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = size.y / 4;
                recyclerView.setLayoutParams(params);
            }
        }
    }
}