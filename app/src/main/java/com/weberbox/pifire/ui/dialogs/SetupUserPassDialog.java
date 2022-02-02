package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogUserPassBinding;
import com.weberbox.pifire.interfaces.AuthDialogCallback;
import com.weberbox.pifire.utils.SecurityUtils;

public class SetupUserPassDialog {

    private final LayoutInflater inflater;
    private final AlertDialog.Builder dialog;
    private final AuthDialogCallback callback;
    private final Context context;
    private EditText user;
    private EditText pass;
    private AlertDialog alertDialog;

    public SetupUserPassDialog(Context context, Fragment fragment) {
        dialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        inflater = LayoutInflater.from(context);
        this.context = context;
        callback = (AuthDialogCallback) fragment;
    }

    public AlertDialog showDialog() {
        DialogUserPassBinding binding = DialogUserPassBinding.inflate(inflater);

        dialog.setTitle(R.string.setup_server_auth_required);

        user = binding.dialogUserInputText;
        pass = binding.dialogPassInputText;

        dialog.setView(binding.getRoot());

        dialog.setPositiveButton(android.R.string.ok, (dialog, which) ->
                callback.onAuthDialogSave(saveCredentials()));

        dialog.setNegativeButton(android.R.string.cancel, (dialog, which) ->
                callback.onAuthDialogCancel());

        alertDialog = dialog.create();

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        user.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    user.setError(context.getString(R.string.settings_blank_error));
                } else {
                    user.setError(null);
                }
                togglePositiveButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    pass.setError(context.getString(R.string.settings_blank_error));
                } else {
                    pass.setError(null);
                }
                togglePositiveButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        user.requestFocus();

        return alertDialog;
    }

    private boolean saveCredentials() {
        String user = this.user.getText().toString();
        String pass = this.pass.getText().toString();
        if (!user.isEmpty() && !pass.isEmpty()) {
            return SecurityUtils.encrypt(context, R.string.prefs_server_basic_auth_user, user) &&
                    SecurityUtils.encrypt(context, R.string.prefs_server_basic_auth_password, pass);
        } else {
            return false;
        }
    }

    private void togglePositiveButton() {
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
                !user.getText().toString().isEmpty() && !pass.getText().toString().isEmpty());
    }
}
