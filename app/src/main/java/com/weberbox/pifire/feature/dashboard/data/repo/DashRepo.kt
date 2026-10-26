package com.weberbox.pifire.feature.dashboard.data.repo

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.dashboard.DashData.Dash
import com.weberbox.pifire.feature.dashboard.data.model.ProbeNotify
import kotlinx.coroutines.flow.Flow

interface DashRepo {
    suspend fun getDashData(): Result<Dash, DataError>
    suspend fun getCachedData(): Result<Dash, DataError.Local>
    suspend fun listenDashData(): Flow<Result<Dash, DataError>>
    suspend fun updateDashData(dash: Dash)

    suspend fun sendSmokeMode(): Result<Unit, DataError>
    suspend fun sendStopMode(): Result<Unit, DataError>
    suspend fun sendStartGrill(): Result<Unit, DataError>
    suspend fun sendMonitorMode(): Result<Unit, DataError>
    suspend fun sendShutdownMode(): Result<Unit, DataError>
    suspend fun sendRecipeUnPause(): Result<Unit, DataError>
    suspend fun setSmokePlus(enabled: Boolean): Result<Unit, DataError>
    suspend fun setPWMControl(enabled: Boolean): Result<Unit, DataError>
    suspend fun sendPrimeGrill(primeAmount: Int, nextMode: String): Result<Unit, DataError>
    suspend fun setGrillHoldTemp(temp: String): Result<Unit, DataError>
    suspend fun setProbeNotify(probeNotify: ProbeNotify): Result<Unit, DataError>
    suspend fun checkHopperLevel(): Result<Unit, DataError>
    suspend fun sendToggleLidOpen(lidOpen: Boolean): Result<Unit, DataError>
    suspend fun sendToggleFan(enabled: Boolean): Result<Unit, DataError>
    suspend fun sendToggleAuger(enabled: Boolean): Result<Unit, DataError>
    suspend fun sendToggleIgniter(enabled: Boolean): Result<Unit, DataError>
    suspend fun sendTimerAction(action: String): Result<Unit, DataError>
    suspend fun sendTimerTime(
        hours: Int,
        minutes: Int,
        shutdown: Boolean,
        keepWarm: Boolean,
    ): Result<Unit, DataError>
}
