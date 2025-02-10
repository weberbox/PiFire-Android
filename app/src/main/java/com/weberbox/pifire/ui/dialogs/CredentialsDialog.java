package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogCredentialsBinding;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogAuthCallback;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.utils.SecurityUtils;

public class CredentialsDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogAuthCallback callback;
    private final Context context;
    private TextInputEditText userInput, passInput;

    public CredentialsDialog(Context context, DialogAuthCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogCredentialsBinding binding = DialogCredentialsBinding.inflate(inflater);

        MaterialButton saveButton = binding.saveButton;
        MaterialButton deleteButton = binding.deleteButton;
        userInput = binding.dialogUserInputTv;
        passInput = binding.dialogPassInputTv;

        userInput.setText(SecurityUtils.decrypt(context, R.string.prefs_server_basic_auth_user));
        passInput.setText(SecurityUtils.decrypt(context, R.string.prefs_server_basic_auth_password));

        saveButton.setOnClickListener(v -> {
            callback.onAuthDialogSave(saveCredentials(
                    userInput.getText() != null ? userInput.getText().toString() : "",
                    passInput.getText() != null ? passInput.getText().toString() : ""));
            bottomSheetDialog.dismiss();
        });

        deleteButton.setOnClickListener(v -> {
            saveCredentials("", "");
            bottomSheetDialog.dismiss();
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

    private boolean saveCredentials(String user, String pass) {
        return SecurityUtils.encrypt(context, R.string.prefs_server_basic_auth_user, user) &&
                SecurityUtils.encrypt(context, R.string.prefs_server_basic_auth_password, pass);
    }
}
