package com.weberbox.pifire.update.updater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.snackbar.Snackbar;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.R;
import com.weberbox.pifire.update.updater.enums.AppUpdaterError;
import com.weberbox.pifire.update.updater.enums.Display;
import com.weberbox.pifire.update.updater.enums.Duration;
import com.weberbox.pifire.update.updater.enums.UpdateFrom;
import com.weberbox.pifire.update.updater.interfaces.IAppUpdater;
import com.weberbox.pifire.update.updater.objects.GitHub;
import com.weberbox.pifire.update.updater.objects.Update;

import timber.log.Timber;

public class AppUpdater implements IAppUpdater {

    private View view;
    private View viewAnchor;
    private final Context context;
    private final LibraryPreferences libraryPreferences;
    private final String notifTitleUpdate;
    private Display display;
    private UpdateFrom updateFrom;
    private Duration duration;
    private GitHub gitHub;
    private String jsonUrl;
    private Integer showEvery;
    private Boolean showAppUpToDate;
    private Boolean btnDisableShown;
    private Boolean btnDismissShown;
    private Boolean showAppUpdateError;
    private String titleUpdate, descriptionUpdate, btnDismiss, btnUpdate, btnDisable; // Update available
    private String titleNoUpdate, descriptionNoUpdate; // Update not available
    private String descriptionUpdateFailed;
    private int iconResId;
    private UtilsAsync.LatestAppVersion latestAppVersion;
    private DialogInterface.OnClickListener btnUpdateClickListener, btnDismissClickListener,
            btnDisableClickListener;

    private AlertDialog alertDialog;
    private Snackbar snackbar;
    private Alerter alerter;
    private Boolean isDialogCancelable;
    private Boolean shouldForceUpdateCheck;
    private Boolean manualForceUpdateCheck;

    public AppUpdater(Context context) {
        this.context = context;
        libraryPreferences = new LibraryPreferences(context);
        display = Display.DIALOG;
        updateFrom = UpdateFrom.JSON;
        duration = Duration.NORMAL;
        showEvery = 1;
        showAppUpToDate = false;
        btnDisableShown = true;
        showAppUpdateError = false;
        shouldForceUpdateCheck = false;
        manualForceUpdateCheck = false;
        iconResId = R.drawable.ic_update;

        // Dialog
        titleNoUpdate = context.getResources().getString(R.string.updater_update_not_available);
        btnUpdate = context.getResources().getString(R.string.update_button);
        btnDismiss = context.getResources().getString(R.string.dismiss_button);
        btnDisable = context.getResources().getString(R.string.disable_button);
        isDialogCancelable = true;
        btnDismissShown = true;

        // Notification
        notifTitleUpdate = context.getResources().getString(R.string.updater_update_available);
    }

    @Override
    public AppUpdater setDisplay(Display display) {
        this.display = display;
        return this;
    }

    @Override
    public AppUpdater setUpdateFrom(UpdateFrom updateFrom) {
        this.updateFrom = updateFrom;
        return this;
    }

