package com.weberbox.pifire.core.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.repo.PushRepo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject

/**
 * Receives relay alerts sent through the app's own Firebase project. OneSignal's messages arrive
 * through OneSignal's own receiver and never reach this service.
 */
@AndroidEntryPoint
class PushMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var pushRepo: PushRepo

    @Inject
    lateinit var alertNotifier: AlertNotifier

    @Inject
    lateinit var alertDeduper: AlertDeduper

    @Inject
    lateinit var json: Json

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        // If the process dies first, the next getPushKey sees the changed token and updates the relay.
        scope.launch {
            val result = pushRepo.onNewFcmToken(token)
            if (result is Result.Error) Timber.w("Push token update failed: %s", result.error)
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val alert = parsePushAlert(data = message.data, json = json) ?: return
        if (!alertDeduper.isNew(alert.alertId)) return
        Timber.d("Push alert received: %s", alert.type)
        // Runs on a background thread, and the notification must be posted before this returns.
        runBlocking { alertNotifier.show(alert) }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }
}
