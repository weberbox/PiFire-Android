package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Outline;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPwmControlBinding;
import com.weberbox.pifire.record.PWMControlRecord;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.ui.utils.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dev.chrisbanes.insetter.Insetter;
import dev.chrisbanes.insetter.Side;

public class PWMControlDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogPWMCallback callback;
    private final Context context;
    private final String units;
    private final int title;
    private final boolean delete;
    private final Integer minTemp, maxTemp, setTemp, dutyCycle, position;
    private final List<PWMControlRecord> list;
    private TextInputEditText tempInput, dutyCycleInput;

    public PWMControlDialog(@NotNull Context context, @StringRes int title, Integer position,
                            @Nullable Integer minTemp, @Nullable Integer maxTemp,
                            @NotNull Integer setTemp, @NotNull Integer dutyCycle,
                            @NotNull String units, boolean delete,
                            @NotNull List<PWMControlRecord> list,
                            @NotNull DialogPWMCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.title = title;
        this.position = position;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.setTemp = setTemp;
        this.dutyCycle = dutyCycle;
        this.units = units;
        this.delete = delete;
        this.list = list;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogPwmControlBinding binding = DialogPwmControlBinding.inflate(inflater);

        TextView dialogNote = binding.dialogPwmTempNote;
        tempInput = binding.dialogPwmTempText;
        dutyCycleInput = binding.dialogPwmDutyCycleText;
        binding.dialogPwmCardHeaderTitle.setText(title);

        String note;
        if (minTemp == null) {
            String temp = String.valueOf(setTemp - 1);
            note = String.format(context.getResources().getString(
                    R.string.settings_pwm_temp_range_note_max), temp, units);
            tempInput.addTextChangedListener(
                    new EmptyTextListener(context, (double) setTemp, 100.0, tempInput));
        } else if (maxTemp == null) {
            note = context.getString(R.string.settings_pwm_temp_range_note_last);
            tempInput.setEnabled(false);
        } else {
            String minTemp = this.minTemp.toString();
            String maxTemp = this.maxTemp.toString();
            note = String.format(context.getResources().getString(
                    R.string.settings_pwm_temp_range_note_range), minTemp, maxTemp, units);
            tempInput.addTextChangedListener(
                    new EmptyTextListener(context, (double) this.minTemp, (double) this.maxTemp,
                            tempInput));
        }

        Double minDutyCycle = Double.valueOf(Prefs.getString(
                context.getString(R.string.prefs_pwm_min_duty_cycle)));

        dutyCycleInput.addTextChangedListener(new EmptyTextListener(context, minDutyCycle, 100.0,
                dutyCycleInput));

        dialogNote.setText(note);

        tempInput.setText(String.valueOf(setTemp));
        dutyCycleInput.setText(String.valueOf(dutyCycle));

        MaterialButton saveButton = binding.saveButton;
        MaterialButton cancelButton = binding.cancelButton;
        MaterialButton deleteButton = binding.deleteButton;

        saveButton.setEnabled(tempInput != null);
        saveButton.setEnabled(dutyCycleInput != null);

        deleteButton.setVisibility(delete ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(delete ? View.GONE : View.VISIBLE);

        saveButton.setOnClickListener(v -> {
            if (tempInput.getText() != null && dutyCycleInput.getText() != null) {
                Integer temp = Integer.valueOf(tempInput.getText().toString());
                Integer dutyCycle = Integer.valueOf(dutyCycleInput.getText().toString());
                if (position != null) {
                    if (position == list.size() - 1) {
                        callback.onDialogEdit(list, position, (temp + 1), dutyCycle);
                    } else {
                        callback.onDialogEdit(list, position, temp, dutyCycle);
                    }
                } else {
                    callback.onDialogAdd(list, temp, dutyCycle);
                }
            }
            bottomSheetDialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        deleteButton.setOnClickListener(view -> {
            callback.onDialogDelete(position);
            bottomSheetDialog.dismiss();
        });

        tempInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    binding.dialogPwmTemp.setError(context.getString(R.string.text_blank_error));
                } else {
                    binding.dialogPwmTemp.setError(null);
                }
                saveButton.setEnabled(s.length() != 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        dutyCycleInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence s, int i, int i1, int i2) {
                if (s.length() == 0) {
                    binding.dialogPwmDutyCycle.setError(context.getString(R.string.text_blank_error));
                } else {
                    binding.dialogPwmDutyCycle.setError(null);
                }
                saveButton.setEnabled(s.length() != 0);
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

        if (tempInput.isEnabled()) {
            tempInput.requestFocus();
        } else {
            dutyCycleInput.requestFocus();
        }

        tempInput.requestFocus();

        Configuration configuration = context.getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE &&
                configuration.screenWidthDp > 450) {
            if (bottomSheetDialog.getWindow() != null) {
                bottomSheetDialog.getWindow().setLayout(ViewUtils.dpToPx(450), -1);
            }
        }

        return bottomSheetDialog;
    }

    public interface DialogPWMCallback {
        void onDialogAdd(List<PWMControlRecord> list, Integer temp, Integer dutyCycle);
        void onDialogEdit(List<PWMControlRecord> list, int position, Integer temp, Integer dutyCycle);
        void onDialogDelete(int position);
    }
}
