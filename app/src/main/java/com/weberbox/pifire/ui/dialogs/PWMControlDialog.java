package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.textfield.TextInputEditText;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPwmControlBinding;
import com.weberbox.pifire.model.local.PWMControlModel;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogPWMCallback;
import com.weberbox.pifire.ui.utils.EmptyTextListener;

import java.util.List;

public class PWMControlDialog {

    private final LayoutInflater inflater;
    private final AlertDialog.Builder dialog;
    private final DialogPWMCallback callback;
    private final Context context;
    private final String units;
    private final int title;
    private final Integer minTemp, maxTemp, setTemp, dutyCycle, position;
    private final List<PWMControlModel> list;
    private TextInputEditText tempInput, dutyCycleInput;

    public PWMControlDialog(Context context, @StringRes int title, Integer position,
                            Integer minTemp, Integer maxTemp, Integer setTemp, Integer dutyCycle,
                            String units, List<PWMControlModel> list, DialogPWMCallback callback) {
        dialog = new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.title = title;
        this.position = position;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.setTemp = setTemp;
        this.dutyCycle = dutyCycle;
        this.units = units;
        this.list = list;
        this.callback = callback;
    }

    public AlertDialog showDialog() {
        DialogPwmControlBinding binding = DialogPwmControlBinding.inflate(inflater);

        dialog.setTitle(title);

        TextView dialogNote = binding.dialogPwmTempNote;
        tempInput = binding.dialogPwmTempText;
        dutyCycleInput = binding.dialogPwmDutyCycleText;

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

        dialog.setView(binding.getRoot());

        dialog.setPositiveButton(R.string.save, (dialog, which) -> {
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
        });

        dialog.setNegativeButton(android.R.string.cancel, (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = dialog.create();

        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        alertDialog.show();

        if (maxTemp == null && position != null) {
            dutyCycleInput.requestFocus();
        } else {
            tempInput.requestFocus();
        }

        return alertDialog;
    }
}
