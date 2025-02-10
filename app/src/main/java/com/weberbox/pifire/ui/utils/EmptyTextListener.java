package com.weberbox.pifire.ui.utils;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.weberbox.pifire.R;

import java.text.DecimalFormat;

public class EmptyTextListener implements TextWatcher {

    private TextInputLayout textInputLayout;
    private final EditText editText;
    private final Context context;
    private final Double min;
    private final Double max;

    public EmptyTextListener(Context context, Double min, Double max, EditText editText) {
        this.context = context;
        this.editText = editText;
        this.min = min;
        this.max = max;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        Button positiveButton = editText.getRootView().findViewById(android.R.id.button1);
        MaterialButton saveButton = editText.getRootView().findViewById(R.id.save_button);
        textInputLayout = (TextInputLayout) editText.findViewById(
                editText.getId()).getParent().getParent();
        if (s.length() == 0) {
            if (positiveButton != null) positiveButton.setEnabled(false);
            if (saveButton != null) saveButton.setEnabled(false);
            setError(context.getString(R.string.text_blank_error));
        } else if (min != null && Double.parseDouble(s.toString()) < min) {
            if (positiveButton != null) positiveButton.setEnabled(false);
            if (saveButton != null) saveButton.setEnabled(false);
            setError(context.getString(R.string.settings_min_error,
                    new DecimalFormat("0.#").format(min - 1)));
        } else if (max != null && Double.parseDouble(s.toString()) > max) {
            if (positiveButton != null) positiveButton.setEnabled(false);
            if (saveButton != null) saveButton.setEnabled(false);
            setError(context.getString(R.string.settings_max_error,
                    new DecimalFormat("0.#").format(max + 1)));
        } else {
            if (positiveButton != null) positiveButton.setEnabled(true);
            if (saveButton != null) saveButton.setEnabled(true);
            setError(null);
        }
    }

    private void setError(String error) {
        if (textInputLayout != null) {
            textInputLayout.setError(error);
        } else {
            editText.setError(error);
        }
    }
}
