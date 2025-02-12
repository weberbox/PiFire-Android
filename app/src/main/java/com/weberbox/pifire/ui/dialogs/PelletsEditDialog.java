package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPelletsEditBinding;
import com.weberbox.pifire.databinding.LayoutPelletsEditCardBinding;
import com.weberbox.pifire.model.remote.PelletDataModel.PelletProfileModel;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogPelletsProfileCallback;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.utils.StringUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;

public class PelletsEditDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogPelletsProfileCallback callback;
    private final PelletProfileModel pelletProfile;
    private final Context context;
    private final List<String> brands, woods;
    private final int position;
    private AutoCompleteTextView profileBrandTv, profileWoodTv, profileRatingTv;
    private TextInputEditText profileComments;
    private TextInputLayout profileBrand, profileWood, profileRating;

    public PelletsEditDialog(@NotNull Context context, @NotNull List<String> brands,
                             @NotNull List<String> woods, @Nullable PelletProfileModel pelletProfile,
                             int position, @NotNull DialogPelletsProfileCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.brands = brands;
        this.woods = woods;
        this.pelletProfile = pelletProfile;
        this.position = position;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogPelletsEditBinding binding = DialogPelletsEditBinding.inflate(inflater);

        LayoutPelletsEditCardBinding pelletsProfileEdit = binding.pelletsEditProfileContainer;

        MaterialButton pelletEditSave = binding.saveButton;
        MaterialButton pelletEditLoad = binding.saveLoadButton;
        MaterialButton pelletEditDelete = binding.deleteButton;
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
        } else {
            pelletEditSave.setEnabled(false);
            pelletEditLoad.setEnabled(false);
        }

        String[] ratings = context.getResources().getStringArray(R.array.items_rating);

        ArrayAdapter<String> brandsAdapter = new ArrayAdapter<>(context,
                R.layout.item_menu_popup, brands);

        ArrayAdapter<String> woodsAdapter = new ArrayAdapter<>(context,
                R.layout.item_menu_popup, woods);

        ArrayAdapter<String> ratingsAdapter = new ArrayAdapter<>(context,
                R.layout.item_menu_popup, ratings);

        profileBrandTv.setAdapter(brandsAdapter);
        profileWoodTv.setAdapter(woodsAdapter);
        profileRatingTv.setAdapter(ratingsAdapter);

        pelletEditDelete.setVisibility(pelletProfile != null ? View.VISIBLE : View.GONE);
        pelletEditLoad.setVisibility(pelletProfile != null ? View.GONE : View.VISIBLE);

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

        profileBrandTv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    pelletEditSave.setEnabled(false);
                    pelletEditLoad.setEnabled(false);
                    profileBrand.setError(context.getString(R.string.text_blank_error));
                } else {
                    if (profileWoodTv.getText() != null &&
                            !profileWoodTv.getText().toString().isEmpty() &&
                            profileRatingTv.getText() != null &&
                            !profileRatingTv.getText().toString().isEmpty()) {
                        pelletEditSave.setEnabled(true);
                        pelletEditLoad.setEnabled(true);
                    }
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
                    pelletEditSave.setEnabled(false);
                    profileWood.setError(context.getString(R.string.text_blank_error));
                } else {
                    if (profileBrandTv.getText() != null &&
                            !profileBrandTv.getText().toString().isEmpty() &&
                            profileRatingTv.getText() != null &&
                            !profileRatingTv.getText().toString().isEmpty()) {
                        pelletEditSave.setEnabled(true);
                        pelletEditLoad.setEnabled(true);
                    }
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
                    pelletEditSave.setEnabled(false);
                    profileRating.setError(context.getString(R.string.text_blank_error));
                } else {
                    if (profileBrandTv.getText() != null &&
                            !profileBrandTv.getText().toString().isEmpty() &&
                            profileWoodTv.getText() != null &&
                            !profileWoodTv.getText().toString().isEmpty()) {
                        pelletEditSave.setEnabled(true);
                        pelletEditLoad.setEnabled(true);
                    }
                    profileRating.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        bottomSheetDialog.setContentView(binding.getRoot());

        bottomSheetDialog.setOnShowListener(dialog -> {
            @SuppressWarnings("rawtypes")
            BottomSheetBehavior bottomSheetBehavior = ((BottomSheetDialog) dialog).getBehavior();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.getRoot().setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                float radius = context.getResources().getDimension(R.dimen.radiusTop);
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight() +
                        (int) radius, radius);
            }
        });
        binding.getRoot().setClipToOutline(true);

        binding.getRoot().setBackgroundColor(ContextCompat.getColor(context,
                R.color.material_dialog_background));

        Insetter.builder()
                .margin(WindowInsetsCompat.Type.systemBars() |
                        WindowInsetsCompat.Type.ime(), Side.BOTTOM)
                .applyToView(binding.dialogContainer);

        bottomSheetDialog.show();

        Configuration configuration = context.getResources().getConfiguration();
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
            profileBrand.setError(context.getString(R.string.text_blank_error));
        } else if (profileWoodTv.getText().length() == 0) {
            profileWood.setError(context.getString(R.string.text_blank_error));
        } else if (profileRatingTv.getText().length() == 0) {
            profileRating.setError(context.getString(R.string.text_blank_error));
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
