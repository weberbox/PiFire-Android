package com.weberbox.pifire.ui.views.preferences;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.utils.AlertUtils;

import io.socket.client.Socket;

@SuppressWarnings("unused")
public class EditTextPreferenceSocket extends EditTextPreference {

    private Socket socket;
    private Context context;

    public EditTextPreferenceSocket(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public EditTextPreferenceSocket(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public EditTextPreferenceSocket(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EditTextPreferenceSocket(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        PiFireApplication app = (PiFireApplication) context.getApplicationContext();
        socket = app.getSocket();
        this.context = context;
    }

    @Override
    protected void onClick() {
        if (socket != null && socket.connected()) {
            super.onClick();
        } else {
            AlertUtils.createErrorAlert((Activity) context, R.string.settings_error_offline, false);
        }
    }
}
