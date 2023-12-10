package com.weberbox.pifire.update;

import android.app.Activity;
import android.content.IntentSender;

import com.google.android.gms.tasks.Task;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.BuildConfig;
import com.weberbox.pifire.R;
import com.weberbox.pifire.config.AppConfig;
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.model.local.UpdateInfo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import timber.log.Timber;

public class UpdateUtils {

    private final Activity activity;
    private final AppUpdateManager appUpdateManager;
    private final FirebaseRemoteConfig firebaseRemoteConfig;
    private final HashMap<String, Object> defaults = new HashMap<>();
    private Alerter downloadAlerter;

    public UpdateUtils(Activity activity) {
        this.activity = activity;
        this.appUpdateManager = AppUpdateManagerFactory.create(activity);
        this.firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
    }

    public void checkForUpdate(boolean forced, boolean waiting) {
        if (forced) {
            showCheckingUpdatesAlert();
        }
        beginCheckForUpdate(forced, waiting);
    }

    public void stopAppUpdater() {
        removeInstallStateUpdateListener();
    }

    public void handleUpdateRequest(int requestCode, int resultCode) {
        if (requestCode == Constants.FLEXIBLE_REQUEST_CODE ||
                requestCode == Constants.IMMEDIATE_REQUEST_CODE) {
            if (resultCode == android.app.Activity.RESULT_CANCELED) {
                Timber.d("Update Canceled by User!");
                if (requestCode == Constants.IMMEDIATE_REQUEST_CODE) {
                    Timber.d("Exiting App");
                    activity.finishAndRemoveTask();
                }
            } else if (resultCode == android.app.Activity.RESULT_OK) {
                Timber.d("Update Success!");
            } else {
                Timber.d("Update Failed!");
            }
        }
    }