    @Override
    public AppUpdater setDuration(Duration duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public AppUpdater setView(View view) {
        this.view = view;
        return this;
    }

    @Override
    public AppUpdater setViewAnchor(View view) {
        viewAnchor = view;
        return this;
    }

    @Override
    public AppUpdater setGitHubUserAndRepo(@NonNull String user, @NonNull String repo) {
        gitHub = new GitHub(user, repo);
        return this;
    }

    @Override
    public AppUpdater setUpdateJSON(@NonNull String jsonUrl) {
        this.jsonUrl = jsonUrl;
        return this;
    }


    @Override
    public AppUpdater showEvery(Integer times) {
        showEvery = times;
        return this;
    }

    @Override
    public AppUpdater showAppUpToDate(Boolean res) {
        showAppUpToDate = res;
        return this;
    }

    @Override
    public AppUpdater showAppUpdateError(Boolean res) {
        showAppUpdateError = res;
        return this;
    }

    @Override
    public AppUpdater setAppUpdateCheckFailedText(@NonNull String text) {
        descriptionUpdateFailed = text;
        return this;
    }

    @Override
    public AppUpdater setTitleOnUpdateAvailable(@NonNull String title) {
        titleUpdate = title;
        return this;
    }

    @Override
    public AppUpdater setTitleOnUpdateAvailable(@StringRes int textResource) {
        titleUpdate = context.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setContentOnUpdateAvailable(@NonNull String description) {
        descriptionUpdate = description;
        return this;
    }

    @Override
    public AppUpdater setContentOnUpdateAvailable(@StringRes int textResource) {
        descriptionUpdate = context.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setTitleOnUpdateNotAvailable(@NonNull String title) {
        titleNoUpdate = title;
        return this;
    }

    @Override
    public AppUpdater setTitleOnUpdateNotAvailable(@StringRes int textResource) {
        titleNoUpdate = context.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setContentOnUpdateNotAvailable(@NonNull String description) {
        descriptionNoUpdate = description;
        return this;
    }

    @Override
    public AppUpdater setContentOnUpdateNotAvailable(@StringRes int textResource) {
        descriptionNoUpdate = context.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setButtonUpdate(@NonNull String text) {
        btnUpdate = text;
        return this;
    }

    @Override
    public AppUpdater setButtonUpdate(@StringRes int textResource) {
        btnUpdate = context.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setButtonDismiss(@NonNull String text) {
        btnDismiss = text;
        return this;
    }

    @Override
    public AppUpdater setButtonDismiss(@StringRes int textResource) {
        btnDismiss = context.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setButtonDoNotShowAgain(@NonNull String text) {
        btnDisable = text;
        return this;
    }

    @Override
    public AppUpdater setButtonDoNotShowAgain(@StringRes int textResource) {
        btnDisable = context.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setButtonDoNotShowAgain(@NonNull Boolean isShown) {
        btnDisableShown = isShown;
        return this;
    }

    @Override
    public AppUpdater setButtonDismissEnabled(@NonNull Boolean isShown) {
        btnDismissShown = isShown;
        return this;
    }

    @Override
    public AppUpdater setButtonUpdateClickListener(final DialogInterface.OnClickListener clickListener) {
        btnUpdateClickListener = clickListener;
        return this;
    }

    @Override
    public AppUpdater setButtonDismissClickListener(final DialogInterface.OnClickListener clickListener) {
        btnDismissClickListener = clickListener;
        return this;
    }

    @Override
    public AppUpdater setButtonDoNotShowAgainClickListener(final DialogInterface.OnClickListener clickListener) {
        btnDisableClickListener = clickListener;
        return this;
    }

    @Override
    public AppUpdater setIcon(@DrawableRes int iconRes) {
        iconResId = iconRes;
        return this;
    }

    @Override
    public AppUpdater setCancelable(Boolean isDialogCancelable) {
        this.isDialogCancelable = isDialogCancelable;
        return this;
    }

    @Override
    public AppUpdater setForceCheck(Boolean forceCheck) {
        manualForceUpdateCheck = forceCheck;
        return this;
    }

    @Override
    public void start() {
        Integer forceChecks = libraryPreferences.getForcedChecks();
        if (!libraryPreferences.getAppUpdaterShow()) {
            libraryPreferences.setForcedChecks(forceChecks + 1);
            if (libraryPreferences.getUpdateRequired() | forceChecks % 25 == 0) {
                shouldForceUpdateCheck = true;
            }
        } else {
            shouldForceUpdateCheck = libraryPreferences.getAppUpdaterShow();
        }

        if (manualForceUpdateCheck) {
            shouldForceUpdateCheck = true;
        }

        latestAppVersion = new UtilsAsync.LatestAppVersion(context, shouldForceUpdateCheck,
                updateFrom, gitHub, jsonUrl, new LibraryListener() {
            @Override
            public void onSuccess(Update update) {
                if (context instanceof Activity && ((Activity) context).isFinishing()) {
                    return;
                }

                Integer currentInstalledVersionCode = UtilsLibrary.getAppInstalledVersionCode();

                Update installedVersion = new Update(UtilsLibrary.getAppInstalledVersion(),
                        currentInstalledVersionCode);

                boolean forceUpdateRequired =  UtilsLibrary.getRequiredUpdate(update,
                        currentInstalledVersionCode);

                if (UtilsLibrary.isUpdateAvailable(installedVersion, update)) {
                    Timber.i("Update Available");
                    Integer successfulChecks = libraryPreferences.getSuccessfulChecks();
                    if (UtilsLibrary.isAbleToShow(successfulChecks, showEvery) |
                            forceUpdateRequired | manualForceUpdateCheck) {
                        if (libraryPreferences.getAppUpdaterShow() | forceUpdateRequired |
                                manualForceUpdateCheck) {
                            switch (display) {
                                case DIALOG:
                                    if (forceUpdateRequired) {
                                        Timber.d("Force Update Requested");
                                        libraryPreferences.setUpdateRequired(true);
                                        btnUpdateClickListener = new ForceUpdateClickListener(
                                                context, update);
                                        isDialogCancelable = false;
                                        btnDisableShown = false;
                                        btnDismissShown = false;
                                    } else {
                                        libraryPreferences.setUpdateRequired(false);
                                    }

                                    final DialogInterface.OnClickListener updateClickListener =
                                            btnUpdateClickListener == null ? new UpdateClickListener(
                                                    context, update) : btnUpdateClickListener;

                                    final DialogInterface.OnClickListener disableClickListener =
                                            btnDisableClickListener == null ? new DisableClickListener(
                                                    context) : btnDisableClickListener;

                                    alertDialog = UtilsDisplay.showUpdateAvailableDialog(context,
                                            getDialogTitleUpdate(context, update, forceUpdateRequired),
                                            getDescriptionUpdate(context, update, Display.DIALOG),
                                            btnDismiss, btnUpdate, btnDisable,
                                            updateClickListener, btnDismissClickListener,
                                            disableClickListener, btnDisableShown, btnDismissShown,
                                            forceUpdateRequired);

                                    alertDialog.setCancelable(isDialogCancelable);
                                    alertDialog.show();
                                    break;
                                case SNACKBAR:
                                    snackbar = UtilsDisplay.showUpdateAvailableSnackbar(view, viewAnchor,
                                            getDescriptionUpdate(context, update, Display.SNACKBAR),
                                            UtilsLibrary.getDurationEnumToBoolean(duration), update);
                                    snackbar.show();
                                case ALERTER:
                                    if (context instanceof Activity) {
                                        alerter = UtilsDisplay.showUpdateAvailableAlert(
                                                (Activity) context, getDescriptionUpdate(context,
                                                        update, Display.ALERTER),
                                                UtilsLibrary.getDurationEnumToBoolean(duration), update);
                                        alerter.show();
                                    }
                                    break;
                                case NOTIFICATION:
                                    UtilsDisplay.showUpdateAvailableNotification(context, notifTitleUpdate,
                                            getDescriptionUpdate(context, update, Display.NOTIFICATION),
                                            update.getUrlToDownload(), iconResId);
                                    break;
                            }
                        }
                    }
                    libraryPreferences.setSuccessfulChecks(successfulChecks + 1);
                } else if (showAppUpToDate) {
                    Timber.i("No Update Available");

                    switch (display) {
                        case DIALOG:
                            alertDialog = UtilsDisplay.showUpdateNotAvailableDialog(context,
                                    titleNoUpdate, getDescriptionNoUpdate(context));
                            alertDialog.setCancelable(isDialogCancelable);
                            alertDialog.show();
                            break;
                        case SNACKBAR:
                            snackbar = UtilsDisplay.showUpdateNotAvailableSnackbar(view, viewAnchor,
                                    getDescriptionNoUpdate(context),
                                    UtilsLibrary.getDurationEnumToBoolean(duration));
                            snackbar.show();
                            break;
                        case ALERTER:
                            if (context instanceof Activity) {
                                alerter = UtilsDisplay.showUpdateNotAvailableAlert(
                                        (Activity) context, getDescriptionNoUpdate(context),
                                        UtilsLibrary.getDurationEnumToBoolean(duration));
                                alerter.show();
                            }
                            break;
                        case NOTIFICATION:
                            UtilsDisplay.showUpdateNotAvailableNotification(context, titleNoUpdate,
                                    getDescriptionNoUpdate(context), iconResId);
                            break;
                    }
                } else {
                    Timber.i("No Update Available");
                }
            }

            @Override
            public void onFailed(AppUpdaterError error) {
                Timber.d("AppUpdater onFailed: %s", error);
                if (showAppUpdateError) {
                    if (context instanceof Activity) {
                        alerter = UtilsDisplay.showUpdateFailedAlert((Activity) context,
                                getDescriptionUpdateFailed(context),
                                UtilsLibrary.getDurationEnumToBoolean(duration));
                        alerter.show();
                    } else {
                        snackbar = UtilsDisplay.showUpdateFailedSnackbar(view, null,
                                getDescriptionUpdateFailed(context),
                                UtilsLibrary.getDurationEnumToBoolean(duration));
                        snackbar.show();
                    }
                }
            }
        });

        latestAppVersion.execute();
    }

    @Override
    public void stop() {
        if (latestAppVersion != null && !latestAppVersion.isCancelled()) {
            latestAppVersion.cancel();
        }
    }

    @Override
    public void dismiss() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
        if (snackbar != null && snackbar.isShown()) {
            snackbar.dismiss();
        }
        if (alerter != null && Alerter.isShowing()) {
            Alerter.hide();
        }
    }

    private String getDescriptionUpdate(Context context, Update update, Display display) {
        if (descriptionUpdate == null || TextUtils.isEmpty(descriptionUpdate)) {
            switch (display) {
                case DIALOG:
                    if (update.getReleaseNotes() != null && !TextUtils.isEmpty(update.getReleaseNotes())) {
                        if (TextUtils.isEmpty(descriptionUpdate)) {
                            return update.getReleaseNotes();
                        } else {
                            return String.format(context.getResources().getString(
                                    R.string.updater_update_available_description_dialog_before_release_notes),
                                    update.getLatestVersion(), update.getReleaseNotes());
                        }
                    } else {
                        return String.format(context.getResources().getString(
                                R.string.updater_update_available_description_dialog),
                                update.getLatestVersion(), UtilsLibrary.getAppName(context));
                    }

                case SNACKBAR:
                    return String.format(context.getResources().getString(
                            R.string.updater_update_available_description_snackbar),
                            update.getLatestVersion());

                case ALERTER:
                case NOTIFICATION:
                    return String.format(context.getResources().getString(
                            R.string.updater_update_available_description_notification),
                            update.getLatestVersion(), UtilsLibrary.getAppName(context));

            }
        }

        return descriptionUpdate;
    }

    private String getDialogTitleUpdate(Context context, Update update, boolean forceUpdate) {
        if (forceUpdate) {
            titleUpdate = context.getResources().getString(R.string.updater_update_required);
        } else {
            if (titleUpdate == null || TextUtils.isEmpty(titleUpdate)) {
                if (update.getReleaseNotes() != null && !TextUtils.isEmpty(update.getReleaseNotes())) {
                    if (TextUtils.isEmpty(titleUpdate)) {
                        return String.format(context.getResources().getString(
                                R.string.updater_update_available_description_snackbar), update.getLatestVersion());
                    } else {
                        return titleUpdate;
                    }
                } else {
                    return context.getResources().getString(R.string.updater_update_available);
                }
            }
        }

        return titleUpdate;
    }

    @SuppressWarnings("all")
    private String getDescriptionNoUpdate(Context context) {
        if (descriptionNoUpdate == null) {
            return String.format(context.getResources().getString(
                    R.string.updater_update_not_available_description),
                    UtilsLibrary.getAppName(context));
        } else {
            return descriptionNoUpdate;
        }
    }

    private String getDescriptionUpdateFailed(Context context) {
        if (descriptionUpdateFailed == null) {
            return context.getResources().getString(R.string.updater_update_check_failed);
        } else {
            return descriptionUpdateFailed;
        }
    }

}
