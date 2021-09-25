package com.weberbox.pifire.updater;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import com.google.android.material.snackbar.Snackbar;
import com.weberbox.pifire.R;
import com.weberbox.pifire.updater.enums.AppUpdaterError;
import com.weberbox.pifire.updater.enums.Display;
import com.weberbox.pifire.updater.enums.Duration;
import com.weberbox.pifire.updater.enums.UpdateFrom;
import com.weberbox.pifire.updater.interfaces.IAppUpdater;
import com.weberbox.pifire.updater.objects.GitHub;
import com.weberbox.pifire.updater.objects.Update;
import com.weberbox.pifire.utils.Log;

import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;

public class AppUpdater implements IAppUpdater {
    private static final String TAG = AppUpdater.class.getSimpleName();

    private View mView;
    private View mViewAnchor;
    private final Context mContext;
    private final LibraryPreferences mLibraryPreferences;
    private final String mNotifTitleUpdate;
    private Display mDisplay;
    private UpdateFrom mUpdateFrom;
    private Duration mDuration;
    private GitHub mGitHub;
    private String mJsonUrl;
    private Integer mShowEvery;
    private Boolean mShowAppUpToDate;
    private Boolean mBtnDisableShown;
    private Boolean mShowAppUpdateError;
    private String mTitleUpdate, mDescriptionUpdate, mBtnDismiss, mBtnUpdate, mBtnDisable; // Update available
    private String mTitleNoUpdate, mDescriptionNoUpdate; // Update not available
    private String mDescriptionUpdateFailed;
    private int mIconResId;
    private UtilsAsync.LatestAppVersion mLatestAppVersion;
    private DialogInterface.OnClickListener mBtnUpdateClickListener, mBtnDismissClickListener, mBtnDisableClickListener;

    private AlertDialog mAlertDialog;
    private Snackbar mSnackbar;
    private Boolean mIsDialogCancelable;

    public AppUpdater(Context context) {
        mContext = context;
        mLibraryPreferences = new LibraryPreferences(context);
        mDisplay = Display.DIALOG;
        mUpdateFrom = UpdateFrom.JSON;
        mDuration = Duration.NORMAL;
        mShowEvery = 1;
        mShowAppUpToDate = false;
        mBtnDisableShown = true;
        mShowAppUpdateError = false;
        mIconResId = R.drawable.ic_update;

        // Dialog
        mTitleNoUpdate = context.getResources().getString(R.string.updater_update_not_available);
        mBtnUpdate = context.getResources().getString(R.string.update_button);
        mBtnDismiss = context.getResources().getString(R.string.dismiss_button);
        mBtnDisable = context.getResources().getString(R.string.disable_button);
        mIsDialogCancelable = true;

        // Notification
        mNotifTitleUpdate = context.getResources().getString(R.string.updater_update_available);
    }

    @Override
    public AppUpdater setDisplay(Display display) {
        mDisplay = display;
        return this;
    }

    @Override
    public AppUpdater setUpdateFrom(UpdateFrom updateFrom) {
        mUpdateFrom = updateFrom;
        return this;
    }

    @Override
    public AppUpdater setDuration(Duration duration) {
        mDuration = duration;
        return this;
    }

    @Override
    public AppUpdater setView(View view) {
        mView = view;
        return this;
    }

    @Override
    public AppUpdater setViewAnchor(View view) {
        mViewAnchor = view;
        return this;
    }

    @Override
    public AppUpdater setGitHubUserAndRepo(@NonNull String user, @NonNull String repo) {
        mGitHub = new GitHub(user, repo);
        return this;
    }

    @Override
    public AppUpdater setUpdateJSON(@NonNull String jsonUrl) {
        mJsonUrl = jsonUrl;
        return this;
    }


    @Override
    public AppUpdater showEvery(Integer times) {
        mShowEvery = times;
        return this;
    }

    @Override
    public AppUpdater showAppUpToDate(Boolean res) {
        mShowAppUpToDate = res;
        return this;
    }

    @Override
    public AppUpdater showAppUpdateError(Boolean res) {
        mShowAppUpdateError = res;
        return this;
    }

    @Override
    public AppUpdater setAppUpdateCheckFailedText(@NonNull String text) {
        mDescriptionUpdateFailed = text;
        return this;
    }

    @Override
    public AppUpdater setTitleOnUpdateAvailable(@NonNull String title) {
        mTitleUpdate = title;
        return this;
    }

