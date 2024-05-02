package com.weberbox.pifire.ui.dialogs;

import android.app.Activity;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPelletsEditBinding;
import com.weberbox.pifire.databinding.LayoutPelletsEditCardBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogPelletsProfileCallback;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.utils.StringUtils;

import java.util.List;

public class PelletsEditDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogPelletsProfileCallback callback;
    private final PelletProfileModel pelletProfile;
    private final Activity activity;
    private final List<String> brands, woods;
    private final int position;
    private AutoCompleteTextView profileBrandTv, profileWoodTv, profileRatingTv;
    private TextInputEditText profileComments;
    private TextInputLayout profileBrand, profileWood, profileRating;

    public PelletsEditDialog(Activity activity, List<String> brands, List<String> woods,
                             PelletProfileModel pelletProfile, int position,
                             DialogPelletsProfileCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(activity, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
        this.brands = brands;
        this.woods = woods;
        this.pelletProfile = pelletProfile;
        this.position = position;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogPelletsEditBinding binding = DialogPelletsEditBinding.inflate(inflater);

        LayoutPelletsEditCardBinding pelletsProfileEdit = binding.pelletsEditProfileContainer;

        AppCompatButton pelletEditSave = binding.pelletEditSave;
        AppCompatButton pelletEditLoad = binding.pelletEditLoad;
        AppCompatButton pelletEditDelete = binding.pelletEditDelete;
        profileBrandTv = binding.pelletsEditProfileContainer.pelletEditBrandTv;
        profileWoodTv = pelletsProfileEdit.pelletEditWoodTv;
        profileRatingTv = pelletsProfileEdit.pelletEditRatingTv;
        profileComments = pelletsProfileEdit.pelletEditCommentsTv;
        profileBrand = pelletsProfileEdit.pelletEditBrand;
        profileWood = pelletsProfileEdit.pelletEditWood;
        profileRating = pelletsProfileEdit.pelletEditRating;

        if (pelletProfile != null) {
            profileBrandTv.setText(pelletProfile.getBrand());
            profileWoodTv.setText(pelletProfile.getWood());
            profileRatingTv.setText(StringUtils.getRatingText(pelletProfile.getRating()));
            profileComments.setText(pelletProfile.getComments());
        }

        String[] ratings = activity.getResources().getStringArray(R.array.items_rating);

        ArrayAdapter<String> brandsAdapter = new ArrayAdapter<>(activity,
                R.layout.item_menu_popup, brands);

        ArrayAdapter<String> woodsAdapter = new ArrayAdapter<>(activity,
                R.layout.item_menu_popup, woods);

        ArrayAdapter<String> ratingsAdapter = new ArrayAdapter<>(activity,
                R.layout.item_menu_popup, ratings);

        profileBrandTv.setAdapter(brandsAdapter);
        profileWoodTv.setAdapter(woodsAdapter);
        profileRatingTv.setAdapter(ratingsAdapter);

        pelletEditDelete.setVisibility(pelletProfile != null ? View.VISIBLE : View.GONE);
        pelletEditLoad.setVisibility(pelletProfile != null ? View.GONE : View.VISIBLE);

        profileBrandTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    profileBrand.setError(activity.getString(R.string.text_blank_error));
                } else {
                    profileBrand.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        profileWoodTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    profileWood.setError(activity.getString(R.string.text_blank_error));
                } else {
                    profileWood.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        profileRatingTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    profileRating.setError(activity.getString(R.string.text_blank_error));
                } else {
                    profileRating.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        pelletEditSave.setOnClickListener(v -> {
            if (checkRequiredFields()) {
                if (pelletProfile != null) {
                    callback.onProfileEdit(buildProfile(pelletProfile), false);
                } else {
                    callback.onProfileAdd(buildProfile(null));
                }
                bottomSheetDialog.dismiss();
            }
        });

        pelletEditLoad.setOnClickListener(v -> {
            if (checkRequiredFields()) {
                callback.onProfileEdit(buildProfile(null), true);
                bottomSheetDialog.dismiss();
            }
        });

        pelletEditDelete.setOnClickListener(v -> {
            if (pelletProfile != null && position != -1) {
                callback.onProfileDelete(pelletProfile.getId(), position);
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(binding.getRoot());

        bottomSheetDialog.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        bottomSheetDialog.show();

        Configuration configuration = activity.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            if (bottomSheetDialog.getWindow() != null) {
                bottomSheetDialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
            }
        }

        return bottomSheetDialog;
    }

    private boolean checkRequiredFields() {
        if (profileBrandTv.getText().length() == 0) {
            profileBrand.setError(activity.getString(R.string.text_blank_error));
        } else if (profileWoodTv.getText().length() == 0) {
            profileWood.setError(activity.getString(R.string.text_blank_error));
        } else if (profileRatingTv.getText().length() == 0) {
            profileRating.setError(activity.getString(R.string.text_blank_error));
        } else {
            return true;
        }
        return false;
    }

    private PelletProfileModel buildProfile(PelletProfileModel profile) {
        return new PelletProfileModel(
                profileBrandTv.getText().toString(),
                profileWoodTv.getText().toString(),
                StringUtils.getRatingInt(profileRatingTv.getText().toString()),
                profileComments.getText() != null ? profileComments.getText().toString() : "",
                profile != null ? profile.getId() : "None");
    }
}
