package com.weberbox.pifire.core.network.transport

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.data.pref.Pref
import com.weberbox.pifire.core.data.pref.Prefs
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.SocketManager
import com.weberbox.pifire.core.remoteconfig.Feature
import com.weberbox.pifire.core.remoteconfig.FeatureSupport
import com.weberbox.pifire.core.remoteconfig.RemoteConfigRepo
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The app's [PiFireTransport]: picks Socket.IO or API v2 on every connect and forwards to it.
 * v2 is used only when it's switched on (Remote Config `MobileApiV2`, or the debug override), the
 * server answers `/mobile/v2/hello`, and the version it reports is in the Remote Config range
 * (`minVersion`/`minBuild`/`maxVersion`/`maxBuild`). Anything else uses Socket.IO, which reports its own
 * connection errors as before, so a v2 problem never locks a user out.
 */
@Singleton
class PiFireConnection @Inject constructor(
    private val socketManager: SocketManager,
    private val httpSseTransport: HttpSseTransport,
    private val remoteConfigRepo: RemoteConfigRepo,
    private val prefs: Prefs,
) : PiFireTransport {

    private val active = MutableStateFlow<PiFireTransport>(socketManager)

    /** True while connected through API v2. */
    val isApiV2: Boolean
        get() = active.value === httpSseTransport

    /** [isApiV2], updated on every connect. */
    val isApiV2Flow: Flow<Boolean> = active.map { it === httpSseTransport }

    override suspend fun connect(
        server: Server,
        disconnectOnCancellation: Boolean,
    ): Boolean {
        disconnect()
        val transport = chooseTransport(server)
        Timber.i("Connecting with %s", if (transport === httpSseTransport) "API v2" else "Socket.IO")
        active.value = transport
        return transport.connect(server = server, disconnectOnCancellation = disconnectOnCancellation)
    }

    override fun disconnect() {
        active.value.disconnect()
    }

    override fun isConnected(): Boolean = active.value.isConnected()

    override suspend fun get(vararg args: Any): Result<String, DataError> = active.value.get(*args)

    override suspend fun postResult(vararg args: Any): Result<String, DataError> = active.value.postResult(*args)

    override suspend fun post(vararg args: Any): Result<Unit, DataError> = active.value.post(*args)

    // Follows the transport across reconnects, like Socket.IO listeners follow a new socket.
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun listen(event: String): Flow<Result<Array<out Any?>, DataError>> = active.flatMapLatest { it.listen(event) }

    private suspend fun chooseTransport(server: Server): PiFireTransport {
        val forced = AppConfig.DEBUG && prefs.get(Pref.forceMobileApiV2)
        val features = if (forced) emptyMap() else remoteConfigRepo.fetchFeatureSupportConfig()
        if (!forced && features[Feature.MobileApiV2.name]?.enabled != true) return socketManager
        return when (val probe = httpSseTransport.probe(server)) {
            is HttpSseTransport.ProbeResult.Available -> {
                // The Remote Config range can rule out PiFire builds whose v2 misbehaves.
                val supported = forced || FeatureSupport(
                    currentVersion = probe.serverVersion,
                    currentBuild = probe.serverBuild,
                    featureMap = features,
                ).isSupported(Feature.MobileApiV2)
                if (supported) httpSseTransport else socketManager
            }

            HttpSseTransport.ProbeResult.NotAvailable -> socketManager

            is HttpSseTransport.ProbeResult.Failed -> {
                Timber.d("API v2 probe failed (%s), using Socket.IO", probe.error)
                socketManager
            }
        }
    }
}
