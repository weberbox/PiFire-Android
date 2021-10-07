package com.weberbox.pifire.updater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.weberbox.pifire.updater.objects.Update;

public class ForceUpdateClickListener extends UpdateClickListener {

    private final Context mContext;
    private final Update mUpdate;

    public ForceUpdateClickListener(Context context, Update update) {
        super(context, update);
        mContext = context;
        mUpdate = update;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        UtilsLibrary.getAppUpdate(mContext, mUpdate);
        if(mContext instanceof Activity) {
            ((Activity) mContext).finish();
        }
    }
}
