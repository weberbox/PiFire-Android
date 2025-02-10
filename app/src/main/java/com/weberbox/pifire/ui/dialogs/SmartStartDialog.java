package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogSmartStartBinding;
import com.weberbox.pifire.model.local.SmartStartModel;
import com.weberbox.pifire.ui.dialogs.interfaces.DialogSmartStartCallback;
import com.weberbox.pifire.ui.utils.EmptyTextListener;
import com.weberbox.pifire.ui.utils.ViewUtils;

import java.util.List;

public class SmartStartDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final DialogSmartStartCallback callback;
    private final Context context;
    private final String units;
    private final int title;
    private final boolean delete;
    private final Integer minTemp, maxTemp, setTemp, startUp, augerOn, pMode, position;
    private final List<SmartStartModel> list;
    private TextInputEditText tempInput, startUpInput, augerOnInput, pModeInput;

    public SmartStartDialog(Context context, @StringRes int title, Integer position,
                            Integer minTemp, Integer maxTemp, Integer setTemp, Integer startUp,
                            Integer augerOn, Integer pMode, String units, boolean delete,
                            List<SmartStartModel> list, DialogSmartStartCallback callback) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.title = title;
        this.position = position;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.setTemp = setTemp;
        this.startUp = startUp;
        this.augerOn = augerOn;
        this.pMode = pMode;
        this.units = units;
        this.delete = delete;
        this.list = list;
        this.callback = callback;
    }

    public BottomSheetDialog showDialog() {
        DialogSmartStartBinding binding = DialogSmartStartBinding.inflate(inflater);

        TextView dialogNote = binding.dialogSmartStartTempNote;
        tempInput = binding.dialogSmartStartTempText;
        startUpInput = binding.dialogSmartStartStartUpText;
        augerOnInput = binding.dialogSmartStartAugerOnText;
        pModeInput = binding.dialogSmartStartPModeText;
        binding.dialogSmartStartHeaderTitle.setText(title);

        String note;
        if (minTemp == null) {
            String temp = String.valueOf(setTemp - 1);
            note = String.format(context.getResources().getString(
                    R.string.settings_pwm_temp_range_note_max), temp, units);
            tempInput.addTextChangedListener(
                    new EmptyTextListener(context, (double) setTemp, 1000.0, tempInput));
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

        startUpInput.addTextChangedListener(new EmptyTextListener(context, 30.0, null,
                startUpInput));

        augerOnInput.addTextChangedListener(new EmptyTextListener(context, 1.0, 1000.0,
                augerOnInput));

        pModeInput.addTextChangedListener(new EmptyTextListener(context, 0.0, 9.0,
                pModeInput));

        dialogNote.setText(note);
        tempInput.setText(String.valueOf(setTemp));
        startUpInput.setText(String.valueOf(startUp));
        augerOnInput.setText((String.valueOf(augerOn)));
        pModeInput.setText((String.valueOf(pMode)));

        MaterialButton saveButton = binding.saveButton;
        MaterialButton cancelButton = binding.cancelButton;
        MaterialButton deleteButton = binding.deleteButton;

        saveButton.setEnabled(tempInput != null);
        saveButton.setEnabled(startUpInput != null);
        saveButton.setEnabled(augerOnInput != null);
        saveButton.setEnabled(pModeInput != null);

        deleteButton.setVisibility(delete ? View.VISIBLE : View.GONE);
        cancelButton.setVisibility(delete ? View.GONE : View.VISIBLE);

        saveButton.setOnClickListener(v -> {
            if (tempInput.getText() != null && startUpInput.getText() != null &&
                    augerOnInput.getText() != null && pModeInput.getText() != null) {
                Integer temp = Integer.valueOf(tempInput.getText().toString());
                Integer start = Integer.valueOf(startUpInput.getText().toString());
                Integer auger = Integer.valueOf(augerOnInput.getText().toString());
                Integer pMode = Integer.valueOf(pModeInput.getText().toString());
                if (position != null) {
                    if (position == list.size() - 1) {
                        callback.onDialogEdit(list, position, (temp + 1), start, auger, pMode);
                    } else {
                        callback.onDialogEdit(list, position, temp, start, auger, pMode);
                    }
                } else {
                    callback.onDialogAdd(list, temp, start, auger, pMode);
                }
            }
            bottomSheetDialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        deleteButton.setOnClickListener(view -> {
            callback.onDialogDelete(position);
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
}
