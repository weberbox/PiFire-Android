package com.weberbox.pifire.updater;

import android.content.Context;
import android.content.DialogInterface;

import com.weberbox.pifire.updater.objects.Update;

public class UpdateClickListener implements DialogInterface.OnClickListener {

    private final Context context;
    private final Update update;

    public UpdateClickListener(final Context context, final Update update) {
        this.context = context;
        this.update = update;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        UtilsLibrary.getAppUpdate(context, update);
    }
}
