package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogInputTextBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogInputTextCallback;

import java.text.DecimalFormat;

public class InputTextDialog {

    private final DialogInputTextCallback callback;
    private final LayoutInflater inflater;
    private final AlertDialog.Builder dialog;
    private final Context context;
    private final String title;
    private final String type;
    private final String initialValue;
    private final Integer maxLength;
    private String string;

    public InputTextDialog(Context context, @NonNull String title, @Nullable String type,
                           @Nullable String initialValue, @Nullable Integer maxLength,
                           DialogInputTextCallback callback) {
        dialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.title = title;
        this.type = type;
        this.initialValue = initialValue;
        this.maxLength = maxLength;
        this.callback = callback;
    }

    public AlertDialog showDialog() {
        DialogInputTextBinding binding = DialogInputTextBinding.inflate(inflater);

        dialog.setTitle(title);

        final TextInputLayout inputLayout = binding.dialogTextInputLayout;
        final TextInputEditText input = binding.dialogTextInput;

        if (initialValue != null) {
            input.setText(initialValue);
        }

        dialog.setView(binding.getRoot());

        dialog.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            if (input.getText() != null) {
                string = input.getText().toString();
                if (string.length() != 0) {
                    callback.onDialogConfirm(type, string);
                    dialog.dismiss();
                }
            }
        });

        dialog.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.cancel());

        final AlertDialog alertDialog = dialog.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(initialValue != null);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    inputLayout.setError(context.getString(R.string.text_blank_error));
                } else if (maxLength != null && s.length() > maxLength) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    inputLayout.setError(context.getString(R.string.dialog_max_error,
                            new DecimalFormat("0.#").format(maxLength + 1)));
                } else {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                    inputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        input.requestFocus();

        return alertDialog;
    }
}
