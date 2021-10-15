package com.weberbox.pifire.updater;

import android.content.Context;
import android.content.DialogInterface;

import com.weberbox.pifire.updater.objects.Update;

public class UpdateClickListener implements DialogInterface.OnClickListener {

    private final Context mContext;
    private final Update mUpdate;

    public UpdateClickListener(final Context context, final Update update) {
        mContext = context;
        mUpdate = update;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        UtilsLibrary.getAppUpdate(mContext, mUpdate);
    }
}
