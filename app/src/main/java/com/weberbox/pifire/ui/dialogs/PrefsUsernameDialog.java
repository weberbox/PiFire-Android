package com.weberbox.pifire.ui.dialogs;

import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.EditTextPreference;

import com.weberbox.pifire.R;
import com.weberbox.pifire.utils.SecurityUtils;

public class PrefsUsernameDialog extends EditTextPreference {
    private static final String TAG = PrefsUsernameDialog.class.getSimpleName();

    private Context mContext;

    public PrefsUsernameDialog(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public PrefsUsernameDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public PrefsUsernameDialog(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public String getText() {
        return SecurityUtils.decrypt(mContext, R.string.prefs_server_basic_auth_user);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        super.setText(restoreValue ? getPersistedString(null) : (String) defaultValue);
    }

    @Override
    public void setText(String text) {
        if (text.isEmpty()) {
            super.setText(null);
            return;
        }
        if (SecurityUtils.encrypt(mContext, R.string.prefs_server_basic_auth_user, text)) {
            super.setText("Encrypted");
        } else {
            super.setText("Error");
        }
    }
}
