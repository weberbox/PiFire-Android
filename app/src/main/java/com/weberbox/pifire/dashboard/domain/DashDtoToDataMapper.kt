package com.weberbox.pifire.dashboard.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.dashboard.data.model.DashDto
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Outputs
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Probe
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.RecipeStatus
import com.weberbox.pifire.dashboard.presentation.model.DashData.Dash.Timer
import com.weberbox.pifire.dashboard.presentation.model.ProbeType

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
        foodProbes = from.foodProbes?.map {
            Probe(
                probeType = ProbeType.Food,
                title = it.title ?: probeDefaults.title,
                label = it.label ?: probeDefaults.label,
                eta = it.eta ?: probeDefaults.eta,
                temp = it.temp ?: probeDefaults.temp,
                setTemp = it.setTemp ?: probeDefaults.setTemp,
                maxTemp = it.maxTemp ?: probeDefaults.maxTemp,
                target = it.target ?: probeDefaults.target,
                lowLimitTemp = it.lowLimitTemp ?: probeDefaults.lowLimitTemp,
                highLimitTemp = it.highLimitTemp ?: probeDefaults.highLimitTemp,
                lowLimitReq = it.lowLimitReq ?: probeDefaults.hasNotifications,
                highLimitReq = it.highLimitReq ?: probeDefaults.highLimitReq,
                targetReq = it.targetReq ?: probeDefaults.targetReq,
                highLimitShutdown = it.highLimitShutdown ?: probeDefaults.highLimitShutdown,
                highLimitTriggered = it.highLimitTriggered ?: probeDefaults.highLimitTriggered,
                lowLimitShutdown = it.highLimitShutdown ?: probeDefaults.lowLimitShutdown,
                lowLimitReignite = it.lowLimitReignite ?: probeDefaults.lowLimitReignite,
                lowLimitTriggered = it.lowLimitTriggered ?: probeDefaults.lowLimitTriggered,
                targetShutdown = it.targetShutdown ?: probeDefaults.targetShutdown,
                targetKeepWarm = it.targetKeepWarm ?: probeDefaults.targetKeepWarm,
                hasNotifications = it.hasNotifications ?: probeDefaults.hasNotifications,
                status = Probe.Status(
                    batteryCharging = it.status?.batteryCharging
                        ?: probeDefaults.status.batteryCharging,
                    batteryPercentage = it.status?.batteryPercentage?.toInt()
                        ?: probeDefaults.status.batteryPercentage,
                    batteryVoltage = it.status?.batteryVoltage
                        ?: probeDefaults.status.batteryVoltage,
                    connected = it.status?.connected ?: probeDefaults.status.connected,
                    error = it.status?.error ?: probeDefaults.status.error,
                    wireless = it.status?.connected != null
                )

            )
        } ?: defaults.foodProbes,
        primaryProbe = Probe(
            probeType = ProbeType.Primary,
            title = from.primaryProbe?.title ?: probeDefaults.title,
            label = from.primaryProbe?.label ?: probeDefaults.label,
            eta = from.primaryProbe?.eta ?: probeDefaults.eta,
            temp = from.primaryProbe?.temp ?: probeDefaults.temp,
            setTemp = from.primaryProbe?.setTemp ?: probeDefaults.setTemp,
            maxTemp = from.primaryProbe?.maxTemp ?: probeDefaults.maxTemp,
            target = from.primaryProbe?.target ?: probeDefaults.target,
            lowLimitTemp = from.primaryProbe?.lowLimitTemp ?: probeDefaults.lowLimitTemp,
            highLimitTemp = from.primaryProbe?.highLimitTemp ?: probeDefaults.highLimitTemp,
            lowLimitReq = from.primaryProbe?.lowLimitReq ?: probeDefaults.lowLimitReq,
            highLimitReq = from.primaryProbe?.highLimitReq ?: probeDefaults.highLimitReq,
            targetReq = from.primaryProbe?.targetReq ?: probeDefaults.targetReq,
            highLimitShutdown = from.primaryProbe?.highLimitShutdown
                ?: probeDefaults.highLimitShutdown,
            highLimitTriggered = from.primaryProbe?.highLimitTriggered
                ?: probeDefaults.highLimitTriggered,
            lowLimitShutdown = from.primaryProbe?.lowLimitShutdown
                ?: probeDefaults.lowLimitShutdown,
            lowLimitReignite = from.primaryProbe?.lowLimitReignite
                ?: probeDefaults.lowLimitReignite,
            lowLimitTriggered = from.primaryProbe?.lowLimitTriggered
                ?: probeDefaults.lowLimitTriggered,
            targetShutdown = from.primaryProbe?.targetShutdown ?: probeDefaults.targetShutdown,
            targetKeepWarm = from.primaryProbe?.targetKeepWarm ?: probeDefaults.targetKeepWarm,
            hasNotifications = from.primaryProbe?.hasNotifications
                ?: probeDefaults.hasNotifications,
            status = Probe.Status(
                batteryCharging = from.primaryProbe?.status?.batteryCharging
                    ?: probeDefaults.status.batteryCharging,
                batteryPercentage = from.primaryProbe?.status?.batteryPercentage?.toInt()
                    ?: probeDefaults.status.batteryPercentage,
                batteryVoltage = from.primaryProbe?.status?.batteryVoltage
                    ?: probeDefaults.status.batteryVoltage,
                connected = from.primaryProbe?.status?.connected ?: probeDefaults.status.connected,
                error = from.primaryProbe?.status?.error ?: probeDefaults.status.error,
                wireless = from.primaryProbe?.status?.connected != null
            )
        )
    )
}