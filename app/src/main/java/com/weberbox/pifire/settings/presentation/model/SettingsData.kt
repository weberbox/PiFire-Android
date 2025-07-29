package com.weberbox.pifire.settings.presentation.model

import com.weberbox.pifire.common.domain.Location
import com.weberbox.pifire.common.domain.ProbeId
import com.weberbox.pifire.common.domain.Temp
import com.weberbox.pifire.common.domain.Uuid
import com.weberbox.pifire.settings.data.model.local.Setting
import com.weberbox.pifire.settings.presentation.model.OneSignalPush.OneSignalDeviceInfo
import kotlinx.serialization.Serializable

@Suppress("unused")
@Serializable
data class SettingsData(
    val currentServerUuid: String = "",
    val serverMap: Map<Uuid, Server> = emptyMap(),
) {
    @Serializable
    data class Server(
        val uuid: String = "",
        val address: String = "",
        val name: String = "",
        val credentialsEnabled: Boolean = false,
        val headersEnabled: Boolean = false,
        val settings: Settings = Settings(),
    ) {
        @Serializable
        data class Settings(
            // Versions
            val serverVersion: String = Setting.serverVersion.value,
            val serverBuild: String = Setting.serverBuild.value,
            val recipesVersion: String = Setting.recipesVersion.value,
            val cookFileVersion: String = Setting.cookFileVersion.value,

            // Probes
            val probeProfiles: Map<ProbeId, ProbeProfile> = Setting.probeProfiles.value,
            val probeMap: ProbeMap = Setting.probeMap.value,

            // Globals
            val grillName: String = Setting.grillName.value,
            val adminDebug: Boolean = Setting.adminDebug.value,
            val primeIgnition: Boolean = Setting.primeIgnition.value,
            val bootToMonitor: Boolean = Setting.bootToMonitor.value,
            val tempUnits: String = Setting.tempUnits.value,
            val augerRate: Double = Setting.augerRate.value,
            val firstTimeSetup: Boolean = Setting.firstTimeSetup.value,
            val extData: Boolean = Setting.extData.value,
            val updatedMessage: Boolean = Setting.updatedMessage.value,
            val venv: Boolean = Setting.venv.value,

            // Platform
            val dcFan: Boolean = Setting.dcFan.value,
            val standalone: Boolean = Setting.standalone.value,
            val realHw: Boolean = Setting.realHw.value,
            val platformCurrent: String = Setting.platformCurrent.value,
            val platformType: String = Setting.platformType.value,

            // Controller
            val cntrlrSelected: String = Setting.cntrlrSelected.value,
            val cntrlrPidPb: Double = Setting.cntrlrPidPb.value,
            val cntrlrPidTd: Double = Setting.cntrlrPidTd.value,
            val cntrlrPidTi: Double = Setting.cntrlrPidTi.value,
            val cntrlrPidCenter: Double = Setting.cntrlrPidCenter.value,
            val cntrlrPidAcPb: Double = Setting.cntrlrPidAcPb.value,
            val cntrlrPidAcTd: Double = Setting.cntrlrPidAcTd.value,
            val cntrlrPidAcTi: Double = Setting.cntrlrPidAcTi.value,
            val cntrlrPidAcCenter: Double = Setting.cntrlrPidAcCenter.value,
            val cntrlrPidAcStable: Int = Setting.cntrlrPidAcStable.value,
            val cntrlrPidSpPb: Double = Setting.cntrlrPidSpPb.value,
            val cntrlrPidSpTd: Double = Setting.cntrlrPidSpTd.value,
            val cntrlrPidSpTi: Double = Setting.cntrlrPidSpTi.value,
            val cntrlrPidSpCenter: Double = Setting.cntrlrPidSpCenter.value,
            val cntrlrPidSpStable: Int = Setting.cntrlrPidSpStable.value,
            val cntrlrPidSpTau: Int = Setting.cntrlrPidSpTau.value,
            val cntrlrPidSpTheta: Int = Setting.cntrlrPidSpTheta.value,

            // Cycle Data
            val holdCycleTime: Int = Setting.holdCycleTime.value,
            val smokeOnCycleTime: Int = Setting.smokeOnCycleTime.value,
            val smokeOffCycleTime: Int = Setting.smokeOffCycleTime.value,
            val pMode: Int = Setting.pMode.value,
            val uMin: Double = Setting.uMin.value,
            val uMax: Double = Setting.uMax.value,
            val lidOpenDetectEnabled: Boolean = Setting.lidOpenDetectEnabled.value,
            val lidOpenThreshold: Int = Setting.lidOpenThreshold.value,
            val lidOpenPauseTime: Int = Setting.lidOpenPauseTime.value,

            // Dashboard
            val dashSelected: String = Setting.dashSelected.value,
            val dashMaxFoodTempF: Int = Setting.dashMaxFoodTempF.value,
            val dashMaxFoodTempC: Int = Setting.dashMaxFoodTempC.value,
            val dashMaxPrimaryTempF: Int = Setting.dashMaxPrimaryTempF.value,
            val dashMaxPrimaryTempC: Int = Setting.dashMaxPrimaryTempC.value,

            // Keep Warm
            val keepWarmTemp: Int = Setting.keepWarmTemp.value,
            val keepWarmSPlus: Boolean = Setting.keepWarmSPlus.value,

            // Smoke Plus
            val sPlusEnabled: Boolean = Setting.sPlusEnabled.value,
            val sPlusMinTemp: Int = Setting.sPlusMinTemp.value,
            val sPlusMaxTemp: Int = Setting.sPlusMaxTemp.value,
            val sPlusOnTime: Int = Setting.sPlusOnTime.value,
            val sPlusOffTime: Int = Setting.sPlusOffTime.value,
            val sPlusDutyCycle: Int = Setting.sPlusDutyCycle.value,
            val sPlusFanRamp: Boolean = Setting.sPlusFanRamp.value,

            // PWM
            val pwmControl: Boolean = Setting.pwmControl.value,
            val pwmUpdateTime: Int = Setting.pwmUpdateTime.value,
            val pwmFrequency: Int = Setting.pwmFrequency.value,
            val pwmMinDutyCycle: Int = Setting.pwmMinDutyCycle.value,
            val pwmMaxDutyCycle: Int = Setting.pwmMaxDutyCycle.value,
            val pwmTempRangeList: List<Temp> = Setting.pwmTempRangeList.value,
            val pwmProfiles: List<PwmProfile> = Setting.pwmProfiles.value,
            val pwmControlList: List<PwmControl> = Setting.pwmControlList.value,

            // Safety
            val safetyStartupCheck: Boolean = Setting.safetyStartupCheck.value,
            val safetyMinStartupTemp: Int = Setting.safetyMinStartupTemp.value,
            val safetyMaxStartupTemp: Int = Setting.safetyMaxStartupTemp.value,
            val safetyMaxTemp: Int = Setting.safetyMaxTemp.value,
            val safetyReigniteRetries: Int = Setting.safetyReigniteRetries.value,
            val safetyAllowManualChanges: Boolean = Setting.safetyAllowManualChanges.value,
            val safetyOverrideTime: Int = Setting.safetyOverrideTime.value,

            // Shutdown
            val shutdownDuration: Int = Setting.shutdownDuration.value,
            val shutdownAutoPowerOff: Boolean = Setting.shutdownAutoPowerOff.value,

            // Startup
            val startupDuration: Int = Setting.startupDuration.value,
            val startupPrime: Int = Setting.startupPrime.value,
            val startupExitTemp: Int = Setting.startupExitTemp.value,
            val startupGotoMode: String = Setting.startupGotoMode.value,
            val startupGotoTemp: Int = Setting.startupGotoTemp.value,
            val startToHoldPrompt: Boolean = Setting.startToHoldPrompt.value,
            val smartStartEnabled: Boolean = Setting.smartStartEnabled.value,
            val smartStartExitTemp: Int = Setting.smartStartExitTemp.value,
            val smartStartTempList: List<Temp> = Setting.smartStartTempList.value,
            val smartStartProfiles: List<SmartStartProfile> = Setting.smartStartProfiles.value,
            val smartStartList: List<SmartStart> = Setting.smartStartList.value,

            // Pellets
            val pelletsEmpty: Int = Setting.pelletsEmpty.value,
            val pelletsFull: Int = Setting.pelletsFull.value,
            val pelletsWarningEnabled: Boolean = Setting.pelletsWarningEnabled.value,
            val pelletsWarningLevel: Int = Setting.pelletsWarningLevel.value,
            val pelletsWarningTime: Int = Setting.pelletsWarningTime.value,

            // Modules
            val modulesDisplay: String = Setting.modulesDisplay.value,
            val modulesDistance: String = Setting.modulesDistance.value,
            val modulesPlatform: String = Setting.modulesPlatform.value,

            // Ifttt
            val iftttEnabled: Boolean = Setting.iftttEnabled.value,
            val iftttApiKey: String = Setting.iftttApiKey.value,

            // Pushbullet
            val pushbulletEnabled: Boolean = Setting.pushbulletEnabled.value,
            val pushbulletApiKey: String = Setting.pushbulletApiKey.value,
            val pushbulletUrl: String = Setting.pushbulletUrl.value,

            // Pushover
            val pushoverEnabled: Boolean = Setting.pushoverEnabled.value,
            val pushoverApiKey: String = Setting.pushoverApiKey.value,
            val pushoverUserKeys: String = Setting.pushoverUserKeys.value,
            val pushoverUrl: String = Setting.pushoverUrl.value,

            // Onesignal
            val onesignalEnabled: Boolean = Setting.onesignalEnabled.value,
            val onesignalUuid: String = Setting.onesignalUuid.value,
            val onesignalDevices: Map<String, OneSignalDeviceInfo> = Setting.onesignalDevices.value,

            // InfluxDb
            val influxDbEnabled: Boolean = Setting.influxdbEnabled.value,
            val influxDbUrl: String = Setting.influxdbUrl.value,
            val influxDbToken: String = Setting.influxdbToken.value,
            val influxDbOrg: String = Setting.influxdbOrg.value,
            val influxDbBucket: String = Setting.influxdbBucket.value,

            // Mqtt
            val mqttEnabled: Boolean = Setting.mqttEnabled.value,
            val mqttBroker: String = Setting.mqttBroker.value,
            val mqttTopic: String = Setting.mqttTopic.value,
            val mqttId: String = Setting.mqttId.value,
            val mqttPassword: String = Setting.mqttPass.value,
            val mqttUsername: String =  Setting.mqttUser.value,
            val mqttPort: Int = Setting.mqttPort.value,
            val mqttUpdateSec: Int = Setting.mqttUpdateSec.value,

            // Apprise
            val appriseEnabled: Boolean = Setting.appriseEnabled.value,
            val appriseLocations: List<Location> = Setting.appriseLocations.value
        )
    }
}

