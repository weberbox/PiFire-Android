package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogAppriseBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogAppriseCallback;
import com.weberbox.pifire.ui.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;

public class AppriseDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogAppriseCallback callback;
    private final Context context;
    private final String location;
    private final int title;
    private final boolean delete;
    private final Integer position;

    public AppriseDialog(@NotNull Context context, @StringRes int title, @Nullable Integer position,
                         @Nullable String location, boolean delete,
                         @NotNull DialogAppriseCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.title = title;
        this.position = position;
        this.location = location;
        this.delete = delete;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogAppriseBinding binding = DialogAppriseBinding.inflate(inflater);

        MaterialButton saveButton = binding.saveButton;
        MaterialButton cancelButton = binding.cancelButton;
        MaterialButton deleteButton = binding.deleteButton;
        TextInputLayout inputLayout = binding.dialogAppriseTextLayout;
        TextInputEditText input = binding.dialogAppriseText;

        binding.dialogAppriseHeaderTitle.setText(title);

        if (location != null) {
            input.setText(location);
        } else {
            saveButton.setEnabled(false);
        }

        deleteButton.setVisibility(delete ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(delete ? View.GONE : View.VISIBLE);

        saveButton.setOnClickListener(v -> {
            Editable locationEdit = input.getText();
            if (locationEdit != null) {
                String location = locationEdit.toString();
                if (location.isEmpty()) {
                    saveButton.setEnabled(false);
                    inputLayout.setError(context.getString(R.string.text_blank_error));
                } else {
                    if (position != null) {
                        callback.onDialogEdit(position, location);
                    } else {
                        callback.onDialogAdd(location);
                    }
                }
                bottomSheetDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        deleteButton.setOnClickListener(view -> {
            callback.onDialogDelete(position);
            bottomSheetDialog.dismiss();
        });

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    saveButton.setEnabled(false);
                    inputLayout.setError(context.getString(R.string.text_blank_error));
                } else {
                    saveButton.setEnabled(true);
                    inputLayout.setError(null);
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
}
