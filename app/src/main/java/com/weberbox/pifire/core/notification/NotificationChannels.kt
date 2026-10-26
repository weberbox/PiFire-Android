package com.weberbox.pifire.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.content.Context
import android.media.AudioAttributes
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.net.toUri
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R

/** Alert channels. Ids match the ones PiFire's OneSignal sends use, so both paths share them. */
enum class AlertChannel(
    @param:StringRes val idRes: Int,
    @param:StringRes val nameRes: Int,
    @param:StringRes val descriptionRes: Int,
    val soundRes: Int? = null,
    val useVibrationPattern: Boolean = false,
) {
    BASE(
        idRes = R.string.notification_channel_base,
        nameRes = R.string.notification_channel_base_name,
        descriptionRes = R.string.notification_desc_base,
    ),
    TEMP(
        idRes = R.string.notification_channel_temp,
        nameRes = R.string.notification_channel_temp_name,
        descriptionRes = R.string.notification_desc_temp,
        soundRes = R.raw.temp_achieved,
    ),
    TIMER(
        idRes = R.string.notification_channel_timer,
        nameRes = R.string.notification_channel_timer_name,
        descriptionRes = R.string.notification_desc_timer,
        soundRes = R.raw.timer_alarm,
        useVibrationPattern = true,
    ),
    ERROR(
        idRes = R.string.notification_channel_error,
        nameRes = R.string.notification_channel_error_name,
        descriptionRes = R.string.notification_desc_error,
        soundRes = R.raw.grill_error,
        useVibrationPattern = true,
    ),
    PELLETS(
        idRes = R.string.notification_channel_pellets,
        nameRes = R.string.notification_channel_pellets_name,
        descriptionRes = R.string.notification_desc_pellets,
        soundRes = R.raw.pellet_alarm,
        useVibrationPattern = true,
    ),
    RECIPE(
        idRes = R.string.notification_channel_recipe,
        nameRes = R.string.notification_channel_recipe_name,
        descriptionRes = R.string.notification_desc_recipe,
    ),
}

/** Creates (or updates the names of) every alert channel. Safe to call on every start. */
fun createAlertChannels(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    AlertChannel.entries.forEach { channel ->
        notificationManager.createNotificationChannel(buildChannel(context = context, channel = channel))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun buildChannel(
    context: Context,
    channel: AlertChannel,
) = NotificationChannel(
    context.getString(channel.idRes),
    context.getString(channel.nameRes),
    NotificationManager.IMPORTANCE_HIGH,
).apply {
    description = context.getString(channel.descriptionRes)
    enableLights(true)
    setShowBadge(true)
    enableVibration(true)
    lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
    if (channel.useVibrationPattern) {
        vibrationPattern = longArrayOf(1000, 1000, 1000, 1000, 1000)
    }
    channel.soundRes?.let { resourceId ->
        setSound(
            "${ContentResolver.SCHEME_ANDROID_RESOURCE}://${BuildConfig.APPLICATION_ID}/$resourceId".toUri(),
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build(),
        )
    }
}
