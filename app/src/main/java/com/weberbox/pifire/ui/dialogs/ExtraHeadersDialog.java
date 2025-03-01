package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogHeadersEditBinding;
import com.weberbox.pifire.ui.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;

public class ExtraHeadersDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogHeadersCallback callback;
    private final Context context;
    private final String key, value;
    private final Integer position;
    private TextInputEditText headerKey, headerValue;

    public ExtraHeadersDialog(@NotNull Context context, @Nullable String key,
                              @Nullable String value, @Nullable Integer position,
                              @NotNull DialogHeadersCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.key = key;
        this.value = value;
        this.position = position;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogHeadersEditBinding binding = DialogHeadersEditBinding.inflate(inflater);

        MaterialButton saveButton = binding.saveButton;
        MaterialButton deleteButton = binding.deleteButton;
        TextInputLayout headerKeyLayout = binding.headersEditKey;
        TextInputLayout headerValueLayout = binding.headersEditValue;
        headerKey = binding.headersEditKeyTv;
        headerValue = binding.headersEditValueTv;

        headerKey.setText(key != null ? key : "");
        headerValue.setText(value != null ? value : "");

        if (key != null && value != null) {
            deleteButton.setVisibility(View.VISIBLE);
        } else {
            deleteButton.setVisibility(View.GONE);
            saveButton.setEnabled(false);
        }

        saveButton.setOnClickListener(v -> {
            Editable keyEdit = headerKey.getText();
            Editable valueEdit = headerValue.getText();
            if (keyEdit != null && valueEdit != null) {
                String key = keyEdit.toString();
                String value = valueEdit.toString();
                if (key.isEmpty()) {
                    headerKeyLayout.setError(context.getString(R.string.text_blank_error));
                }
                if (value.isEmpty()) {
                    headerValueLayout.setError(context.getString(R.string.text_blank_error));
                }
                if (!key.isEmpty() && !value.isEmpty()) {
                    callback.onDialogSave(key, value, position);
                    bottomSheetDialog.dismiss();
                }
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (position != null) {
                callback.onDialogDelete(position);
            }
            bottomSheetDialog.dismiss();
        });

        headerKey.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    saveButton.setEnabled(false);
                    headerKeyLayout.setError(context.getString(R.string.text_blank_error));
                } else {
                    if (headerValue.getText() != null &&
                            !headerValue.getText().toString().isEmpty()) {
                        saveButton.setEnabled(true);
                    }
                    headerKeyLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        headerValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    saveButton.setEnabled(false);
                    headerValueLayout.setError(context.getString(R.string.text_blank_error));
                } else {
                    if (headerKey.getText() != null &&
                            !headerKey.getText().toString().isEmpty()) {
                        saveButton.setEnabled(true);
                    }
                    headerValueLayout.setError(null);
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

        headerKey.requestFocus();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            if (bottomSheetDialog.getWindow() != null) {
                bottomSheetDialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
            }
        }

        return bottomSheetDialog;
    }

    public interface DialogHeadersCallback {
        void onDialogSave(String key, String value, Integer position);
        void onDialogDelete(int position);
    }
}