    private void beginCheckForUpdate(boolean forced, boolean waiting) {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (waiting) {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    showCompleteUpdateAlert();
                } else if (appUpdateInfo.installStatus() == InstallStatus.INSTALLED) {
                    removeInstallStateUpdateListener();
                } else if (appUpdateInfo.updateAvailability()
                        == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    startAppUpdate(appUpdateInfo, AppUpdateType.IMMEDIATE);
                }
            } else {
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    fetchFirebaseUpdateInfo(forced, appUpdateInfo);
                } else if (forced && appUpdateInfo.updateAvailability() ==
                        UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                    showNoUpdateAlert();
                } else if (forced && appUpdateInfo.updateAvailability() ==
                        UpdateAvailability.UNKNOWN) {
                    showUpdateCheckFailedAlert();
                }
            }
        });
    }

    private void fetchFirebaseUpdateInfo(boolean forced, AppUpdateInfo appUpdateInfo) {
        firebaseRemoteConfig.setConfigSettingsAsync(new FirebaseRemoteConfigSettings.Builder()
                .setFetchTimeoutInSeconds(2000)
                .setMinimumFetchIntervalInSeconds(AppConfig.DEBUG ? 0 : 3600)
                .build());
        defaults.put(AppConfig.INAPP_FIREBASE_INFO, R.raw.update_info);
        firebaseRemoteConfig.setDefaultsAsync(defaults);

        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String firebaseInfo = firebaseRemoteConfig.getString(AppConfig.INAPP_FIREBASE_INFO);
                checkUpdatePriority(forced, firebaseInfo, appUpdateInfo);
            } else {
                Timber.d("Firebase Update Info Fetch Failed");
            }
        });
    }

    private void checkUpdatePriority(boolean forced, String json, AppUpdateInfo info) {
        ArrayList<UpdateInfo> updateInfo = UpdateInfo.parseJSON(json);
        Integer stalenessDays = info.clientVersionStalenessDays();
        int updatePriority = getUpdatePriority(updateInfo);
        boolean firstShow = Prefs.getBoolean(activity.getString(R.string.prefs_inapp_first_show), true);

        switch (updatePriority) {
            case AppConfig.PRIORITY_URGENT:
                if (info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                    startAppUpdate(info, AppUpdateType.IMMEDIATE);
                }
            case AppConfig.PRIORITY_HIGH:
                if (stalenessDays != null && stalenessDays >= 5) {
                    if (info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startAppUpdate(info, AppUpdateType.IMMEDIATE);
                    }
                } else if (stalenessDays != null && stalenessDays >= 3 || forced || firstShow) {
                    if (info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        startAppUpdate(info, AppUpdateType.FLEXIBLE);
                    }
                }
            case AppConfig.PRIORITY_MEDIUM:
                if (stalenessDays != null && stalenessDays >= 20) {
                    if (info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startAppUpdate(info, AppUpdateType.IMMEDIATE);
                    }
                } else if (stalenessDays != null && stalenessDays >= 10 || forced || firstShow) {
                    if (info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        startAppUpdate(info, AppUpdateType.FLEXIBLE);
                    }
                }
            case AppConfig.PRIORITY_LOW:
                if (stalenessDays != null && stalenessDays >= 30) {
                    if (info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startAppUpdate(info, AppUpdateType.IMMEDIATE);
                    }
                } else if (stalenessDays != null && stalenessDays >= 15 || forced || firstShow) {
                    if (info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        startAppUpdate(info, AppUpdateType.FLEXIBLE);
                    }
                }
            case AppConfig.PRIORITY_MINIMAL:
                if (stalenessDays != null && stalenessDays >= 60) {
                    if (info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                        startAppUpdate(info, AppUpdateType.IMMEDIATE);
                    }
                } else if (stalenessDays != null && stalenessDays >= 30 || forced || firstShow) {
                    if (info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        startAppUpdate(info, AppUpdateType.FLEXIBLE);
                    }
                }
            case AppConfig.PRIORITY_NONE:
                if (forced) {
                    if (info.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                        startAppUpdate(info, AppUpdateType.FLEXIBLE);
                    }
                }
            default:
                // Shouldn't get here
                Timber.d("Update Priority Missing");
        }
    }

    private void startAppUpdate(AppUpdateInfo appUpdateInfo, Integer updateType) {
        int requestCode;
        if (updateType == AppUpdateType.IMMEDIATE) {
            requestCode = Constants.IMMEDIATE_REQUEST_CODE;
        } else {
            Prefs.putBoolean(activity.getString(R.string.prefs_inapp_first_show), false);
            appUpdateManager.registerListener(listener);
            requestCode = Constants.FLEXIBLE_REQUEST_CODE;
        }

        try {
            appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateType,
                    activity,
                    requestCode);
        } catch (IntentSender.SendIntentException e) {
            Timber.e(e, "startAppUpdate SendIntentException");
        }
    }

    private int getUpdatePriority(ArrayList<UpdateInfo> info) {
        UpdateInfo mostImportantUpdate = info
                .stream()
                .filter(v -> v.getVersionCode() > BuildConfig.VERSION_CODE)
                .max(Comparator.comparing(UpdateInfo::getUpdatePriority))
                .orElse(null);
        return mostImportantUpdate != null ? mostImportantUpdate.getUpdatePriority() : 0;
    }

    private void showCompleteUpdateAlert() {
        removeInstallStateUpdateListener();
        Alerter.create(activity)
                .setText(R.string.inapp_update_downloaded)
                .setIcon(R.drawable.ic_update)
                .setBackgroundColorRes(R.color.colorAccentRed)
                .setTitleAppearance(R.style.Text18AllerBold)
                .setTextAppearance(R.style.Text14AllerBold)
                .enableInfiniteDuration(true)
                .enableSwipeToDismiss()
                .addButton(activity.getString(R.string.restart), R.style.AlerterButton, v -> {
                    appUpdateManager.completeUpdate();
                    Alerter.hide();
                })
                .show();
    }

    private void showUpdateDownloadAlert() {
        if (downloadAlerter == null) {
            downloadAlerter = Alerter.create(activity)
                    .setText(R.string.inapp_update_downloading)
                    .setIcon(R.drawable.ic_update)
                    .setBackgroundColorRes(R.color.colorPrimaryLighter)
                    .enableProgress(true)
                    .enableSwipeToDismiss()
                    .setTitleAppearance(R.style.Text18AllerBold)
                    .setTextAppearance(R.style.Text14AllerBold)
                    .enableInfiniteDuration(true);
            downloadAlerter.show();
        }
    }

    private void showNoUpdateAlert() {
        Alerter.create(activity)
                .setText(R.string.inapp_update_current)
                .setIcon(R.drawable.ic_menu_about)
                .setBackgroundColorRes(R.color.colorPrimaryLighter)
                .setTitleAppearance(R.style.Text18AllerBold)
                .setTextAppearance(R.style.Text14AllerBold)
                .enableSwipeToDismiss()
                .show();
    }

    private void showUpdateCheckFailedAlert() {
        Alerter.create(activity)
                .setText(R.string.inapp_update_check_failed)
                .setIcon(R.drawable.ic_error_triangle)
                .setBackgroundColorRes(R.color.colorAccentRed)
                .setTitleAppearance(R.style.Text18AllerBold)
                .setTextAppearance(R.style.Text14AllerBold)
                .enableSwipeToDismiss()
                .show();
    }

    private void showCheckingUpdatesAlert() {
        Alerter.create(activity)
                .setText(R.string.inapp_update_checking)
                .setIcon(R.drawable.ic_menu_about)
                .setBackgroundColorRes(R.color.colorPrimaryLighter)
                .setTitleAppearance(R.style.Text18AllerBold)
                .setTextAppearance(R.style.Text14AllerBold)
                .enableSwipeToDismiss()
                .show();
    }

    private void removeInstallStateUpdateListener() {
        if (appUpdateManager != null) {
            appUpdateManager.unregisterListener(listener);
        }
    }

    @SuppressWarnings("unused")
    private final InstallStateUpdatedListener listener = state -> {
        if (state.installStatus() == InstallStatus.DOWNLOADING) {
            long bytesDownloaded = state.bytesDownloaded();
            long totalBytesToDownload = state.totalBytesToDownload();
            showUpdateDownloadAlert();
        } else if (state.installStatus() == InstallStatus.DOWNLOADED) {
            showCompleteUpdateAlert();
        }
    };
}
