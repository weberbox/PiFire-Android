package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogCredentialsBinding;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.utils.SecurityUtils;

import org.jetbrains.annotations.NotNull;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;

public class CredentialsDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogAuthCallback callback;
    private final Context context;
    private TextInputEditText userInput, passInput;

    public CredentialsDialog(@NotNull Context context, @NotNull DialogAuthCallback callback) {
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

        userInput.setText(SecurityUtils.getUsername(context));
        passInput.setText(SecurityUtils.getPassword(context));

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

        bottomSheetDialog.setOnCancelListener(dialogInterface -> callback.onAuthDialogCancel());

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

        userInput.requestFocus();

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
        return SecurityUtils.setUsername(context, user) && SecurityUtils.setPassword(context, pass);
    }

    public interface DialogAuthCallback {
        void onAuthDialogSave(boolean success);
        void onAuthDialogCancel();
    }
}
