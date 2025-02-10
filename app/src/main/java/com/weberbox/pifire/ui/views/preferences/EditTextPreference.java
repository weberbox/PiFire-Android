package com.weberbox.pifire.ui.views.preferences;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

import androidx.annotation.Nullable;

public class EditTextPreference extends androidx.preference.EditTextPreference {
    @Nullable
    private OnBindEditTextListener onBindEditTextListener;

    public EditTextPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public EditTextPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public EditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EditTextPreference(Context context) {
        super(context);
    }

    @Nullable
    public OnBindEditTextListener getOnBindEditTextListener() {
        return this.onBindEditTextListener;
    }

    @Override
    public void setOnBindEditTextListener(@Nullable OnBindEditTextListener onBindEditTextListener) {
        this.onBindEditTextListener = onBindEditTextListener;
    }

    @Deprecated
    public EditText getEditText() {
        throw new UnsupportedOperationException("Use OnBindEditTextListener to modify the EditText");
    }

    @Override
    public void setText(String text) {
        String oldText = getText();
        super.setText(text);
        if (!TextUtils.equals(text, oldText)) {
            notifyChanged();
        }
    }
}
