package com.weberbox.pifire.feature.dashboard.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.dashboard.DashData
import com.weberbox.pifire.core.model.dashboard.DashData.Dash
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.core.network.parseResponse
import com.weberbox.pifire.core.repo.ServerDataSync
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.feature.dashboard.data.api.DashApi
import com.weberbox.pifire.feature.dashboard.data.mapper.DashDtoToDataMapper
import com.weberbox.pifire.feature.dashboard.data.mapper.toDto
import com.weberbox.pifire.feature.dashboard.data.model.ProbeNotify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DashRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<DashData>,
    private val dashApi: DashApi,
    private val json: Json,
) : DashRepo {

    private val sync = ServerDataSync(
        currentServerUuid = { sessionStateHolder.currentServerUuid },
        publish = sessionStateHolder::setDashData,
        readCache = { serverUuid -> dataStore.data.firstOrNull()?.dashMap?.get(serverUuid) },
        writeCache = ::updateDashData,
    )

    override suspend fun getDashData(): Result<Dash, DataError> = sync.fetch(
        request = dashApi::getDashData,
        parse = { parseGetResponse(it, json, DashDtoToDataMapper) },
    )

    override suspend fun getCachedData(): Result<Dash, DataError.Local> = sync.cached()

    override suspend fun listenDashData(): Flow<Result<Dash, DataError>> = sync.listen(
        updates = dashApi.listenDashData(),
        parse = { parseResponse(it, json, DashDtoToDataMapper) },
    )

    override suspend fun updateDashData(dash: Dash) {
        dataStore.updateData { data ->
            if (dash.uuid.isBlank()) return@updateData data

            val updatedMap = data.dashMap.toMutableMap().apply {
                put(dash.uuid, dash)
            }

            data.copy(dashMap = updatedMap)
        }
    }

    override suspend fun sendSmokeMode(): Result<Unit, DataError> =
        dashApi.sendSmokeMode()

    override suspend fun sendStopMode(): Result<Unit, DataError> =
        dashApi.sendStopMode()

    override suspend fun sendStartGrill(): Result<Unit, DataError> =
        dashApi.sendStartGrill()

    override suspend fun sendMonitorMode(): Result<Unit, DataError> =
        dashApi.sendMonitorMode()

    override suspend fun sendShutdownMode(): Result<Unit, DataError> =
        dashApi.sendShutdownMode()

    override suspend fun sendRecipeUnPause(): Result<Unit, DataError> =
        dashApi.sendRecipeUnPause()

    override suspend fun setSmokePlus(enabled: Boolean): Result<Unit, DataError> =
        dashApi.setSmokePlus(enabled = enabled)

    override suspend fun setPWMControl(enabled: Boolean): Result<Unit, DataError> =
        dashApi.setPWMControl(enabled = enabled)

    override suspend fun sendPrimeGrill(primeAmount: Int, nextMode: String): Result<Unit, DataError> =
        dashApi.sendPrimeGrill(primeAmount = primeAmount, nextMode = nextMode)

    override suspend fun setGrillHoldTemp(temp: String): Result<Unit, DataError> =
        dashApi.setGrillHoldTemp(temp = temp)

    override suspend fun setProbeNotify(probeNotify: ProbeNotify): Result<Unit, DataError> =
        dashApi.setProbeNotify(notifyDto = probeNotify.toDto())

    override suspend fun checkHopperLevel(): Result<Unit, DataError> =
        dashApi.checkHopperLevel()

    override suspend fun sendToggleLidOpen(lidOpen: Boolean): Result<Unit, DataError> =
        dashApi.sendToggleLidOpen(lidOpen = lidOpen)

    override suspend fun sendToggleFan(enabled: Boolean): Result<Unit, DataError> =
        dashApi.sendToggleFan(enabled = enabled)

    override suspend fun sendToggleAuger(enabled: Boolean): Result<Unit, DataError> =
        dashApi.sendToggleAuger(enabled = enabled)

    override suspend fun sendToggleIgniter(enabled: Boolean): Result<Unit, DataError> =
        dashApi.sendToggleIgniter(enabled = enabled)

    override suspend fun sendTimerAction(action: String): Result<Unit, DataError> =
        dashApi.sendTimerAction(action = action)

    override suspend fun sendTimerTime(
        hours: Int,
        minutes: Int,
        shutdown: Boolean,
        keepWarm: Boolean,
    ): Result<Unit, DataError> = dashApi.sendTimerTime(
        hours = hours,
        minutes = minutes,
        shutdown = shutdown,
        keepWarm = keepWarm,
    )
}
