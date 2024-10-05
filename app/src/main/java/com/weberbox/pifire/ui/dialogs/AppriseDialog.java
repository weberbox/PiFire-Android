package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogInputTextBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogAppriseCallback;

public class AppriseDialog {

    private final LayoutInflater inflater;
    private final AlertDialog.Builder dialog;
    private final DialogAppriseCallback callback;
    private final Context context;
    private final String location;
    private final int title;
    private final Integer position;
    private String string;

    public AppriseDialog(Context context, @StringRes int title, Integer position,
                         String location, DialogAppriseCallback callback) {
        dialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.title = title;
        this.position = position;
        this.location = location;
        this.callback = callback;
    }

    public AlertDialog showDialog() {
        DialogInputTextBinding binding = DialogInputTextBinding.inflate(inflater);

        dialog.setTitle(title);

        final TextInputLayout inputLayout = binding.dialogTextInputLayout;
        final TextInputEditText input = binding.dialogTextInput;

        if (location != null) {
            input.setText(location);
        }

        dialog.setView(binding.getRoot());

        dialog.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            if (input.getText() != null) {
                string = input.getText().toString();
                if (!string.isEmpty()) {
                    if (position != null) {
                        callback.onDialogEdit(position, string);
                    } else {
                        callback.onDialogAdd(string);
                    }
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

        if (location == null) {
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
        }

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                    inputLayout.setError(context.getString(R.string.text_blank_error));
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
