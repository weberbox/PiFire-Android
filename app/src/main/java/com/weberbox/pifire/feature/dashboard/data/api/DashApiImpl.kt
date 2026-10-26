package com.weberbox.pifire.feature.dashboard.data.api

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.model.dashboard.RunningMode
import com.weberbox.pifire.core.network.model.PostDto
import com.weberbox.pifire.core.network.model.PostDto.ControlDto
import com.weberbox.pifire.core.network.model.PostDto.ControlDto.Recipe
import com.weberbox.pifire.core.network.model.PostDto.ControlDto.StepData
import com.weberbox.pifire.core.network.model.PostDto.NotifyDto
import com.weberbox.pifire.core.network.model.PostDto.TimerDto
import com.weberbox.pifire.core.network.model.settings.ManualDto
import com.weberbox.pifire.core.network.model.settings.ManualDto.Manual
import com.weberbox.pifire.core.network.transport.PiFireTransport
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

class DashApiImpl @Inject constructor(
    private val transport: PiFireTransport,
    private val json: Json,
) : DashApi {

    override suspend fun getDashData(): Result<String, DataError> = transport.get(ServerConstants.GA_DASH_DATA)

    override suspend fun listenDashData(): Flow<Result<String, DataError>> {
        val flow = transport.listen(ServerConstants.LISTEN_DASH_DATA)
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
            ControlDto(currentMode = RunningMode.Smoke.name, updated = true),
        )
        return sendEmitPost(json)
    }

    override suspend fun sendStopMode(): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(currentMode = RunningMode.Stop.name, updated = true),
        )
        return sendEmitPost(json)
    }

    override suspend fun sendStartGrill(): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(currentMode = RunningMode.Startup.name, updated = true),
        )
        return sendEmitPost(json)
    }

    override suspend fun sendMonitorMode(): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(currentMode = RunningMode.Monitor.name, updated = true),
        )
        return sendEmitPost(json)
    }

    override suspend fun sendShutdownMode(): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(currentMode = RunningMode.Shutdown.name, updated = true),
        )
        return sendEmitPost(json)
    }

    override suspend fun sendRecipeUnPause(): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(recipe = Recipe(stepData = StepData(pause = false)), updated = true),
        )
        return sendEmitPost(json)
    }

    override suspend fun setSmokePlus(enabled: Boolean): Result<Unit, DataError> {
        val json = json.encodeToString(ControlDto(smokePlus = enabled))
        return sendEmitPost(json)
    }

    override suspend fun setPWMControl(enabled: Boolean): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(pwmControl = enabled, settingsUpdate = true),
        )
        return sendEmitPost(json)
    }

    override suspend fun sendPrimeGrill(
        primeAmount: Int,
        nextMode: String,
    ): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(
                currentMode = RunningMode.Prime.name,
                primeAmount = primeAmount,
                nextMode = nextMode,
                updated = true,
            ),
        )
        return sendEmitPost(json)
    }

    override suspend fun setGrillHoldTemp(temp: String): Result<Unit, DataError> {
        val setPoint = temp.toDoubleOrNull()?.takeIf { it.isFinite() }?.roundToInt()
        if (setPoint == null) {
            Timber.w("Invalid hold temp: $temp")
            return Result.Error(DataError.Local.UNKNOWN)
        }
        val json = json.encodeToString(
            ControlDto(
                currentMode = RunningMode.Hold.name,
                primarySetPoint = setPoint,
                updated = true,
            ),
        )
        return sendEmitPost(json)
    }

    override suspend fun setProbeNotify(notifyDto: NotifyDto): Result<Unit, DataError> {
        val json = json.encodeToString(PostDto(notifyDto = notifyDto))
        return transport.post(
            ServerConstants.PA_NOTIFY_ACTION,
            ServerConstants.PT_NOTIFY_UPDATE,
            json,
        )
    }

    override suspend fun sendToggleFan(enabled: Boolean): Result<Unit, DataError> {
        val output = if (enabled) ServerConstants.OUTPUT_FAN else null
        val json = json.encodeToString(
            ManualDto(
                manual = Manual(
                    change = ServerConstants.OUTPUT_FAN,
                    output = output,
                ),
            ),
        )
        return sendEmitPost(json)
    }

    override suspend fun sendToggleAuger(enabled: Boolean): Result<Unit, DataError> {
        val output = if (enabled) ServerConstants.OUTPUT_AUGER else null
        val json = json.encodeToString(
            ManualDto(
                manual = Manual(
                    change = ServerConstants.OUTPUT_AUGER,
                    output = output,
                ),
            ),
        )
        return sendEmitPost(json)
    }

    override suspend fun sendToggleIgniter(enabled: Boolean): Result<Unit, DataError> {
        val output = if (enabled) ServerConstants.OUTPUT_IGNITER else null
        val json = json.encodeToString(
            ManualDto(
                manual = Manual(
                    change = ServerConstants.OUTPUT_IGNITER,
                    output = output,
                ),
            ),
        )
        return sendEmitPost(json)
    }

    override suspend fun checkHopperLevel(): Result<Unit, DataError> = transport.post(
        ServerConstants.PA_PELLETS_ACTION,
        ServerConstants.PT_HOPPER_CHECK,
    )

    override suspend fun sendToggleLidOpen(lidOpen: Boolean): Result<Unit, DataError> {
        val json = json.encodeToString(
            ControlDto(
                lidOpenToggle = lidOpen,
                updated = true,
            ),
        )
        return sendEmitPost(json)
    }

    override suspend fun sendTimerAction(action: String): Result<Unit, DataError> = transport.post(ServerConstants.PA_TIMER_ACTION, action)

    override suspend fun sendTimerTime(
        hours: Int,
        minutes: Int,
        shutdown: Boolean,
        keepWarm: Boolean,
    ): Result<Unit, DataError> {
        val payload = json.encodeToString(
            PostDto(
                timerDto = TimerDto(
                    hoursRange = hours,
                    minutesRange = minutes,
                    timerShutdown = shutdown,
                    timerKeepWarm = keepWarm,
                ),
            ),
        )
        return transport.post(
            ServerConstants.PA_TIMER_ACTION,
            ServerConstants.PT_TIMER_START,
            payload,
        )
    }

    private suspend fun sendEmitPost(json: String): Result<Unit, DataError> = transport.post(
        ServerConstants.PA_UPDATE_ACTION,
        ServerConstants.PT_CONTROL,
        json,
    )
}
