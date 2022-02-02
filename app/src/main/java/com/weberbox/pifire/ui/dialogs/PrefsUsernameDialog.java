package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.EditTextPreference;

import com.weberbox.pifire.R;
import com.weberbox.pifire.utils.SecurityUtils;

public class PrefsUsernameDialog extends EditTextPreference {

    private final Context context;

    @SuppressWarnings("unused")
    public PrefsUsernameDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
    }

    @SuppressWarnings("unused")
    public PrefsUsernameDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @SuppressWarnings("unused")
    public PrefsUsernameDialog(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public String getText() {
        return SecurityUtils.decrypt(context, R.string.prefs_server_basic_auth_user);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.setText(restoreValue ? getPersistedString(null) : (String) defaultValue);
    }

    @Override
    public void setText(String text) {
        if (text != null && text.isEmpty()) {
            super.setText(null);
            return;
        }
        if (SecurityUtils.encrypt(context, R.string.prefs_server_basic_auth_user, text)) {
            super.setText("Encrypted");
        } else {
            super.setText("Error");
        }
    }
}
