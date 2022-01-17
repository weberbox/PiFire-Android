package com.weberbox.pifire.ui.preferences;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import androidx.preference.SwitchPreferenceCompat;

import com.weberbox.pifire.R;
import com.weberbox.pifire.application.PiFireApplication;
import com.weberbox.pifire.utils.AlertUtils;

import io.socket.client.Socket;

@SuppressWarnings("unused")
public class SwitchPreferenceCompatSocket extends SwitchPreferenceCompat {

    private Socket mSocket;
    private Context mContext;

    public SwitchPreferenceCompatSocket(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public SwitchPreferenceCompatSocket(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public SwitchPreferenceCompatSocket(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SwitchPreferenceCompatSocket(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        PiFireApplication app = (PiFireApplication) context.getApplicationContext();
        mSocket = app.getSocket();
        mContext = context;
    }

    @Override
    protected void onClick() {
        if (mSocket != null && mSocket.connected()) {
            super.onClick();
        } else {
            AlertUtils.createErrorAlert((Activity) mContext, R.string.prefs_not_connected, false);
        }
    }
}
