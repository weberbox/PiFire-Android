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

    private final LayoutInflater mInflater;
    private final AlertDialog.Builder mDialog;
    private final AuthDialogCallback mCallback;
    private final Context mContext;
    private EditText mUser;
    private EditText mPass;
    private AlertDialog mAlertDialog;

    public SetupUserPassDialog(Context context, Fragment fragment) {
        mDialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mCallback = (AuthDialogCallback) fragment;
    }

    public AlertDialog showDialog() {
        DialogUserPassBinding binding = DialogUserPassBinding.inflate(mInflater);

        mDialog.setTitle(R.string.setup_server_auth_required);

        mUser = binding.dialogUserInputText;
        mPass = binding.dialogPassInputText;

        mDialog.setView(binding.getRoot());

        mDialog.setPositiveButton(android.R.string.ok, (dialog, which) ->
                mCallback.onAuthDialogSave(saveCredentials()));

        mDialog.setNegativeButton(android.R.string.cancel, (dialog, which) ->
                mCallback.onAuthDialogCancel());

        mAlertDialog = mDialog.create();

        mAlertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        mAlertDialog.show();

        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        mUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    mUser.setError(mContext.getString(R.string.settings_blank_error));
                } else {
                    mUser.setError(null);
                }
                togglePositiveButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    mPass.setError(mContext.getString(R.string.settings_blank_error));
                } else {
                    mPass.setError(null);
                }
                togglePositiveButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mUser.requestFocus();

        return mAlertDialog;
    }

    private boolean saveCredentials() {
        String user = mUser.getText().toString();
        String pass = mPass.getText().toString();
        if (!user.isEmpty() && !pass.isEmpty()) {
            return SecurityUtils.encrypt(mContext, R.string.prefs_server_basic_auth_user, user) &&
                    SecurityUtils.encrypt(mContext, R.string.prefs_server_basic_auth_password, pass);
        } else {
            return false;
        }
    }

    private void togglePositiveButton() {
        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(
                !mUser.getText().toString().isEmpty() && !mPass.getText().toString().isEmpty());
    }
}
