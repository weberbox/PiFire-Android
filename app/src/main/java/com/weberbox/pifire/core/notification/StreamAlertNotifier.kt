package com.weberbox.pifire.core.notification

import com.weberbox.pifire.core.model.push.PushAlert
import com.weberbox.pifire.core.network.transport.HttpSseTransport
import com.weberbox.pifire.core.session.SessionStateHolder
import dagger.Lazy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Shows alerts from the v2 live stream while the app is connected (docs/PUSH.md 4.3). PiFire also
 * sends each alert through the relay; [AlertDeduper] drops whichever copy arrives second, so the
 * stream is the fast path (and works on the LAN without internet) and the relay covers a closed app.
 */
@Singleton
class StreamAlertNotifier @Inject constructor(
    private val httpSseTransport: HttpSseTransport,
    private val alertNotifier: AlertNotifier,
    private val alertDeduper: AlertDeduper,
    private val sessionStateHolder: SessionStateHolder,
    // Lazy: creating OneSignalManager initializes the OneSignal SDK, which shouldn't happen at app start.
    private val oneSignalManager: Lazy<OneSignalManager>,
    private val json: Json,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val started = AtomicBoolean(false)

    /** Starts collecting for the life of the process; later calls do nothing. */
    fun start() {
        if (!started.compareAndSet(false, true)) return
        scope.launch {
            httpSseTransport.alerts.collect { raw ->
                try {
                    handle(raw)
                } catch (e: Exception) {
                    Timber.e(e, "Stream alert failed")
                }
            }
        }
    }

    internal suspend fun handle(raw: String) {
        val alert = parseStreamAlert(raw = raw, json = json) ?: return
        if (!shouldShow(alert) || !alertDeduper.isNew(alert.alertId)) return
        Timber.d("Stream alert received: %s", alert.type)
        alertNotifier.show(alert)
    }

    // PiFire only streams alerts with the grill's App Push switch on, so the switch needs no check here
    // (the app's copy of the settings can lag behind a change). Push consent from setup still applies.
    private fun shouldShow(alert: PushAlert): Boolean {
        val server = sessionStateHolder.settingsDataState.value ?: return false
        if (server.uuid.isNotEmpty() && alert.serverUuid.isNotEmpty() && server.uuid != alert.serverUuid) return false
        return oneSignalManager.get().getUserConsent()
    }
}