    @Override
    public AppUpdater setTitleOnUpdateAvailable(@StringRes int textResource) {
        mTitleUpdate = mContext.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setContentOnUpdateAvailable(@NonNull String description) {
        mDescriptionUpdate = description;
        return this;
    }

    @Override
    public AppUpdater setContentOnUpdateAvailable(@StringRes int textResource) {
        mDescriptionUpdate = mContext.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setTitleOnUpdateNotAvailable(@NonNull String title) {
        mTitleNoUpdate = title;
        return this;
    }

    @Override
    public AppUpdater setTitleOnUpdateNotAvailable(@StringRes int textResource) {
        mTitleNoUpdate = mContext.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setContentOnUpdateNotAvailable(@NonNull String description) {
        mDescriptionNoUpdate = description;
        return this;
    }

    @Override
    public AppUpdater setContentOnUpdateNotAvailable(@StringRes int textResource) {
        mDescriptionNoUpdate = mContext.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setButtonUpdate(@NonNull String text) {
        mBtnUpdate = text;
        return this;
    }

    @Override
    public AppUpdater setButtonUpdate(@StringRes int textResource) {
        mBtnUpdate = mContext.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setButtonDismiss(@NonNull String text) {
        mBtnDismiss = text;
        return this;
    }

    @Override
    public AppUpdater setButtonDismiss(@StringRes int textResource) {
        mBtnDismiss = mContext.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setButtonDoNotShowAgain(@NonNull String text) {
        mBtnDisable = text;
        return this;
    }

    @Override
    public AppUpdater setButtonDoNotShowAgain(@StringRes int textResource) {
        mBtnDisable = mContext.getString(textResource);
        return this;
    }

    @Override
    public AppUpdater setButtonDoNotShowAgain(@NonNull Boolean isShown) {
        mBtnDisableShown = isShown;
        return this;
    }

    @Override
    public AppUpdater setButtonUpdateClickListener(final DialogInterface.OnClickListener clickListener) {
        mBtnUpdateClickListener = clickListener;
        return this;
    }

    @Override
    public AppUpdater setButtonDismissClickListener(final DialogInterface.OnClickListener clickListener) {
        mBtnDismissClickListener = clickListener;
        return this;
    }

    @Override
    public AppUpdater setButtonDoNotShowAgainClickListener(final DialogInterface.OnClickListener clickListener) {
        mBtnDisableClickListener = clickListener;
        return this;
    }

    @Override
    public AppUpdater setIcon(@DrawableRes int iconRes) {
        mIconResId = iconRes;
        return this;
    }

    @Override
    public AppUpdater setCancelable(Boolean isDialogCancelable) {
        mIsDialogCancelable = isDialogCancelable;
        return this;
    }

    @Override
    public void start() {
        mLatestAppVersion = new UtilsAsync.LatestAppVersion(mContext, false, mUpdateFrom,
                mGitHub, mJsonUrl, new LibraryListener() {
            @Override
            public void onSuccess(Update update) {
                if (mContext instanceof Activity && ((Activity) mContext).isFinishing()) {
                    return;
                }

                Update installedUpdate = new Update(UtilsLibrary.getAppInstalledVersion(mContext),
                        UtilsLibrary.getAppInstalledVersionCode(mContext));

                if (UtilsLibrary.isUpdateAvailable(installedUpdate, update)) {
                    Log.d(TAG, "Update Available");
                    Integer successfulChecks = mLibraryPreferences.getSuccessfulChecks();
                    if (UtilsLibrary.isAbleToShow(successfulChecks, mShowEvery)) {
                        switch (mDisplay) {
                            case DIALOG:
                                final DialogInterface.OnClickListener updateClickListener =
                                        mBtnUpdateClickListener == null ? new UpdateClickListener(
                                                mContext, mUpdateFrom, update.getUrlToDownload()) :
                                                mBtnUpdateClickListener;

                                final DialogInterface.OnClickListener disableClickListener =
                                        mBtnDisableClickListener == null ? new DisableClickListener(
                                                mContext) : mBtnDisableClickListener;

                                mAlertDialog = UtilsDisplay.showUpdateAvailableDialog(mContext,
                                        getDialogTitleUpdate(mContext, update),
                                        getDescriptionUpdate(mContext, update, Display.DIALOG),
                                        mBtnDismiss, mBtnUpdate, mBtnDisable,
                                        updateClickListener, mBtnDismissClickListener,
                                        disableClickListener, mBtnDisableShown);

                                mAlertDialog.setCancelable(mIsDialogCancelable);
                                mAlertDialog.show();
                                break;
                            case SNACKBAR:
                                mSnackbar = UtilsDisplay.showUpdateAvailableSnackbar(mView, mViewAnchor,
                                        getDescriptionUpdate(mContext, update, Display.SNACKBAR),
                                        UtilsLibrary.getDurationEnumToBoolean(mDuration), mUpdateFrom,
                                        update.getUrlToDownload());
                                mSnackbar.show();
                                break;
                            case NOTIFICATION:
                                UtilsDisplay.showUpdateAvailableNotification(mContext, mNotifTitleUpdate,
                                        getDescriptionUpdate(mContext, update, Display.NOTIFICATION),
                                        mUpdateFrom, update.getUrlToDownload(), mIconResId);
                                break;
                        }
                    }
                    mLibraryPreferences.setSuccessfulChecks(successfulChecks + 1);
                } else if (mShowAppUpToDate) {
                    Log.d(TAG, "No Update Available");

                    switch (mDisplay) {
                        case DIALOG:
                            mAlertDialog = UtilsDisplay.showUpdateNotAvailableDialog(mContext,
                                    mTitleNoUpdate, getDescriptionNoUpdate(mContext));
                            mAlertDialog.setCancelable(mIsDialogCancelable);
                            mAlertDialog.show();
                            break;
                        case SNACKBAR:
                            mSnackbar = UtilsDisplay.showUpdateNotAvailableSnackbar(mView, mViewAnchor,
                                    getDescriptionNoUpdate(mContext),
                                    UtilsLibrary.getDurationEnumToBoolean(mDuration));
                            mSnackbar.show();
                            break;
                        case NOTIFICATION:
                            UtilsDisplay.showUpdateNotAvailableNotification(mContext, mTitleNoUpdate,
                                    getDescriptionNoUpdate(mContext), mIconResId);
                            break;
                    }
                } else {
                    Log.d(TAG, "No Update Available");
                }
            }

            @Override
            public void onFailed(AppUpdaterError error) {
                Log.d(TAG, "onFailed: " + error);
                if (mShowAppUpdateError) {
                    mSnackbar = UtilsDisplay.showUpdateFailedSnackbar(mView, null,
                            getDescriptionUpdateFailed(mContext),
                            UtilsLibrary.getDurationEnumToBoolean(mDuration));
                    mSnackbar.show();
                }
                if (error == AppUpdaterError.GITHUB_USER_REPO_INVALID) {
                    throw new IllegalArgumentException("GitHub user or repo is empty!");
                } else if (error == AppUpdaterError.JSON_URL_MALFORMED) {
                    throw new IllegalArgumentException("JSON file is not valid!");
                } else if (error == AppUpdaterError.JSON_ERROR) {
                    throw new IllegalArgumentException("JSON file error");
                }
            }
        });

        mLatestAppVersion.execute();
    }

    @Override
    public void stop() {
        if (mLatestAppVersion != null && !mLatestAppVersion.isCancelled()) {
            mLatestAppVersion.cancel(true);
        }
    }

    @Override
    public void dismiss() {
        if (mAlertDialog != null && mAlertDialog.isShowing()) {
            mAlertDialog.dismiss();
        }
        if (mSnackbar != null && mSnackbar.isShown()) {
            mSnackbar.dismiss();
        }
    }

    private String getDescriptionUpdate(Context context, Update update, Display display) {
        if (mDescriptionUpdate == null || TextUtils.isEmpty(mDescriptionUpdate)) {
            switch (display) {
                case DIALOG:
                    if (update.getReleaseNotes() != null && !TextUtils.isEmpty(update.getReleaseNotes())) {
                        if (TextUtils.isEmpty(mDescriptionUpdate)) {
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

                case NOTIFICATION:
                    return String.format(context.getResources().getString(
                            R.string.updater_update_available_description_notification),
                            update.getLatestVersion(), UtilsLibrary.getAppName(context));

            }
        }

        return mDescriptionUpdate;
    }

    private String getDialogTitleUpdate(Context context, Update update) {
        if (mDescriptionUpdate == null || TextUtils.isEmpty(mDescriptionUpdate)) {
            if (update.getReleaseNotes() != null && !TextUtils.isEmpty(update.getReleaseNotes())) {
                if (TextUtils.isEmpty(mTitleUpdate)) {
                    return String.format(context.getResources().getString(
                            R.string.updater_update_available_description_snackbar), update.getLatestVersion());
                } else {
                    return mTitleUpdate;
                }
            } else {
                return context.getResources().getString(R.string.updater_update_available);
            }
        }

        return mDescriptionUpdate;
    }

    private String getDescriptionNoUpdate(Context context) {
        if (mDescriptionNoUpdate == null) {
            return String.format(context.getResources().getString(R.string.updater_update_not_available_description),
                    UtilsLibrary.getAppName(context));
        } else {
            return mDescriptionNoUpdate;
        }
    }

    private String getDescriptionUpdateFailed(Context context) {
        if (mDescriptionUpdateFailed == null) {
            return context.getResources().getString(R.string.updater_update_check_failed);
        } else {
            return mDescriptionUpdateFailed;
        }
    }

}
