package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPelletsAddBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogPelletsAddCallback;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.text.DecimalFormat;

public class PelletsAddDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogPelletsAddCallback callback;
    private final Context context;
    private final String title;
    private final String type;
    private final String initialValue;
    private final Integer maxLength;

    public PelletsAddDialog(Context context, @NonNull String title, @Nullable String type,
                            @Nullable String initialValue, @Nullable Integer maxLength,
                            DialogPelletsAddCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.title = title;
        this.type = type;
        this.initialValue = initialValue;
        this.maxLength = maxLength;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogPelletsAddBinding binding = DialogPelletsAddBinding.inflate(inflater);

        MaterialButton saveButton = binding.saveButton;
        MaterialButton cancelButton = binding.cancelButton;
        TextInputLayout inputLayout = binding.dialogPelletsAddTextLayout;
        TextInputEditText input = binding.dialogPelletsAddText;

        binding.dialogPelletsAddHeaderTitle.setText(title);

        if (initialValue != null) {
            input.setText(initialValue);
        } else {
            saveButton.setEnabled(false);
        }

        saveButton.setOnClickListener(v -> {
            Editable pelletsEdit = input.getText();
            if (pelletsEdit != null) {
                String pellets = pelletsEdit.toString();
                if (pellets.isEmpty()) {
                    saveButton.setEnabled(false);
                    inputLayout.setError(context.getString(R.string.text_blank_error));
                } else {
                    callback.onDialogConfirm(type, pellets);
                    bottomSheetDialog.dismiss();
                }
            }
        });

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    saveButton.setEnabled(false);
                    inputLayout.setError(context.getString(R.string.text_blank_error));
                } else if (maxLength != null && s.length() > maxLength) {
                    saveButton.setEnabled(false);
                    inputLayout.setError(context.getString(R.string.dialog_max_error,
                            new DecimalFormat("0.#").format(maxLength + 1)));
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
