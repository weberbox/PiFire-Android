package com.weberbox.pifire.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.weberbox.pifire.MainActivity;
import com.weberbox.pifire.R;

import timber.log.Timber;

public class FirebaseNotificationService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Timber.d("Got new token: %s", token);
    }

    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }
    }

    @SuppressWarnings("unused")
    private RemoteViews getCustomDesign(String title, String message) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(),
                R.layout.layout_notification);
        remoteViews.setTextViewText(R.id.notification_title, title);
        remoteViews.setTextViewText(R.id.notification_message, message);
        remoteViews.setImageViewResource(R.id.notification_icon, R.drawable.ic_grill);
        return remoteViews;
    }

    public void showNotification(String title, String message) {
        Intent intent = new Intent(this, MainActivity.class);
        String channel_id = getString(R.string.notification_channel_id);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat
                .Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.ic_grill)
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setAutoCancel(true)
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setOnlyAlertOnce(true)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent);

        //builder = builder.setContent(getCustomDesign(title, message));

        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);

        initNotificationChannel(notificationManager, channel_id);

        notificationManager.notify(0, builder.build());
    }

    private void initNotificationChannel(NotificationManager notificationManager, String channel_id) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    channel_id, getString(R.string.notification_channel_grill_name),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setDescription(getString(R.string.notification_desc_grill));
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }
}