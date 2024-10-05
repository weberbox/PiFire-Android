package com.weberbox.pifire.service;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.onesignal.OSMutableNotification;
import com.onesignal.OSNotification;
import com.onesignal.OSNotificationReceivedEvent;
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler;
import com.weberbox.pifire.MainActivity;
import com.weberbox.pifire.R;

@SuppressWarnings("unused")
public class NotificationServiceExtension implements OSRemoteNotificationReceivedHandler {

    @Override
    public void remoteNotificationReceived(Context context, OSNotificationReceivedEvent
            receivedEvent) {
        OSNotification notification = receivedEvent.getNotification();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        OSMutableNotification mutableNotification = notification.mutableCopy();
        mutableNotification.setExtender(builder -> {
            builder.setColor(ContextCompat.getColor(context, R.color.colorAccent));
            builder.setSmallIcon(R.drawable.ic_grill);
            builder.setAutoCancel(true);
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
            builder.setOnlyAlertOnce(false);
            builder.setCategory(NotificationCompat.CATEGORY_MESSAGE);
            builder.setWhen(System.currentTimeMillis());
            builder.setContentTitle(notification.getTitle());
            builder.setContentText(notification.getBody());
            builder.setContentIntent(pendingIntent);
            return builder;
        });

        receivedEvent.complete(mutableNotification);
    }
}
