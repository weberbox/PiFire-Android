package com.weberbox.pifire.settings.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.settings.data.model.local.Setting
import com.weberbox.pifire.settings.data.model.remote.SettingsDto
import com.weberbox.pifire.settings.presentation.model.PwmProfile
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server.Settings

object SettingsDtoToDataMapper : Mapper<SettingsDto, Server> {

    override fun map(from: SettingsDto): Server {
        val pid = from.controller?.config?.pid
        val pidAc = from.controller?.config?.pidAc
        val pidSp = from.controller?.config?.pidSp
        val pellets = from.pelletLevel
        val notify = from.notifyServices
        val dashboard = from.dashboard
        val dashboards = from.dashboard?.dashboards
        val dashConfig = dashboards?.default?.config

        return Server(
            uuid = from.serverInfo?.uuid.orEmpty(),
            name = from.globals?.grillName ?: Setting.grillName.value,
            settings = Settings(
                serverVersion = from.versions?.server ?: Setting.serverVersion.value,
                serverBuild = from.versions?.build ?: Setting.serverBuild.value,
                recipesVersion = from.versions?.recipe ?: Setting.recipesVersion.value,
                cookFileVersion = from.versions?.cookFile ?: Setting.cookFileVersion.value,
                probeProfiles = ProbeProfilesMapper.map(from.probeSettings?.probeProfiles),
                probeMap = ProbeMapMapper.map(
                    from.probeSettings?.probeMap?.probeDevices,
                    from.probeSettings?.probeMap?.probeInfo
                ),
                grillName = from.globals?.grillName ?: Setting.grillName.value,
                adminDebug = from.globals?.debugMode ?: Setting.adminDebug.value,
                primeIgnition = from.globals?.primeIgnition ?: Setting.primeIgnition.value,
                bootToMonitor = from.globals?.bootToMonitor ?: Setting.bootToMonitor.value,
                tempUnits = from.globals?.units ?: Setting.tempUnits.value,
                augerRate = from.globals?.augerRate ?: Setting.augerRate.value,
                firstTimeSetup = from.globals?.firstTimeSetup ?: Setting.firstTimeSetup.value,
                extData = from.globals?.extData ?: Setting.extData.value,
                updatedMessage = from.globals?.updatedMessage ?: Setting.updatedMessage.value,
                venv = from.globals?.venv ?: Setting.venv.value,
                dcFan = from.platform?.dcFan ?: Setting.dcFan.value,
                standalone = from.platform?.standalone ?: Setting.standalone.value,
                realHw = from.platform?.realHw ?: Setting.realHw.value,
                platformCurrent = from.platform?.current ?: Setting.platformCurrent.value,
                platformType = from.platform?.systemType ?: Setting.platformType.value,
                cntrlrSelected = from.controller?.selected ?: Setting.cntrlrSelected.value,
                cntrlrPidPb = pid?.pb ?: Setting.cntrlrPidPb.value,
                cntrlrPidTd = pid?.td ?: Setting.cntrlrPidTd.value,
                cntrlrPidTi = pid?.ti ?: Setting.cntrlrPidTi.value,
                cntrlrPidCenter = pid?.center ?: Setting.cntrlrPidCenter.value,
                cntrlrPidAcPb = pidAc?.pb ?: Setting.cntrlrPidAcPb.value,
                cntrlrPidAcTd = pidAc?.td ?: Setting.cntrlrPidAcTd.value,
                cntrlrPidAcTi = pidAc?.ti ?: Setting.cntrlrPidAcTi.value,
                cntrlrPidAcCenter = pidAc?.centerFactor ?: Setting.cntrlrPidAcCenter.value,
                cntrlrPidAcStable = pidAc?.stableWindow ?: Setting.cntrlrPidAcStable.value,
                cntrlrPidSpPb = pidSp?.pb ?: Setting.cntrlrPidSpPb.value,
                cntrlrPidSpTd = pidSp?.td ?: Setting.cntrlrPidSpTd.value,
                cntrlrPidSpTi = pidSp?.ti ?: Setting.cntrlrPidSpTi.value,
                cntrlrPidSpCenter = pidSp?.centerFactor ?: Setting.cntrlrPidSpCenter.value,
                cntrlrPidSpStable = pidSp?.stableWindow ?: Setting.cntrlrPidSpStable.value,
                cntrlrPidSpTau = pidSp?.tau ?: Setting.cntrlrPidSpTau.value,
                cntrlrPidSpTheta = pidSp?.theta ?: Setting.cntrlrPidSpTheta.value,
                holdCycleTime = from.cycleData?.holdCycleTime ?: Setting.holdCycleTime.value,
                smokeOnCycleTime = from.cycleData?.smokeOnCycleTime
                    ?: Setting.smokeOnCycleTime.value,
                smokeOffCycleTime = from.cycleData?.smokeOffCycleTime
                    ?: Setting.smokeOffCycleTime.value,
                pMode = from.cycleData?.pMode ?: Setting.pMode.value,
                uMin = from.cycleData?.uMin ?: Setting.uMin.value,
                uMax = from.cycleData?.uMax ?: Setting.uMax.value,
                lidOpenDetectEnabled = from.cycleData?.lidOpenDetectEnabled
                    ?: Setting.lidOpenDetectEnabled.value,
                lidOpenThreshold = from.cycleData?.lidOpenThreshold
                    ?: Setting.lidOpenThreshold.value,
                lidOpenPauseTime = from.cycleData?.lidOpenPauseTime
                    ?: Setting.lidOpenPauseTime.value,
                dashSelected = dashboard?.current ?: Setting.dashSelected.value,
                dashMaxFoodTempF = dashConfig?.maxFoodTempF ?: Setting.dashMaxFoodTempF.value,
                dashMaxFoodTempC = dashConfig?.maxFoodTempC ?: Setting.dashMaxFoodTempC.value,
                dashMaxPrimaryTempF = dashConfig?.maxPrimaryTempF
                    ?: Setting.dashMaxPrimaryTempF.value,
                dashMaxPrimaryTempC = dashConfig?.maxPrimaryTempC
                    ?: Setting.dashMaxPrimaryTempC.value,
                keepWarmTemp = from.keepWarm?.temp ?: Setting.keepWarmTemp.value,
                keepWarmSPlus = from.keepWarm?.enabled ?: Setting.keepWarmSPlus.value,
                sPlusEnabled = from.smokePlus?.enabled ?: Setting.sPlusEnabled.value,
                sPlusMinTemp = from.smokePlus?.minTemp ?: Setting.sPlusMinTemp.value,
                sPlusMaxTemp = from.smokePlus?.maxTemp ?: Setting.sPlusMaxTemp.value,
                sPlusOnTime = from.smokePlus?.onTime ?: Setting.sPlusOnTime.value,
                sPlusOffTime = from.smokePlus?.offTime ?: Setting.sPlusOffTime.value,
                sPlusDutyCycle = from.smokePlus?.dutyCycle ?: Setting.sPlusDutyCycle.value,
                sPlusFanRamp = from.smokePlus?.fanRamp ?: Setting.sPlusFanRamp.value,
                pwmControl = from.pwm?.pwmControl ?: Setting.pwmControl.value,
                pwmUpdateTime = from.pwm?.updateTime ?: Setting.pwmUpdateTime.value,
                pwmFrequency = from.pwm?.frequency ?: Setting.pwmFrequency.value,
                pwmMinDutyCycle = from.pwm?.minDutyCycle ?: Setting.pwmMinDutyCycle.value,
                pwmMaxDutyCycle = from.pwm?.maxDutyCycle ?: Setting.pwmMaxDutyCycle.value,
                pwmTempRangeList = from.pwm?.tempRangeList ?: Setting.pwmTempRangeList.value,
                pwmProfiles = from.pwm?.profiles?.map { profile -> PwmProfile(
                    dutyCycle = profile.dutyCycle ?: 0
                ) } ?: emptyList(),
                pwmControlList = PwmControlMapper.map(from.pwm?.tempRangeList, from.pwm?.profiles),
                safetyStartupCheck = from.safety?.startupCheck ?: Setting.safetyStartupCheck.value,
                safetyMinStartupTemp = from.safety?.minStartupTemp
                    ?: Setting.safetyMinStartupTemp.value,
                safetyMaxStartupTemp = from.safety?.maxStartupTemp
                    ?: Setting.safetyMaxStartupTemp.value,
                safetyMaxTemp = from.safety?.maxTemp ?: Setting.safetyMaxTemp.value,
                safetyReigniteRetries = from.safety?.reigniteRetries
                    ?: Setting.safetyReigniteRetries.value,
                safetyAllowManualChanges = from.safety?.allowManualChanges
                    ?: Setting.safetyAllowManualChanges.value,
                safetyOverrideTime = from.safety?.manualOverrideTime
                    ?: Setting.safetyOverrideTime.value,
                shutdownDuration = from.shutdown?.duration ?: Setting.shutdownDuration.value,
                shutdownAutoPowerOff = from.shutdown?.autoPowerOff
                    ?: Setting.shutdownAutoPowerOff.value,
                startupDuration = from.startup?.duration ?: Setting.startupDuration.value,
                startupPrime = from.startup?.primeOnStartup ?: Setting.startupPrime.value,
                startupExitTemp = from.startup?.startExitTemp ?: Setting.startupExitTemp.value,
                startupGotoMode = from.startup?.startToMode?.afterStartUpMode
                    ?: Setting.startupGotoMode.value,
                startupGotoTemp = from.startup?.startToMode?.primarySetPoint
                    ?: Setting.startupGotoTemp.value,
                smartStartEnabled = from.startup?.smartStart?.enabled
                    ?: Setting.smartStartEnabled.value,
                smartStartExitTemp = from.startup?.smartStart?.exitTemp
                    ?: Setting.smartStartExitTemp.value,
                smartStartTempList = from.startup?.smartStart?.tempRangeList
                    ?: Setting.smartStartTempList.value,
                smartStartProfiles = Setting.smartStartProfiles.value,
                smartStartList = SmartStartMapper.map(
                    from.startup?.smartStart?.tempRangeList,
                    from.startup?.smartStart?.profiles
                ),
                pelletsEmpty = pellets?.empty ?: Setting.pelletsEmpty.value,
                pelletsFull = pellets?.full ?: Setting.pelletsFull.value,
                pelletsWarningEnabled = pellets?.warningEnabled
                    ?: Setting.pelletsWarningEnabled.value,
                pelletsWarningLevel = pellets?.warningLevel ?: Setting.pelletsWarningLevel.value,
                pelletsWarningTime = pellets?.warningTime ?: Setting.pelletsWarningTime.value,
                modulesDisplay = from.modules?.display ?: Setting.modulesDistance.value,
                modulesDistance = from.modules?.dist ?: Setting.modulesDistance.value,
                modulesPlatform = from.modules?.grillplat ?: Setting.modulesPlatform.value,
                iftttEnabled = notify?.ifttt?.enabled ?: Setting.iftttEnabled.value,
                iftttApiKey = notify?.ifttt?.apiKey ?: Setting.iftttApiKey.value,
                pushbulletEnabled = notify?.pushbullet?.enabled ?: Setting.pushbulletEnabled.value,
                pushbulletApiKey = notify?.pushbullet?.apiKey ?: Setting.pushbulletApiKey.value,
                pushbulletUrl = notify?.pushbullet?.publicURL ?: Setting.pushbulletUrl.value,
                pushoverEnabled = notify?.pushover?.enabled ?: Setting.pushoverEnabled.value,
                pushoverApiKey = notify?.pushover?.apiKey ?: Setting.pushoverApiKey.value,
                pushoverUserKeys = notify?.pushover?.userKeys ?: Setting.pushoverApiKey.value,
                pushoverUrl = notify?.pushover?.publicURL ?: Setting.pushoverUrl.value,
                onesignalEnabled = notify?.onesignal?.enabled ?: Setting.onesignalEnabled.value,
                onesignalUuid = notify?.onesignal?.uuid ?: Setting.onesignalUuid.value,
                onesignalDevices = OneSignalDeviceMapper.map(
                    from.notifyServices?.onesignal?.devices
                ),
                influxDbEnabled = notify?.influxDB?.enabled ?: Setting.influxdbEnabled.value,
                influxDbUrl = notify?.influxDB?.url ?: Setting.influxdbUrl.value,
                influxDbToken = notify?.influxDB?.token ?: Setting.influxdbToken.value,
                influxDbOrg = notify?.influxDB?.org ?: Setting.influxdbOrg.value,
                influxDbBucket = notify?.influxDB?.bucket ?: Setting.influxdbBucket.value,
                mqttEnabled = notify?.mqtt?.enabled ?: Setting.mqttEnabled.value,
                mqttBroker = notify?.mqtt?.broker ?: Setting.mqttBroker.value,
                mqttTopic = notify?.mqtt?.topic ?: Setting.mqttTopic.value,
                mqttId = notify?.mqtt?.id ?: Setting.mqttId.value,
                mqttUsername = notify?.mqtt?.username ?: Setting.mqttUser.value,
                mqttPassword = notify?.mqtt?.password ?: Setting.mqttPass.value,
                mqttPort = notify?.mqtt?.port ?: Setting.mqttPort.value,
                mqttUpdateSec = notify?.mqtt?.updateSec ?: Setting.mqttUpdateSec.value,
                appriseEnabled = notify?.apprise?.enabled ?: Setting.appriseEnabled.value,
                appriseLocations = notify?.apprise?.locations ?: Setting.appriseLocations.value
            )
        )
    }
}