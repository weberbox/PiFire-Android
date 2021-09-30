package com.weberbox.pifire.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.weberbox.pifire.R;

public class AppUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_PACKAGE_REPLACED.equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences(context.getPackageName(),
                    Context.MODE_PRIVATE);
            prefs.edit()
                    .putInt(context.getString(R.string.prefs_app_updater_checks), 0)
                    .putInt(context.getString(R.string.prefs_app_updater_force_checks), 0)
                    .putBoolean(context.getString(R.string.prefs_app_update_required), false)
                    .apply();
        }
    }
}
