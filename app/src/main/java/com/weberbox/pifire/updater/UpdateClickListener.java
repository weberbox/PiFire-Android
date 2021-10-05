package com.weberbox.pifire.updater;

import android.content.Context;
import android.content.DialogInterface;

import java.net.URL;

/**
 * Click listener for the "Update" button of the update dialog. <br/>
 * Extend this class to add custom actions to the button on top of the default functionality.
 */
public class UpdateClickListener implements DialogInterface.OnClickListener {

    private final Context mContext;
    private final URL mApk;

    public UpdateClickListener(final Context context, final URL apk) {
        mContext = context;
        mApk = apk;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        UtilsLibrary.goToUpdate(mContext, mApk);
    }
}
