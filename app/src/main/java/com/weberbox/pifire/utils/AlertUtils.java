package com.weberbox.pifire.utils;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.pixplicity.easyprefs.library.Prefs;
import com.tapadoo.alerter.Alerter;
import com.tapadoo.alerter.OnHideAlertListener;
import com.weberbox.pifire.R;

public class AlertUtils {

    public static void toggleOfflineAlert(Alerter alerter, boolean connected, boolean dismissed) {
        if (!Alerter.isShowing() && !connected) {
            alerter.show();
        } else if (Alerter.isShowing() && connected) {
            Alerter.hide();
        } else if (Alerter.isShowing() && dismissed && connected) {
            Alerter.hide();
        }
    }

    public static void createOfflineAlert(Activity activity) {
        Alerter.create(activity)
                .setText(R.string.control_not_connected)
                .setIcon(R.drawable.ic_grill_disconnected)
                .setBackgroundColorRes(R.color.colorAccentRed)
                .enableSwipeToDismiss()
                .setTextAppearance(R.style.Text14Aller)
                .enableInfiniteDuration(true)
                .setIconSize(R.dimen.alerter_icon_size_small)
                .show();
    }

    public static Alerter createOfflineAlert(Activity activity, OnHideAlertListener listener) {
        return Alerter.create(activity)
                .setText(R.string.control_not_connected)
                .setIcon(R.drawable.ic_grill_disconnected)
                .setBackgroundColorRes(R.color.colorAccentRed)
                .enableSwipeToDismiss()
                .setTextAppearance(R.style.Text14Aller)
                .enableInfiniteDuration(true)
                .setOnHideListener(listener)
                .setIconSize(R.dimen.alerter_icon_size_small);
    }

    public static void createErrorAlert(Activity activity, @StringRes int message,
                                        boolean infinite) {
        Alerter.create(activity)
                .setText(message)
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorAccentRed)
                .enableSwipeToDismiss()
                .setTextAppearance(R.style.Text14Aller)
                .enableInfiniteDuration(infinite)
                .setIconSize(R.dimen.alerter_icon_size_small)
                .show();
    }

    public static void createErrorAlert(Activity activity, @NonNull String message,
                                        boolean infinite) {
        Alerter.create(activity)
                .setText(message)
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorAccentRed)
                .enableSwipeToDismiss()
                .setTextAppearance(R.style.Text14Aller)
                .enableInfiniteDuration(infinite)
                .setIconSize(R.dimen.alerter_icon_size_small)
                .show();
    }

    @SuppressWarnings("unused")
    public static void createAlert(Activity activity, @StringRes int message, boolean infinite) {
        Alerter.create(activity)
                .setText(message)
                .setIcon(R.drawable.ic_menu_about)
                .setBackgroundColorRes(R.color.colorPrimaryLighter)
                .enableSwipeToDismiss()
                .setTextAppearance(R.style.Text14Aller)
                .enableInfiniteDuration(infinite)
                .setIconSize(R.dimen.alerter_icon_size_small)
                .show();
    }

    public static void createAlert(Activity activity, @StringRes int message, long duration) {
        Alerter.create(activity)
                .setText(message)
                .setIcon(R.drawable.ic_menu_about)
                .setBackgroundColorRes(R.color.colorPrimaryLighter)
                .enableSwipeToDismiss()
                .setTextAppearance(R.style.Text14Aller)
                .setDuration(duration)
                .setIconSize(R.dimen.alerter_icon_size_small)
                .show();
    }

    public static void createOneSignalAlert(Activity activity, int title, int message) {
        Alerter.create(activity)
                .setTitle(title)
                .setText(message)
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorAccentRed)
                .enableSwipeToDismiss()
                .setTitleAppearance(R.style.Text18Aller)
                .setTextAppearance(R.style.Text14Aller)
                .enableInfiniteDuration(false)
                .show();
    }

    public static void createOneSignalConsentAlert(Activity activity, int title, int message) {
        Alerter.create(activity)
                .setTitle(title)
                .setText(message)
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorAccentRed)
                .enableSwipeToDismiss()
                .setTitleAppearance(R.style.Text18Aller)
                .setTextAppearance(R.style.Text14Aller)
                .enableInfiniteDuration(true)
                .addButton(activity.getString(R.string.dismiss_button), R.style.AlerterButton, v -> {
                    Prefs.putBoolean(activity.getString(R.string.prefs_onesignal_consent_dismiss),
                            true);
                    Alerter.hide();
                })
                .show();
    }
}
