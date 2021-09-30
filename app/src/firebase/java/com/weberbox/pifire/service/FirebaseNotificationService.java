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
import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.utils.Log;

public class FirebaseNotificationService extends FirebaseMessagingService {
    private static final String TAG = FirebaseNotificationService.class.getSimpleName();

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Got new token: " + token);
    }

    @Override
    public void
    onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            showNotification(remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody());
        }
    }

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
        String channel_id = Constants.NOTIFICATIONS_CHANNEL;
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

        NotificationChannel notificationChannel = new NotificationChannel(
                channel_id, Constants.NOTIFICATIONS_TOPIC,
                NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(notificationChannel);

        notificationManager.notify(0, builder.build());
    }
}