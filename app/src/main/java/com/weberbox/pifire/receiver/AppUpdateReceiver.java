package com.weberbox.pifire.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.Constants;

import java.io.File;

public class AppUpdateReceiver extends BroadcastReceiver {
    private static final String TAG = AppUpdateReceiver.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (Intent.ACTION_MY_PACKAGE_REPLACED.equals(intent.getAction())) {
            SharedPreferences prefs = context.getSharedPreferences(context.getPackageName() +
                            "_preferences", Context.MODE_PRIVATE);

            prefs.edit()
                    .putInt(context.getString(R.string.prefs_app_updater_checks), 0)
                    .putInt(context.getString(R.string.prefs_app_updater_force_checks), 0)
                    .putBoolean(context.getString(R.string.prefs_app_update_required), false)
                    .putBoolean(context.getString(R.string.prefs_show_changelog), true)
                    .apply();

            removeUpdateFile(Uri.parse(context.getCacheDir().getPath() + "/" +
                    Constants.UPDATE_FILENAME));
        }
    }

    @SuppressLint("LogNotTimber")
    private void removeUpdateFile(Uri uri) {
        File fileDelete = new File(uri.getPath());
        if (fileDelete.exists()) {
            if (!fileDelete.delete()) {
                Log.d(TAG, "Update file delete error");
            }
        }
    }
}
