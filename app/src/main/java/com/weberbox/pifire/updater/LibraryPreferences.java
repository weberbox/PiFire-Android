package com.weberbox.pifire.updater;

import android.content.Context;

import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;

class LibraryPreferences {
    private final Context context;

    public LibraryPreferences(Context context) {
        this.context = context;
    }

    public Boolean getAppUpdaterShow() {
        return Prefs.getBoolean(context.getString(R.string.prefs_app_updater_enabled),
                context.getResources().getBoolean(R.bool.def_app_updater_enabled));
    }

    public void setAppUpdaterShow(Boolean show) {
        Prefs.putBoolean(context.getString(R.string.prefs_app_updater_enabled), show);
    }

    public Integer getSuccessfulChecks() {
        return Prefs.getInt(context.getString(R.string.prefs_app_updater_checks), 0);
    }

    public void setSuccessfulChecks(Integer checks) {
        Prefs.putInt(context.getString(R.string.prefs_app_updater_checks), checks);
    }

    public void setForcedChecks(Integer checks) {
        Prefs.putInt(context.getString(R.string.prefs_app_updater_force_checks), checks);
    }

    public Integer getForcedChecks() {
        return Prefs.getInt(context.getString(R.string.prefs_app_updater_force_checks), 0);
    }

    public void setUpdateRequired(Boolean required) {
        Prefs.putBoolean(context.getString(R.string.prefs_app_update_required), required);
    }

    public Boolean getUpdateRequired() {
        return Prefs.getBoolean(context.getString(R.string.prefs_app_update_required), false);
    }

}
