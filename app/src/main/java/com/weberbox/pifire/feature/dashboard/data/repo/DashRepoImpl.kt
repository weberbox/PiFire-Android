package com.weberbox.pifire.feature.dashboard.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.dashboard.DashData
import com.weberbox.pifire.core.model.dashboard.DashData.Dash
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.core.network.parseResponse
import com.weberbox.pifire.core.session.SessionStateHolder
import com.weberbox.pifire.feature.dashboard.data.api.DashApi
import com.weberbox.pifire.feature.dashboard.data.mapper.DashDtoToDataMapper
import com.weberbox.pifire.feature.dashboard.data.mapper.toDto
import com.weberbox.pifire.feature.dashboard.data.model.ProbeNotify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DashRepoImpl @Inject constructor(
    private val sessionStateHolder: SessionStateHolder,
    private val dataStore: DataStore<DashData>,
    private val dashApi: DashApi,
    private val json: Json,
) : DashRepo {

    override suspend fun getDashData(): Result<Dash, DataError> = withContext(Dispatchers.IO) {
        dashApi.getDashData().let { result ->
            when (result) {
                is Result.Error -> Result.Error(result.error)

                is Result.Success -> {
                    parseGetResponse(result.data, json, DashDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> Result.Error(data.error)

                            is Result.Success -> {
                                sessionStateHolder.setDashData(data.data)
                                updateDashData(data.data)
                                Result.Success(data.data)
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun getCachedData(): Result<Dash, DataError.Local> = withContext(Dispatchers.IO) {
        val data = dataStore.data.firstOrNull()
        val currentServer = sessionStateHolder.currentServerUuid
        if (data != null && currentServer.isNotBlank()) {
            data.dashMap[currentServer]?.let { dash ->
                sessionStateHolder.setDashData(dash)
                Result.Success(dash)
            } ?: Result.Error(DataError.Local.NO_CACHED_DATA)
        } else {
            Result.Error(DataError.Local.NO_CACHED_DATA)
        }
    }

    override suspend fun listenDashData(): Flow<Result<Dash, DataError>> {
        val flow = dashApi.listenDashData()
        val parsed: Flow<Result<Dash, DataError>> = flow.transform { result ->
            when (result) {
                is Result.Error -> emit(Result.Error(result.error))

                is Result.Success -> {
                    parseResponse(result.data, json, DashDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> emit(Result.Error(data.error))

                            is Result.Success -> {
                                sessionStateHolder.setDashData(data.data)
                                emit(Result.Success(data.data))
                            }
                        }
                    }
                }
            }
        }
        return parsed.flowOn(Dispatchers.IO)
    }

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
