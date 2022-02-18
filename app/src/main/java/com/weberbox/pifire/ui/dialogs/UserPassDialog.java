package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.WindowManager;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogUserPassBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogAuthCallback;
import com.weberbox.pifire.utils.SecurityUtils;

public class UserPassDialog {

    private final LayoutInflater inflater;
    private final AlertDialog.Builder dialog;
    private final DialogAuthCallback callback;
    private final Context context;
    private final int title;
    private TextInputEditText userInput, passInput;

    public UserPassDialog(Context context, @StringRes int title, DialogAuthCallback callback) {
        dialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.title = title;
        this.callback = callback;
    }

    public AlertDialog showDialog() {
        DialogUserPassBinding binding = DialogUserPassBinding.inflate(inflater);

        dialog.setTitle(title);

        userInput = binding.dialogUserInputText;
        passInput = binding.dialogPassInputText;

        userInput.setText(SecurityUtils.decrypt(context, R.string.prefs_server_basic_auth_user));
        passInput.setText(SecurityUtils.decrypt(context, R.string.prefs_server_basic_auth_password));

        dialog.setView(binding.getRoot());

        dialog.setPositiveButton(android.R.string.ok, (dialog, which) ->
                callback.onAuthDialogSave(saveCredentials()));

        dialog.setNegativeButton(android.R.string.cancel, (dialog, which) ->
                callback.onAuthDialogCancel());

        AlertDialog alertDialog = dialog.create();

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        alertDialog.show();

        userInput.requestFocus();

        return alertDialog;
    }

    private boolean saveCredentials() {
        String user = userInput.getText() != null ? userInput.getText().toString() : "";
        String pass = passInput.getText() != null ? passInput.getText().toString() : "";
        return SecurityUtils.encrypt(context, R.string.prefs_server_basic_auth_user, user) &&
                SecurityUtils.encrypt(context, R.string.prefs_server_basic_auth_password, pass);
    }
}
