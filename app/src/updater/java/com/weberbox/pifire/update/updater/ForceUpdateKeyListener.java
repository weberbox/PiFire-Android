package com.weberbox.pifire.update.updater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.view.KeyEvent;

public class ForceUpdateKeyListener implements DialogInterface.OnKeyListener {

    private final Context context;

    public ForceUpdateKeyListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if(context instanceof Activity) {
                ((Activity) context).finish();
            }
        }
        return false;
    }
}
