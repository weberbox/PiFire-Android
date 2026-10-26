package com.weberbox.pifire.core.notification

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.TaskStackBuilder
import com.weberbox.pifire.R
import com.weberbox.pifire.application.MainActivity
import com.weberbox.pifire.core.model.push.PushAlert
import com.weberbox.pifire.core.repo.SettingsRepo
import com.weberbox.pifire.core.ui.util.checkPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/** Posts the notification for a [PushAlert]. */
class AlertNotifier @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val settingsRepo: SettingsRepo,
) {

    suspend fun show(alert: PushAlert) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !context.checkPermission(Manifest.permission.POST_NOTIFICATIONS)
        ) {
            Timber.d("Notification permission denied, dropping alert")
            return
        }
        val content = alert.toAlertContent()
        // The saved server's name tells alerts from several grills apart. Unknown or slow: no name.
        val serverName = withTimeoutOrNull(SERVER_LOOKUP_TIMEOUT_MS.milliseconds) {
            settingsRepo.getCurrentServerItem(alert.serverUuid)?.name
        }?.takeIf { it.isNotBlank() }

        val notification = NotificationCompat.Builder(context, context.getString(content.channel.idRes))
            .setSmallIcon(R.drawable.ic_grill)
            .setContentTitle(content.title.asString(context))
            .setContentText(content.body.asString(context))
            .setStyle(NotificationCompat.BigTextStyle().bigText(content.body.asString(context)))
            .setSubText(serverName)
            .setWhen(alert.timeMillis ?: System.currentTimeMillis())
            .setShowWhen(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(openAppIntent())
            .build()

        try {
            NotificationManagerCompat.from(context).notify(alert.alertId.hashCode(), notification)
        } catch (e: SecurityException) {
            // Permission revoked between the check and the post.
            Timber.w(e, "Couldn't post alert notification")
        }
    }

    private fun openAppIntent(): PendingIntent? {
        val intent = Intent(context, MainActivity::class.java).apply {
            action = Intent.ACTION_VIEW
        }
        return TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(intent)
            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private companion object {
        const val SERVER_LOOKUP_TIMEOUT_MS = 2_000L
    }
}
