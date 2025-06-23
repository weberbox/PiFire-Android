package com.weberbox.pifire.dashboard.data.api

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.model.PostDto.NotifyDto
import kotlinx.coroutines.flow.Flow

interface DashApi {
    suspend fun requestDashUpdate(): Result<Unit, DataError>
    suspend fun getDashData(): Result<String, DataError>
    suspend fun listenDashData(): Flow<Result<String, DataError>>
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
    suspend fun setProbeNotify(notifyDto: NotifyDto): Result<Unit, DataError>
    suspend fun checkHopperLevel(): Result<Unit, DataError>
    suspend fun triggerLidOpen(): Result<Unit, DataError>
    suspend fun sendToggleFan(enabled: Boolean): Result<Unit, DataError>
    suspend fun sendToggleAuger(enabled: Boolean): Result<Unit, DataError>
    suspend fun sendToggleIgniter(enabled: Boolean): Result<Unit, DataError>
    suspend fun sendTimerAction(action: String): Result<Unit, DataError>
    suspend fun sendTimerTime(hours: Int, minutes: Int, shutdown: Boolean, keepWarm: Boolean
    ): Result<Unit, DataError>
}