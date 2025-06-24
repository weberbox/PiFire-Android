package com.weberbox.pifire.dashboard.data.api

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.model.PostDto
import com.weberbox.pifire.common.data.model.PostDto.ControlDto
import com.weberbox.pifire.common.data.model.PostDto.ControlDto.Recipe
import com.weberbox.pifire.common.data.model.PostDto.ControlDto.StepData
import com.weberbox.pifire.common.data.model.PostDto.NotifyDto
import com.weberbox.pifire.common.data.model.PostDto.TimerDto
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.singleton.SocketManager
import com.weberbox.pifire.dashboard.presentation.model.RunningMode
import com.weberbox.pifire.settings.data.model.remote.ManualDto
import com.weberbox.pifire.settings.data.model.remote.ManualDto.Manual
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.json.Json
import javax.inject.Inject

class DashApiImpl @Inject constructor(
    private val socketManager: SocketManager,
    private val json: Json
) : DashApi {

    @Suppress("unused")
    override suspend fun requestDashUpdate(): Result<Unit, DataError> {
        return socketManager.emitPost(ServerConstants.GF_LISTEN_APP_DATA, true)
    }

    override suspend fun getDashData(): Result<String, DataError> {
        return socketManager.emitGet(ServerConstants.GA_DASH_DATA)
    }

    override suspend fun listenDashData(): Flow<Result<String, DataError>> {
        val flow = socketManager.onFlow(ServerConstants.LISTEN_DASH_DATA)
        return flow.transform { result ->
            when (result) {
                is Result.Error -> emit(Result.Error(result.error))
                is Result.Success -> {
                    if (result.data.isNotEmpty() && result.data[0] != null) {
                        emit(Result.Success(result.data[0].toString()))
                    } else {
                        emit(Result.Error(DataError.Network.SERVER_ERROR))
                    }
                }
            }
        }
    }

    override suspend fun sendSmokeMode(): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(currentMode = RunningMode.Smoke.name, updated = true)
        )
        return sendEmitPost(json)
    }

    override suspend fun sendStopMode(): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(currentMode = RunningMode.Stop.name, updated = true)
        )
        return sendEmitPost(json)
    }

    override suspend fun sendStartGrill(): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(currentMode = RunningMode.Startup.name, updated = true)
        )
        return sendEmitPost(json)
    }

    override suspend fun sendMonitorMode(): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(currentMode = RunningMode.Monitor.name, updated = true)
        )
        return sendEmitPost(json)
    }

    override suspend fun sendShutdownMode(): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(currentMode = RunningMode.Shutdown.name, updated = true)
        )
        return sendEmitPost(json)
    }

    override suspend fun sendRecipeUnPause(): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(recipe = Recipe(stepData = StepData(pause = false)), updated = true)
        )
        return sendEmitPost(json)
    }

    override suspend fun setSmokePlus(enabled: Boolean): Result<Unit, DataError> {
        val json = json.encodeToString(ControlDto(smokePlus = enabled))
        return sendEmitPost(json)
    }

    override suspend fun setPWMControl(enabled: Boolean): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(pwmControl = enabled, settingsUpdate = true)
        )
        return sendEmitPost(json)
    }

    override suspend fun sendPrimeGrill(
        primeAmount: Int,
        nextMode: String
    ): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(
                currentMode = RunningMode.Prime.name,
                primeAmount = primeAmount,
                nextMode = nextMode,
                updated = true
            )
        )
        return sendEmitPost(json)
    }

    override suspend fun setGrillHoldTemp(temp: String): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(
                currentMode = RunningMode.Hold.name,
                primarySetPoint = temp.toInt(),
                updated = true
            )
        )
        return sendEmitPost(json)
    }

    override suspend fun setProbeNotify(notifyDto: NotifyDto): Result<Unit, DataError> {
        val json = json.encodeToString(PostDto(notifyDto = notifyDto))
        return socketManager.emitPost(
            ServerConstants.PA_NOTIFY_ACTION,
            ServerConstants.PT_NOTIFY_UPDATE,
            json
        )
    }

    override suspend fun sendToggleFan(enabled: Boolean): Result<Unit, DataError> {
        val output = if (enabled) ServerConstants.OUTPUT_FAN else null
        val json = json.encodeToString(
            ManualDto(
                manual = Manual(
                    change = ServerConstants.OUTPUT_FAN,
                    output = output
                )
            )
        )
        return sendEmitPost(json)
    }

    override suspend fun sendToggleAuger(enabled: Boolean): Result<Unit, DataError> {
        val output = if (enabled) ServerConstants.OUTPUT_AUGER else null
        val json = json.encodeToString(
            ManualDto(
                manual = Manual(
                    change = ServerConstants.OUTPUT_AUGER,
                    output = output
                )
            )
        )
        return sendEmitPost(json)
    }

    override suspend fun sendToggleIgniter(enabled: Boolean): Result<Unit, DataError> {
        val output = if (enabled) ServerConstants.OUTPUT_IGNITER else null
        val json = json.encodeToString(
            ManualDto(
                manual = Manual(
                    change = ServerConstants.OUTPUT_IGNITER,
                    output = output
                )
            )
        )
        return sendEmitPost(json)
    }

    override suspend fun checkHopperLevel(): Result<Unit, DataError> {
        return socketManager.emitPost(
            ServerConstants.PA_PELLETS_ACTION,
            ServerConstants.PT_HOPPER_CHECK
        )
    }

    override suspend fun sendToggleLidOpen(lidOpen: Boolean): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(
                lidOpenToggle = lidOpen,
                updated = true
            )
        )
        return sendEmitPost(json)
    }

    override suspend fun sendTimerAction(action: String): Result<Unit, DataError> {
        return socketManager.emitPost(ServerConstants.PA_TIMER_ACTION, action)
    }

    override suspend fun sendTimerTime(
        hours: Int,
        minutes: Int,
        shutdown: Boolean,
        keepWarm: Boolean
    ): Result<Unit, DataError> {
        val json = Json.encodeToString(
            PostDto(
                timerDto = TimerDto(
                    hoursRange = hours,
                    minutesRange = minutes,
                    timerShutdown = shutdown,
                    timerKeepWarm = keepWarm
                )
            )
        )
        return socketManager.emitPost(
            ServerConstants.PA_TIMER_ACTION,
            ServerConstants.PT_TIMER_START, json
        )
    }

    private suspend fun sendEmitPost(json: String): Result<Unit, DataError> {
        return socketManager.emitPost(
            ServerConstants.PA_UPDATE_ACTION,
            ServerConstants.PT_CONTROL,
            json
        )
    }
}