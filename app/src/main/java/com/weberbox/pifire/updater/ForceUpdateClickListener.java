package com.weberbox.pifire.updater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.weberbox.pifire.updater.enums.UpdateFrom;

import java.net.URL;

public class ForceUpdateClickListener extends UpdateClickListener {

    private final Context mContext;
    private final UpdateFrom mUpdateFrom;
    private final URL mApk;

    public ForceUpdateClickListener(Context context, UpdateFrom updateFrom, URL apk) {
        super(context, updateFrom, apk);
        mContext = context;
        mUpdateFrom = updateFrom;
        mApk = apk;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        UtilsLibrary.goToUpdate(mContext, mUpdateFrom, mApk);
        if(mContext instanceof Activity){ ((Activity) mContext).finish(); }
    }
}
