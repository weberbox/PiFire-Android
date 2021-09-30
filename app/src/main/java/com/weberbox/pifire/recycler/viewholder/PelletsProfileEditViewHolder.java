package com.weberbox.pifire.recycler.viewholder;

import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.TransitionManager;

import com.skydoves.powerspinner.DefaultSpinnerAdapter;
import com.skydoves.powerspinner.OnSpinnerItemSelectedListener;
import com.skydoves.powerspinner.OnSpinnerOutsideTouchListener;
import com.skydoves.powerspinner.PowerSpinnerView;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.interfaces.PelletsCallbackInterface;
import com.weberbox.pifire.model.PelletProfileModel;
import com.weberbox.pifire.ui.utils.RotateUtils;
import com.weberbox.pifire.utils.Log;
import com.weberbox.pifire.utils.StringUtils;

import java.util.List;


public class PelletsProfileEditViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = PelletsProfileEditViewHolder.class.getSimpleName();

    private final TextView mPelletProfile;
    private final TextView mPelletProfileId;
    private final PowerSpinnerView mPelletProfileBrand;
    private final PowerSpinnerView mPelletProfileWood;
    private final PowerSpinnerView mPelletProfileRating;
    private final TextView mPelletProfileComments;
    private final ImageView mExpandIcon;
    private final CardView mCardView;
    private final LinearLayout mEditCardView;


    public PelletsProfileEditViewHolder(final View itemView, PelletsCallbackInterface callback,
                                        List<String> brands, List<String> woods) {
        super(itemView);

        mPelletProfile = itemView.findViewById(R.id.pellets_item);
        mPelletProfileId = itemView.findViewById(R.id.pellets_item_id);
        ImageView pelletProfileDelete = itemView.findViewById(R.id.pellets_item_delete);

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

        pelletProfileDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemDelete(Constants.PELLET_PROFILE,
                        mPelletProfileId.getText().toString(), getAbsoluteAdapterPosition());
            }
        });

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCardView();
            }
        });

        mPelletProfileBrand.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {
                mPelletProfileBrand.dismiss();
            }
        });

        mPelletProfileWood.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {
                mPelletProfileWood.dismiss();
            }
        });

        mPelletProfileRating.setSpinnerOutsideTouchListener(new OnSpinnerOutsideTouchListener() {
            @Override
            public void onSpinnerOutsideTouch(@NonNull View view, @NonNull MotionEvent motionEvent) {
                mPelletProfileRating.dismiss();
            }
        });

        mPelletProfileBrand.setOnSpinnerItemSelectedListener(
                new OnSpinnerItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(int oldIndex, @Nullable String oldItem,
                                               int newIndex, String newItem) {
                        Log.d(TAG, "New Item " + newItem);
                    }
                });

        mPelletProfileWood.setOnSpinnerItemSelectedListener(
                new OnSpinnerItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(int oldIndex, @Nullable String oldItem,
                                               int newIndex, String newItem) {
                        Log.d(TAG, "New Item " + newItem);
                    }
                });

        mPelletProfileRating.setOnSpinnerItemSelectedListener(
                new OnSpinnerItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(int oldIndex, @Nullable String oldItem,
                                               int newIndex, String newItem) {
                        Log.d(TAG, "New Item " + newItem);
                    }
                });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPelletProfileId.getText() != null) {
                    callback.onProfileDelete(mPelletProfileId.getText().toString(),
                            getAbsoluteAdapterPosition());
                }
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCardView();
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
            }
        });

        DefaultSpinnerAdapter brandsSpinnerAdapter = new DefaultSpinnerAdapter(mPelletProfileBrand);
        mPelletProfileBrand.setSpinnerAdapter(brandsSpinnerAdapter);
        mPelletProfileBrand.setItems(brands);

        DefaultSpinnerAdapter woodsSpinnerAdapter = new DefaultSpinnerAdapter(mPelletProfileWood);
        mPelletProfileWood.setSpinnerAdapter(woodsSpinnerAdapter);
        mPelletProfileWood.setItems(woods);

    }

    private void toggleCardView() {
        boolean visibility = mEditCardView.getVisibility() == View.VISIBLE;
        mEditCardView.setVisibility(visibility ? View.GONE : View.VISIBLE);
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
