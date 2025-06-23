package com.weberbox.pifire.core.service

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal.OSRemoteNotificationReceivedHandler
import com.weberbox.pifire.MainActivity
import com.weberbox.pifire.R

@Suppress("unused")
class NotificationServiceExtension : OSRemoteNotificationReceivedHandler {

    override fun remoteNotificationReceived(
        context: Context,
        receivedEvent: OSNotificationReceivedEvent
    ) {
        val notification = receivedEvent.notification

        val activityIntent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
        }

        val pendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(activityIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val mutableNotification = notification.mutableCopy()
        mutableNotification.setExtender { builder: NotificationCompat.Builder ->
            builder.setSmallIcon(R.drawable.ic_grill)
            builder.setAutoCancel(true)
            builder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            builder.setOnlyAlertOnce(false)
            builder.setCategory(NotificationCompat.CATEGORY_NAVIGATION)
            builder.setWhen(System.currentTimeMillis())
            builder.setContentTitle(notification.title)
            builder.setContentText(notification.body)
            builder.setContentIntent(pendingIntent)
        }

        receivedEvent.complete(mutableNotification)
    }
}