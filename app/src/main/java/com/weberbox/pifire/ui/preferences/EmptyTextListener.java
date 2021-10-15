package com.weberbox.pifire.ui.preferences;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.weberbox.pifire.R;

public class EmptyTextListener implements TextWatcher {

    private final EditText mEditText;
    private final Context mContext;

    public EmptyTextListener(Context context, EditText target) {
        mContext = context;
        mEditText = target;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (s.length() == 0) {
            mEditText.setError(mContext.getString(R.string.settings_blank_error));
        } else if (s.toString().equals("0")) {
            mEditText.setError(mContext.getString(R.string.settings_zero_error));
        } else {
            mEditText.setError(null);
        }
    }
}
