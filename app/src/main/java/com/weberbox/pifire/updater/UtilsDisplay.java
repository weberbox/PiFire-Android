package com.weberbox.pifire.updater;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.NotificationCompat;

import com.google.android.material.snackbar.Snackbar;
import com.tapadoo.alerter.Alerter;
import com.weberbox.pifire.MainActivity;
import com.weberbox.pifire.R;
import com.weberbox.pifire.updater.objects.Update;

import java.net.URL;

public class UtilsDisplay {

    public static AlertDialog showUpdateAvailableDialog(
            final Context context, String title, String content, String btnNegative,
            String btnPositive, String btnNeutral,
            final DialogInterface.OnClickListener updateClickListener,
            final DialogInterface.OnClickListener dismissClickListener,
            final DialogInterface.OnClickListener disableClickListener,
            Boolean isDisableShown, Boolean isDismissShown, Boolean forceUpdateFinish) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context,
                R.style.AlertDialogThemeMaterial);
        builder.setTitle(title)
                .setMessage(content)
                .setPositiveButton(btnPositive, updateClickListener);

        if (isDisableShown) {
            builder.setNeutralButton(btnNeutral, disableClickListener);
        }

        if (isDismissShown) {
            builder.setNegativeButton(btnNegative, dismissClickListener);
        }

        if (forceUpdateFinish) {
            builder.setOnKeyListener(new ForceUpdateKeyListener(context));
        }

        return builder.create();
    }

    public static AlertDialog showUpdateNotAvailableDialog(
            final Context context, String title, String content) {
        return new AlertDialog.Builder(context, R.style.AlertDialogThemeMaterial)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(context.getResources().getString(android.R.string.ok),
                        (dialog, i) -> dialog.dismiss())
                .create();
    }

    public static Snackbar showUpdateAvailableSnackbar(
            View view, View anchor, String content, boolean indefinite, final Update update) {

        final Context context = view.getContext();
        int snackbarTime = indefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG;

        Snackbar snackbar = Snackbar.make(view, content, snackbarTime);
        if (anchor != null) {
            snackbar.setAnchorView(anchor);
        }

        snackbar.setAction(context.getResources().getString(R.string.update_button), view1 -> UtilsLibrary.getAppUpdate(context, update));

        return snackbar;
    }

    public static Snackbar showUpdateNotAvailableSnackbar(
            View view, View anchor, String content, boolean indefinite) {

        int snackbarTime = indefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG;

        Snackbar snackbar = Snackbar.make(view, content, snackbarTime);
        if (anchor != null) {
            snackbar.setAnchorView(anchor);
        }

        return snackbar;
    }

    public static Snackbar showUpdateFailedSnackbar(
            View view, View anchor, String content, boolean indefinite) {

        int snackbarTime = indefinite ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG;

        Snackbar snackbar = Snackbar.make(view, content, snackbarTime);
        if (anchor != null) {
            snackbar.setAnchorView(anchor);
        }

        return snackbar;
    }

    public static Alerter showUpdateAvailableAlert(
            Activity activity, String content, boolean indefinite, final Update update) {
        return Alerter.create(activity)
                .setText(content)
                .setIcon(R.drawable.ic_update)
                .setBackgroundColorRes(R.color.colorPrimaryLighter)
                .enableSwipeToDismiss()
                .setTextAppearance(R.style.Text14Aller)
                .enableInfiniteDuration(indefinite)
                .setIconSize(R.dimen.alerter_icon_size_small)
                .addButton(activity.getResources().getString(R.string.update_button),
                        R.style.AlerterButton, v -> UtilsLibrary.getAppUpdate(activity, update));

    }

    public static Alerter showUpdateNotAvailableAlert(
            Activity activity, String content, boolean indefinite) {
        return Alerter.create(activity)
                .setText(content)
                .setIcon(R.drawable.ic_no_update)
                .setBackgroundColorRes(R.color.colorPrimaryLighter)
                .enableSwipeToDismiss()
                .setTextAppearance(R.style.Text14Aller)
                .enableInfiniteDuration(indefinite)
                .setIconSize(R.dimen.alerter_icon_size_small);
    }

    public static Alerter showUpdateFailedAlert(
            Activity activity, String content, boolean indefinite) {
        return Alerter.create(activity)
                .setText(content)
                .setIcon(R.drawable.ic_error)
                .setBackgroundColorRes(R.color.colorAccentRed)
                .enableSwipeToDismiss()
                .setTextAppearance(R.style.Text14Aller)
                .enableInfiniteDuration(indefinite)
                .setIconSize(R.dimen.alerter_icon_size_small);
    }

    public static void showUpdateAvailableNotification(
            Context context, String title, String content, URL apk, int smallIconResourceId) {

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        initNotificationChannel(context, notificationManager);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                context.getPackageManager().getLaunchIntentForPackage(
                        UtilsLibrary.getAppPackageName(context)),
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        PendingIntent pendingIntentUpdate = PendingIntent.getActivity(context, 0,
                UtilsLibrary.intentToUpdate(apk),
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = getBaseNotification(context, contentIntent, title,
                content, smallIconResourceId)
                .addAction(R.drawable.ic_update, context.getResources().getString(
                        R.string.update_button), pendingIntentUpdate);

        notificationManager.notify(0, builder.build());
    }

    public static void showUpdateNotAvailableNotification(
            Context context, String title, String content, int smallIconResourceId) {

        Intent intent = new Intent(context, MainActivity.class);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        initNotificationChannel(context, notificationManager);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = getBaseNotification(context, contentIntent, title,
                content, smallIconResourceId)
                .setAutoCancel(true);

        notificationManager.notify(0, builder.build());
    }

    private static NotificationCompat.Builder getBaseNotification(Context context, PendingIntent
            contentIntent, String title, String content, int smallIconResourceId) {
        return new NotificationCompat.Builder(context, context.getString(
                R.string.notification_channel_updates))
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(smallIconResourceId)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

    }

    @TargetApi(26)
    private static void initNotificationChannel(Context context, NotificationManager notificationManager) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    context.getString(R.string.notification_channel_updates),
                    context.getString(R.string.notification_channel_updates_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(context.getString(R.string.notification_desc_updates));
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

}
