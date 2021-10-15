package com.weberbox.pifire.updater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

public class ForceUpdateKeyListener implements DialogInterface.OnKeyListener {

    private final Context mContext;

    public ForceUpdateKeyListener(Context context) {
        mContext = context;
    }

    @Override
    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if(mContext instanceof Activity) {
                ((Activity) mContext).finish();
            }
        }
        return false;
    }
}
