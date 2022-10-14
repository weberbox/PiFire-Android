package com.weberbox.pifire.update;

import android.content.Context;

import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.update.updater.AppUpdater;
import com.weberbox.pifire.update.updater.enums.Display;
import com.weberbox.pifire.update.updater.enums.UpdateFrom;

@SuppressWarnings("ConstantConditions")
public class UpdateUtils {

    private final Context context;
    private AppUpdater appUpdater;

    public UpdateUtils(Context context) {
        this.context = context;
    }

    public boolean updaterEnabled() {
        return !context.getString(R.string.def_app_update_check_url).isEmpty() &&
                !context.getString(R.string.def_app_update_check_url_beta).isEmpty();
    }

    public void checkForUpdate(boolean forced, boolean waiting) {
        beginCheckForUpdate(forced);
        appUpdater = new AppUpdater(context)
                .setDisplay(Display.DIALOG)
                .setButtonDoNotShowAgain(R.string.disable_button)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON(AppConfig.IS_BETA ?
                        context.getString(R.string.def_app_update_check_url_beta) :
                        context.getString(R.string.def_app_update_check_url))
                .showEvery(AppConfig.UPDATE_CHECK_FREQ);
        appUpdater.start();
    }

    public void stopAppUpdater() {
        if (appUpdater != null) {
            appUpdater.stop();
        }
    }

    private void beginCheckForUpdate(boolean forced) {
        if (forced) {
            appUpdater = new AppUpdater(context)
                    .setDisplay(Display.DIALOG)
                    .setButtonDoNotShowAgain(false)
                    .showAppUpToDate(true)
                    .showAppUpdateError(true)
                    .setForceCheck(true)
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setUpdateJSON(AppConfig.IS_BETA ?
                            context.getString(R.string.def_app_update_check_url_beta) :
                            context.getString(R.string.def_app_update_check_url));
            appUpdater.start();
        } else {
            appUpdater = new AppUpdater(context)
                    .setDisplay(Display.DIALOG)
                    .setButtonDoNotShowAgain(R.string.disable_button)
                    .setUpdateFrom(UpdateFrom.JSON)
                    .setUpdateJSON(AppConfig.IS_BETA ?
                            context.getString(R.string.def_app_update_check_url_beta) :
                            context.getString(R.string.def_app_update_check_url))
                    .showEvery(AppConfig.UPDATE_CHECK_FREQ);
            appUpdater.start();
        }
    }

    public void handleUpdateRequest(int requestCode, int resultCode) {
        // Nothing to do
    }
}
