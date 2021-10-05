package com.weberbox.pifire.updater.interfaces;

import android.content.DialogInterface;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.weberbox.pifire.updater.AppUpdater;
import com.weberbox.pifire.updater.DisableClickListener;
import com.weberbox.pifire.updater.UpdateClickListener;
import com.weberbox.pifire.updater.enums.AppUpdaterError;
import com.weberbox.pifire.updater.enums.Display;
import com.weberbox.pifire.updater.enums.Duration;
import com.weberbox.pifire.updater.enums.UpdateFrom;
import com.weberbox.pifire.updater.objects.Update;

public interface IAppUpdater {
    AppUpdater setDisplay(Display display);

    /**
     * Set the source where the latest update can be found. Default: JSON.
     *
     * @param updateFrom source where the latest update is uploaded. If GITHUB is selected,
     *                   .setGitHubAndRepo method is required.
     * @return this
     */
    AppUpdater setUpdateFrom(UpdateFrom updateFrom);

    /**
     * Set the duration of the Snackbar Default: NORMAL.
     *
     * @param duration duration of the Snackbar
     * @return this
     *
     */
    AppUpdater setDuration(Duration duration);

    /**
     * Set the view where Snackbar will be placed.
     *
     * @param view view where Snackbar will be placed. This is required for snackbar
     * @return this
     */
    AppUpdater setView(View view);

    /**
     * Set the Snackbar Anchor
     *
     * @param view View the snackbar will be anchored to. This is required for snackbar
     * @return this
     */
    AppUpdater setViewAnchor(View view);

    /**
     * Set the user and repo where the releases are uploaded. You must upload your updates as a
     * release in order to work properly tagging them as vX.X.X or X.X.X.
     *
     * @param user GitHub user
     * @param repo GitHub repository
     * @return this
     */
    AppUpdater setGitHubUserAndRepo(@NonNull String user, @NonNull String repo);

    /**
     * Set the url to the json file with the latest version info.
     *
     * @param jsonUrl file
     * @return this
     */

    AppUpdater setUpdateJSON(@NonNull String jsonUrl);

    /**
     * Set the times the app ascertains that a new update is available and display a dialog,
     * Snackbar or notification. It makes the updates less invasive. Default: 1.
     *
     * @param times every X times
     * @return this
     */
    AppUpdater showEvery(Integer times);

    /**
     * Set if the dialog, Snackbar or notification is displayed although there aren't updates.
     * Default: false.
     *
     * @param res true to show, false otherwise
     * @return this
     */
    AppUpdater showAppUpToDate(Boolean res);

    /**
     * Set if the Snackbar is displayed on app update failure.
     * Default: false.
     *
     * @param res true to show, false otherwise
     * @return this
     */
    AppUpdater showAppUpdateError(Boolean res);

    /**
     * Set a custom description for the Snackbar when an update fails.
     *
     * @param text for the Snackbar
     * @return this
     */
    AppUpdater setAppUpdateCheckFailedText(@NonNull String text);

    /**
     * Set a custom title for the dialog when an update is available.
     *
     * @param title for the dialog
     * @return this
     */
    AppUpdater setTitleOnUpdateAvailable(@NonNull String title);

    /**
     * Set a custom title for the dialog when an update is available.
     *
     * @param textResource resource from the strings xml file for the dialog
     * @return this
     */
    AppUpdater setTitleOnUpdateAvailable(@StringRes int textResource);

    /**
     * Set a custom description for the dialog when an update is available.
     *
     * @param description for the dialog
     * @return this
     */
    AppUpdater setContentOnUpdateAvailable(@NonNull String description);

    /**
     * Set a custom description for the dialog when an update is available.
     *
     * @param textResource resource from the strings xml file for the dialog
     * @return this
     */
    AppUpdater setContentOnUpdateAvailable(@StringRes int textResource);

    /**
     * Set a custom title for the dialog when no update is available.
     *
     * @param title for the dialog
     * @return this
     */
    AppUpdater setTitleOnUpdateNotAvailable(@NonNull String title);

