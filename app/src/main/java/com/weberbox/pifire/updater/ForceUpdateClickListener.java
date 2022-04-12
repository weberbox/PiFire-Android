package com.weberbox.pifire.updater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.weberbox.pifire.updater.objects.Update;

public class ForceUpdateClickListener extends UpdateClickListener {

    private final Context context;
    private final Update update;

    public ForceUpdateClickListener(Context context, Update update) {
        super(context, update);
        this.context = context;
        this.update = update;
    }

    @Override
    public void onClick(final DialogInterface dialog, final int which) {
        UtilsLibrary.getAppUpdate(context, update);
        if(context instanceof Activity) {
            ((Activity) context).finish();
        }
    }
}
