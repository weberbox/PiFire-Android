package com.weberbox.pifire.recycler.viewholder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.skydoves.powerspinner.DefaultSpinnerAdapter;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;
import com.weberbox.pifire.model.PelletProfileModel;
import com.weberbox.pifire.ui.utils.AnimUtils;
import com.weberbox.pifire.ui.utils.RotateUtils;
import com.weberbox.pifire.utils.StringUtils;

import java.util.List;

import timber.log.Timber;


public class PelletsProfileEditViewHolder extends RecyclerView.ViewHolder {

    private final TextView mPelletProfile;
    private final TextView mPelletProfileId;
    private final PowerSpinnerView mPelletProfileBrand;
    private final PowerSpinnerView mPelletProfileWood;
    private final PowerSpinnerView mPelletProfileRating;
    private final TextView mPelletProfileComments;
    private final ImageView mExpandIcon;
    private final ImageView mDeleteIcon;
    private final CardView mCardView;
    private final LinearLayout mEditCardView;


    public PelletsProfileEditViewHolder(final View itemView, PelletsCallbackInterface callback,
                                        List<String> brands, List<String> woods) {
        super(itemView);

        mPelletProfile = itemView.findViewById(R.id.pellets_item);
        mPelletProfileId = itemView.findViewById(R.id.pellets_item_id);
        mDeleteIcon = itemView.findViewById(R.id.pellets_item_delete);

        View profileView = itemView.findViewById(R.id.pellet_edit_card_header);
        mPelletProfileBrand = itemView.findViewById(R.id.pellet_edit_brand_text);
        mPelletProfileWood = itemView.findViewById(R.id.pellet_edit_wood_text);
        mPelletProfileRating = itemView.findViewById(R.id.pellet_edit_rating_text);
        mPelletProfileComments = itemView.findViewById(R.id.pellet_edit_comments_text);
        mExpandIcon = itemView.findViewById(R.id.pellet_edit_expand_icon);
        mCardView = itemView.findViewById(R.id.pellet_edit_card_view);
        mEditCardView = itemView.findViewById(R.id.pellet_edit_card_container);
        AppCompatButton deleteButton = itemView.findViewById(R.id.pellet_edit_delete);
        AppCompatButton saveButton = itemView.findViewById(R.id.pellet_edit_save);

        mDeleteIcon.setOnClickListener(view -> callback.onItemDelete(Constants.PELLET_PROFILE,
                mPelletProfileId.getText().toString(), getAbsoluteAdapterPosition()));

        profileView.setOnClickListener(view -> {
            AnimUtils.fadeAnimation(mDeleteIcon, 300, mDeleteIcon.getVisibility() ==
                    View.VISIBLE ? Constants.FADE_OUT : Constants.FADE_IN);
            toggleCardView();
        });

        mPelletProfileRating.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);

        mPelletProfileBrand.setSpinnerOutsideTouchListener((view, motionEvent) ->
                mPelletProfileBrand.dismiss());

        mPelletProfileWood.setSpinnerOutsideTouchListener((view, motionEvent) ->
                mPelletProfileWood.dismiss());

        mPelletProfileRating.setSpinnerOutsideTouchListener((view, motionEvent) ->
                mPelletProfileRating.dismiss());

        mPelletProfileBrand.setOnSpinnerItemSelectedListener(
                (OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) ->
                        Timber.d("New Item %s", newItem));

        mPelletProfileWood.setOnSpinnerItemSelectedListener(
                (OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) ->
                        Timber.d("New Item %s", newItem));

        mPelletProfileRating.setOnSpinnerItemSelectedListener(
                (OnSpinnerItemSelectedListener<String>) (oldIndex, oldItem, newIndex, newItem) ->
                        Timber.d("New Item %s", newItem));

        deleteButton.setOnClickListener(view -> {
            toggleCardView();
            mDeleteIcon.setVisibility(View.VISIBLE);
            if (mPelletProfileId.getText() != null) {
                callback.onProfileDelete(mPelletProfileId.getText().toString(),
                        getAbsoluteAdapterPosition());
            }
        });

        saveButton.setOnClickListener(view -> {
            toggleCardView();
            mDeleteIcon.setVisibility(View.VISIBLE);
            if (mPelletProfileId.getText() != null) {
                callback.onProfileEdit(
                        new PelletProfileModel(
                                mPelletProfileBrand.getText().toString(),
                                mPelletProfileWood.getText().toString(),
                                StringUtils.getRatingInt(mPelletProfileRating.getText().toString()),
                                mPelletProfileComments.getText().toString(),
                                mPelletProfileId.getText().toString()
                        )
                );
            }
        });

        DefaultSpinnerAdapter brandsSpinnerAdapter = new DefaultSpinnerAdapter(mPelletProfileBrand);
        mPelletProfileBrand.setSpinnerAdapter(brandsSpinnerAdapter);
        mPelletProfileBrand.setItems(brands);
        mPelletProfileBrand.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);
        setPowerSpinnerMaxHeight(brandsSpinnerAdapter, mPelletProfileBrand.getSpinnerRecyclerView());

        DefaultSpinnerAdapter woodsSpinnerAdapter = new DefaultSpinnerAdapter(mPelletProfileWood);
        mPelletProfileWood.setSpinnerAdapter(woodsSpinnerAdapter);
        mPelletProfileWood.setItems(woods);
        mPelletProfileWood.getSpinnerRecyclerView().setVerticalScrollBarEnabled(false);
        setPowerSpinnerMaxHeight(woodsSpinnerAdapter, mPelletProfileWood.getSpinnerRecyclerView());

    }

    private void setPowerSpinnerMaxHeight(DefaultSpinnerAdapter adapter, RecyclerView recyclerView) {
        if (adapter != null) {
            if (adapter.getItemCount() > 6) {
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = 780;
                recyclerView.setLayoutParams(params);
            }
        }
    }

    private void toggleCardView() {
        boolean visibility = mEditCardView.getVisibility() == View.VISIBLE;
        if (visibility) {
            AnimUtils.slideUp(mEditCardView);
        } else {
            AnimUtils.slideDown(mEditCardView);
        }
        TransitionManager.beginDelayedTransition(mCardView, new RotateUtils());
        mExpandIcon.setRotation(visibility ? 0 : 180);
    }

    public void bindData(final PelletProfileModel viewModel) {
        String item = viewModel.getBrand() + " " + viewModel.getWood();
        mPelletProfile.setText(item);
        mPelletProfileId.setText(viewModel.getId());
        mPelletProfileBrand.setText(viewModel.getBrand());
        mPelletProfileWood.setText(viewModel.getWood());
        mPelletProfileRating.setText(StringUtils.getRatingText(viewModel.getRating()));
        mPelletProfileComments.setText(viewModel.getComments());

    }
}