    /**
     * Set a custom title for the dialog when no update is available.
     *
     * @param textResource resource from the strings xml file for the dialog
     * @return this
     */
    AppUpdater setTitleOnUpdateNotAvailable(@StringRes int textResource);

    /**
     * Set a custom description for the dialog when no update is available.
     *
     * @param description for the dialog
     * @return this
     */
    AppUpdater setContentOnUpdateNotAvailable(@NonNull String description);

    /**
     * Set a custom description for the dialog when no update is available.
     *
     * @param textResource resource from the strings xml file for the dialog
     * @return this
     */
    AppUpdater setContentOnUpdateNotAvailable(@StringRes int textResource);

    /**
     * Set a custom "Update" button text when a new update is available.
     *
     * @param text for the update button
     * @return this
     */
    AppUpdater setButtonUpdate(@NonNull String text);

    /**
     * Set a custom "Update" button text when a new update is available.
     *
     * @param textResource resource from the strings xml file for the update button
     * @return this
     */
    AppUpdater setButtonUpdate(@StringRes int textResource);

    /**
     * Set a custom "Dismiss" button text when a new update is available.
     *
     * @param text for the dismiss button
     * @return this
     */
    AppUpdater setButtonDismiss(@NonNull String text);

    /**
     * Set a custom "Dismiss" button text when a new update is available.
     *
     * @param textResource resource from the strings xml file for the dismiss button
     * @return this
     */
    AppUpdater setButtonDismiss(@StringRes int textResource);

    /**
     * Set a custom "Don't show again" button text when a new update is available.
     *
     * @param text for the disable button
     * @return this
     */
    AppUpdater setButtonDoNotShowAgain(@NonNull String text);

    /**
     * Set a custom "Don't show again" button text when a new update is available.
     *
     * @param textResource resource from the strings xml file for the disable button
     * @return this
     */
    AppUpdater setButtonDoNotShowAgain(@StringRes int textResource);

    /**
     * Enable or disable the "Don't show again" button
     *
     * @param isShown resource from the strings xml file for the disable button
     * @return this
     */
    AppUpdater setButtonDoNotShowAgain(Boolean isShown);

    /**
     * Enable or disable the Dismiss button, usually used for forcing updates
     *
     * @param isShown false to remove dismiss button true to show it
     * @return this
     */
    AppUpdater setButtonDismissEnabled(Boolean isShown);

    /**
     * Sets a custom click listener for the "Update" button when a new update is available.
     * In order to maintain the default functionality, extend {@link UpdateClickListener}
     *
     * @param clickListener for update button
     * @return this
     */
    AppUpdater setButtonUpdateClickListener(DialogInterface.OnClickListener clickListener);

    /**
     * Sets a custom click listener for the "Dismiss" button when a new update is available.
     *
     * @param clickListener for dismiss button
     * @return this
     */
    AppUpdater setButtonDismissClickListener(DialogInterface.OnClickListener clickListener);

    /**
     * Sets a custom click listener for the "Don't show again" button when a new update is available. <br/>
     * In order to maintain the default functionality, extend {@link DisableClickListener}
     *
     * @param clickListener for disable button
     * @return this
     */
    AppUpdater setButtonDoNotShowAgainClickListener(DialogInterface.OnClickListener clickListener);

    /**
     * Sets the resource identifier for the small notification icon
     *
     * @param iconRes The id of the drawable item
     * @return this
     */
    AppUpdater setIcon(@DrawableRes int iconRes);

    /**
     * Make update dialog non-cancelable, and
     * force user to make update
     *  @param isCancelable true to force user to make update, false otherwise
     *  @return this
     */
    AppUpdater setCancelable(Boolean isCancelable);

    /**
     * Set the options to force the update check even if it is disabled
     *
     *  @param forceCheck true to force the update check
     *  @return this
     */
    AppUpdater setForceCheck(Boolean forceCheck);

    /**
     * Execute AppUpdater in background.
     */
    void start();

    /**
     * Stops the execution of AppUpdater.
     */
    void stop();

    /**
     * Dismisses the alert dialog or the snackbar.
     */
    void dismiss();

    interface LibraryListener {
        void onSuccess(Update update);

        void onFailed(AppUpdaterError error);
    }
}
