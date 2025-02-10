package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.view.LayoutInflater;

import androidx.preference.Preference;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.weberbox.pifire.R;
import com.weberbox.pifire.databinding.DialogPrefsEditBinding;
import com.weberbox.pifire.ui.utils.ViewUtils;
import com.weberbox.pifire.ui.views.preferences.EditTextPreference;

public class PrefsEditDialog {

    private final BottomSheetDialog bottomSheetDialog;
    private final LayoutInflater inflater;
    private final Context context;
    private final Preference preference;

    public PrefsEditDialog(Context context, Preference preference) {
        bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogFloating);
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.preference = preference;
    }

    public BottomSheetDialog showDialog() {
        DialogPrefsEditBinding binding = DialogPrefsEditBinding.inflate(inflater);

        MaterialButton saveButton = binding.saveButton;
        MaterialButton cancelButton = binding.cancelButton;
        TextInputEditText input = binding.dialogPrefsEditText;

        EditTextPreference editTextPreference = ((EditTextPreference) preference);

        if (editTextPreference.getOnBindEditTextListener() != null) {
            editTextPreference.getOnBindEditTextListener().onBindEditText(input);
        }

        String title = "";

        if (preference.getTitle() != null) {
            title = preference.getTitle().toString();
        }

        binding.dialogPrefsEditHeaderTitle.setText(title);

        input.setText(((EditTextPreference) preference).getText());

        saveButton.setOnClickListener(v -> {
            Editable prefsEdit = input.getText();
            if (prefsEdit != null) {
                ((EditTextPreference) preference).setText(input.getText().toString());
                bottomSheetDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

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
