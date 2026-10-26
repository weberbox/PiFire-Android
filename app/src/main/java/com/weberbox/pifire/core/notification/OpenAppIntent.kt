package com.weberbox.pifire.core.notification

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.weberbox.pifire.application.MainActivity

/**
 * What a notification tap opens: the app as the launcher icon would. A running app comes to the
 * front on the screen it was showing (MainActivity is singleTask); otherwise it starts normally.
 * A TaskStackBuilder intent would clear the task and drop the user back on the landing screen.
 */
fun openAppPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        action = Intent.ACTION_MAIN
        addCategory(Intent.CATEGORY_LAUNCHER)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
    }
    return PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )
}
