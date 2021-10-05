package com.weberbox.pifire.updater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import java.net.URL;

public class ForceUpdateClickListener extends UpdateClickListener {

    private final Context mContext;
    private final URL mApk;

    public ForceUpdateClickListener(Context context, URL apk) {
        super(context, apk);
        mContext = context;
        mApk = apk;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        UtilsLibrary.goToUpdate(mContext, mApk);
        if(mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }
}
