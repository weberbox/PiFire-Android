package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPelletsAddBinding;
import com.weberbox.pifire.ui.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;

public class PelletsAddDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogPelletsAddCallback callback;
    private final Context context;
    private final String title;
    private final String type;
    private final String initialValue;
    private final Integer maxLength;

    public PelletsAddDialog(@NotNull Context context, @NotNull String title, @Nullable String type,
                            @Nullable String initialValue, @Nullable Integer maxLength,
                            @NotNull DialogPelletsAddCallback callback) {
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
            Editable editable = input.getText();
            if (editable != null) {
                String item = editable.toString();
                if (item.isEmpty()) {
                    saveButton.setEnabled(false);
                    inputLayout.setError(context.getString(R.string.text_blank_error));
                } else {
                    callback.onPelletItemAdded(item, type);
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

        input.requestFocus();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            if (bottomSheetDialog.getWindow() != null) {
                bottomSheetDialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
            }
        }

        return bottomSheetDialog;
    }

    public interface DialogPelletsAddCallback {
        void onPelletItemAdded(@NotNull String item, @Nullable String type);
    }
}
