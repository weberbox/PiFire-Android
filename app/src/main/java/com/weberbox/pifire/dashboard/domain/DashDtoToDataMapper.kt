package com.weberbox.pifire.dashboard.domain

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.dashboard.data.model.DashDto
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Outputs
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Probe
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.RecipeStatus
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Timer
import com.weberbox.pifire.dashboard.presentation.model.ProbeType
import kotlin.math.roundToInt

object DashDtoToDataMapper : Mapper<DashDto, Dash> {
    private val defaults = Dash()
    private val timerDefaults = Timer()
    private val probeDefaults = Probe()
    private val outputDefaults = Outputs()
    private val recipeDefaults = RecipeStatus()

    override fun map(from: DashDto) = Dash(
        uuid = from.uuid.orEmpty(),
        errors = from.errors ?: defaults.errors,
        warnings = from.warnings ?: defaults.warnings,
        status = from.status ?: defaults.status,
        criticalError = from.criticalError ?: defaults.criticalError,
        grillName = from.grillName ?: defaults.grillName,
        currentMode = from.currentMode ?: defaults.currentMode,
        nextMode = from.nextMode ?: defaults.nextMode,
        displayMode = from.displayMode ?: defaults.displayMode,
        smokePlus = from.smokePlus ?: defaults.smokePlus,
        pwmControl = from.pwmControl ?: defaults.pwmControl,
        pMode = from.pMode ?: defaults.pMode,
        hopperLevel = from.hopperLevel ?: defaults.hopperLevel,
        startupTimestamp = from.startupTimestamp ?: defaults.startupTimestamp,
        modeStartTime = from.modeStartTime ?: defaults.modeStartTime,
        lidOpenDetectEnabled = from.lidOpenDetectEnabled ?: defaults.lidOpenDetectEnabled,
        lidOpenDetected = from.lidOpenDetected ?: defaults.lidOpenDetected,
        lidOpenEndTime = from.lidOpenEndTime ?: defaults.lidOpenEndTime,
        startDuration = from.startDuration ?: defaults.startDuration,
        shutdownDuration = from.shutdownDuration ?: defaults.shutdownDuration,
        primeDuration = from.primeDuration ?: defaults.primeDuration,
        primeAmount = from.primeAmount ?: defaults.primeAmount,
        tempUnits = from.tempUnits ?: defaults.tempUnits,
        hasDcFan = from.hasDcFan ?: defaults.hasDcFan,
        hasDistanceSensor = from.hasDistanceSensor ?: defaults.hasDistanceSensor,
        startupCheck = from.startupCheck ?: defaults.startupCheck,
        startToHoldPrompt = from.startToHoldPrompt ?: defaults.startToHoldPrompt,
        startupGotoTemp = from.startupGotoTemp ?: defaults.startupGotoTemp,
        startupGotoMode = from.startupGotoMode ?: defaults.startupGotoMode,
        allowManualOutputs = from.allowManualOutputs ?: defaults.allowManualOutputs,
        timer = Timer(
            start = from.timer?.start ?: timerDefaults.start,
            paused = from.timer?.paused ?: timerDefaults.paused,
            end = from.timer?.end ?: timerDefaults.end,
            keepWarm = from.timer?.keepWarm ?: timerDefaults.keepWarm,
            shutdown = from.timer?.shutdown ?: timerDefaults.shutdown,
            timerActive = (from.timer?.start ?: 0) > 0,
            timerPaused = (from.timer?.paused ?: 0) > 0
        ),
        outputs = Outputs(
            fan = from.outputs?.fan ?: outputDefaults.fan,
            auger = from.outputs?.auger ?: outputDefaults.auger,
            igniter = from.outputs?.igniter ?: outputDefaults.igniter
        ),
        recipeStatus = RecipeStatus(
            recipeMode = from.recipeStatus?.recipeMode ?: recipeDefaults.recipeMode,
            filename = from.recipeStatus?.filename ?: recipeDefaults.filename,
            mode = from.recipeStatus?.mode ?: recipeDefaults.mode,
            paused = from.recipeStatus?.paused ?: recipeDefaults.paused,
            step = from.recipeStatus?.step ?: recipeDefaults.step
        ),
        foodProbes = from.foodProbes?.map { mapProbe(from = it, probeType = ProbeType.Food) }
            ?: defaults.foodProbes,
        primaryProbe = mapProbe(from = from.primaryProbe, probeType = ProbeType.Primary),
    )

    private fun mapProbe(from: DashDto.Probe?, probeType: ProbeType) = Probe(
        probeType = probeType,
        title = from?.title ?: probeDefaults.title,
        label = from?.label ?: probeDefaults.label,
        eta = from?.eta ?: probeDefaults.eta,
        temp = from?.temp?.roundToIntOrNull() ?: probeDefaults.temp,
        setTemp = from?.setTemp ?: probeDefaults.setTemp,
        maxTemp = from?.maxTemp ?: probeDefaults.maxTemp,
        target = from?.target ?: probeDefaults.target,
        lowLimitTemp = from?.lowLimitTemp ?: probeDefaults.lowLimitTemp,
        highLimitTemp = from?.highLimitTemp ?: probeDefaults.highLimitTemp,
        lowLimitReq = from?.lowLimitReq ?: probeDefaults.lowLimitReq,
        highLimitReq = from?.highLimitReq ?: probeDefaults.highLimitReq,
        targetReq = from?.targetReq ?: probeDefaults.targetReq,
        highLimitShutdown = from?.highLimitShutdown ?: probeDefaults.highLimitShutdown,
        highLimitTriggered = from?.highLimitTriggered ?: probeDefaults.highLimitTriggered,
        lowLimitShutdown = from?.lowLimitShutdown ?: probeDefaults.lowLimitShutdown,
        lowLimitReignite = from?.lowLimitReignite ?: probeDefaults.lowLimitReignite,
        lowLimitTriggered = from?.lowLimitTriggered ?: probeDefaults.lowLimitTriggered,
        targetShutdown = from?.targetShutdown ?: probeDefaults.targetShutdown,
        targetKeepWarm = from?.targetKeepWarm ?: probeDefaults.targetKeepWarm,
        hasNotifications = from?.hasNotifications ?: probeDefaults.hasNotifications,
        status = Probe.Status(
            batteryCharging = from?.status?.batteryCharging
                ?: probeDefaults.status.batteryCharging,
            batteryPercentage = from?.status?.batteryPercentage?.roundToIntOrNull()
                ?: probeDefaults.status.batteryPercentage,
            batteryVoltage = from?.status?.batteryVoltage ?: probeDefaults.status.batteryVoltage,
            connected = from?.status?.connected ?: probeDefaults.status.connected,
            error = from?.status?.error ?: probeDefaults.status.error,
            wireless = from?.status?.connected != null,
        ),
    )

    // NaN (e.g. a disconnected probe) falls back to the default instead of throwing
    private fun Double.roundToIntOrNull(): Int? = takeIf { it.isFinite() }?.roundToInt()
}