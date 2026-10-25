package com.weberbox.pifire.core.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.common.model.map
import com.weberbox.pifire.core.model.settings.OneSignalPush.OneSignalDeviceInfo
import com.weberbox.pifire.core.model.settings.ProbeInfoUpdate
import com.weberbox.pifire.core.model.settings.PwmProfile
import com.weberbox.pifire.core.model.settings.SettingsData
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.model.settings.SmartStartProfile
import com.weberbox.pifire.core.network.api.SettingsApi
import com.weberbox.pifire.core.network.model.PostDto.ProbesDto
import com.weberbox.pifire.core.network.model.settings.Pwm
import com.weberbox.pifire.core.network.model.settings.Startup
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.core.network.parseResponse
import com.weberbox.pifire.core.repo.ServerDataSync
import com.weberbox.pifire.core.repo.mapper.SettingsDtoToDataMapper
import com.weberbox.pifire.core.session.SessionStateHolder
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SettingsRepoImpl @Inject constructor(
    private val settingsApi: SettingsApi,
    private val sessionStateHolder: SessionStateHolder,
    private val serverDataManager: Lazy<ServerDataManager>,
    private val dataStore: DataStore<SettingsData>,
    private val json: Json,
) : SettingsRepo {

    private val sync = ServerDataSync(
        currentServerUuid = { sessionStateHolder.currentServerUuid },
        publish = sessionStateHolder::setSettingsData,
        readCache = { serverUuid -> dataStore.data.firstOrNull()?.serverMap?.get(serverUuid) },
        writeCache = ::updateServerSettings,
    )

    override suspend fun getSettings(): Result<Server, DataError> = sync.fetch(
        request = settingsApi::getSettings,
        parse = { parseGetResponse(it, json, SettingsDtoToDataMapper) },
    )

    override suspend fun listenSettingsData(): Flow<Result<Server, DataError>> = sync.listen(
        updates = settingsApi.listenSettingsData(),
        parse = { parseResponse(it, json, SettingsDtoToDataMapper) },
    )

    override suspend fun getCachedData(): Result<Server, DataError.Local> =
        sync.cached(publishToSession = false)

    override suspend fun updateServerSettings(server: Server) {
        dataStore.updateData { data ->
            if (!data.serverMap.keys.contains(server.uuid) && server.uuid.isNotBlank()) {
                data.copy(
                    serverMap = data.serverMap.plus(server.uuid to server),
                )
            } else {
                data.copy(
                    serverMap = data.serverMap.mapValues { (uuid, item) ->
                        if (server.uuid == uuid) {
                            item.copy(
                                name = server.settings.grillName,
                                settings = server.settings,
                            )
                        } else {
                            item
                        }
                    },
                )
            }
        }
    }

    override suspend fun getSettingsFlow(): Flow<SettingsData> = dataStore.data

    override suspend fun getServerMap(): Map<String, Server>? = dataStore.data.firstOrNull()?.serverMap

    override suspend fun getCurrentServer(): Server? {
        val server = dataStore.data.firstOrNull()?.currentServerUuid
        return getServerMap()?.get(server)
    }

    override suspend fun getCurrentServerItem(uuid: String): Server? = getServerMap()?.get(uuid)

    override suspend fun toggleHeadersEnabled(enabled: Boolean, uuid: String) {
        getCurrentServerItem(uuid)?.let {
            val server = it.copy(headersEnabled = enabled)
            updateServerList(server, uuid)
        }
    }

    override suspend fun toggleCredentialsEnabled(enabled: Boolean, uuid: String) {
        getCurrentServerItem(uuid)?.let {
            val server = it.copy(credentialsEnabled = enabled)
            updateServerList(server, uuid)
        }
    }

    override suspend fun updateServerList(server: Server, uuid: String) {
        dataStore.updateData { data ->
            data.copy(
                serverMap = data.serverMap.plus(uuid to server),
            )
        }
    }

    override suspend fun updateServerAddress(address: String, uuid: String) {
        getCurrentServerItem(uuid)?.let {
            val server = it.copy(address = address)
            updateServerList(server, uuid)
        }
    }

    override suspend fun selectServer(uuid: String): Server? = dataStore.updateData { data ->
        data.copy(
            currentServerUuid = uuid,
        )
    }.serverMap[uuid]

    override suspend fun deleteServer(uuid: String) {
        serverDataManager.get().deleteServer(uuid)
    }

    // Follows currentServerUuid, so switching servers switches the flow
    override fun getCurrentServerFlow(): Flow<Server> = dataStore.data
        .mapNotNull { it.serverMap[it.currentServerUuid] }
        .distinctUntilChanged()

    override suspend fun deleteHistory(): Result<Unit, DataError> = settingsApi.deleteHistory()

    override suspend fun deleteEvents(): Result<Unit, DataError> = settingsApi.deleteEvents()

    override suspend fun deletePelletLogs(): Result<Unit, DataError> = settingsApi.deletePelletLogs()

    override suspend fun deletePellets(): Result<Unit, DataError> = settingsApi.deletePellets()

    override suspend fun factoryReset(): Result<Unit, DataError> = settingsApi.factoryReset()

    override suspend fun restartControl(): Result<Unit, DataError> = settingsApi.restartControl()

    override suspend fun restartWebApp(): Result<Unit, DataError> = settingsApi.restartWebApp()

    override suspend fun restartSupervisor(): Result<Unit, DataError> = settingsApi.restartSupervisor()

    override suspend fun rebootSystem(): Result<Unit, DataError> = settingsApi.rebootSystem()

    override suspend fun shutdownSystem(): Result<Unit, DataError> = settingsApi.shutdownSystem()

    override suspend fun setDebugMode(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setDebugMode(enabled))

    override suspend fun setBootToMonitor(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setBootToMonitor(enabled))

    override suspend fun setGrillName(name: String): Result<Server, DataError> = transformResult(settingsApi.setGrillName(name))

    override suspend fun setETACalculations(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setETACalculations(enabled))

    override suspend fun getManualData(): Result<Server, DataError> = transformResult(settingsApi.getManualData())

    override suspend fun setManualMode(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setManualMode(enabled))

    override suspend fun setManualFanOutput(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setManualFanOutput(enabled))

    override suspend fun setManualAugerOutput(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setManualAugerOutput(enabled))

    override suspend fun setManualIgniterOutput(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setManualIgniterOutput(enabled))

    override suspend fun setManualPowerOutput(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setManualPowerOutput(enabled))

    override suspend fun setManualPWMOutput(pwm: Int): Result<Server, DataError> = transformResult(settingsApi.setManualPWMOutput(pwm))

    override suspend fun setIFTTTEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setIFTTTEnabled(enabled))

    override suspend fun setIFTTTAPIKey(apiKey: String): Result<Server, DataError> = transformResult(settingsApi.setIFTTTAPIKey(apiKey))

    override suspend fun setPushOverEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setPushOverEnabled(enabled))

    override suspend fun setPushOverAPIKey(apiKey: String): Result<Server, DataError> = transformResult(settingsApi.setPushOverAPIKey(apiKey))

    override suspend fun setPushOverUserKeys(userKeys: String): Result<Server, DataError> = transformResult(settingsApi.setPushOverUserKeys(userKeys))

    override suspend fun setPushOverUrl(url: String): Result<Server, DataError> = transformResult(settingsApi.setPushOverUrl(url))

    override suspend fun setPushBulletEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setPushBulletEnabled(enabled))

    override suspend fun setPushBulletAPIKey(apiKey: String): Result<Server, DataError> = transformResult(settingsApi.setPushBulletAPIKey(apiKey))

    override suspend fun setPushBulletURL(url: String): Result<Server, DataError> = transformResult(settingsApi.setPushBulletURL(url))

    override suspend fun setInfluxDBEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setInfluxDBEnabled(enabled))

    override suspend fun setInfluxDBUrl(url: String): Result<Server, DataError> = transformResult(settingsApi.setInfluxDBUrl(url))

    override suspend fun setInfluxDBToken(token: String): Result<Server, DataError> = transformResult(settingsApi.setInfluxDBToken(token))

    override suspend fun setInfluxDBOrg(org: String): Result<Server, DataError> = transformResult(settingsApi.setInfluxDBOrg(org))

    override suspend fun setInfluxDBBucket(bucket: String): Result<Server, DataError> = transformResult(settingsApi.setInfluxDBBucket(bucket))

    override suspend fun setMqttEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setMqttEnabled(enabled))

    override suspend fun setMqttBroker(broker: String): Result<Server, DataError> = transformResult(settingsApi.setMqttBroker(broker))

    override suspend fun setMqttTopic(topic: String): Result<Server, DataError> = transformResult(settingsApi.setMqttTopic(topic))

    override suspend fun setMqttId(id: String): Result<Server, DataError> = transformResult(settingsApi.setMqttId(id))

    override suspend fun setMqttPassword(password: String): Result<Server, DataError> = transformResult(settingsApi.setMqttPassword(password))

    override suspend fun setMqttPort(port: Int): Result<Server, DataError> = transformResult(settingsApi.setMqttPort(port))

    override suspend fun setMqttUpdateSec(updateSec: Int): Result<Server, DataError> = transformResult(settingsApi.setMqttUpdateSec(updateSec))

    override suspend fun setMqttUsername(username: String): Result<Server, DataError> = transformResult(settingsApi.setMqttUsername(username))

    override suspend fun setAppriseEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setAppriseEnabled(enabled))

    override suspend fun setAppriseLocations(locations: List<String>): Result<Server, DataError> = transformResult(settingsApi.setAppriseLocations(locations))

    override suspend fun setWLEDEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setWLEDEnabled(enabled))

    override suspend fun setWLEDAddress(address: String): Result<Server, DataError> = transformResult(settingsApi.setWLEDAddress(address))

    override suspend fun setWLEDDuration(duration: Int): Result<Server, DataError> = transformResult(settingsApi.setWLEDDuration(duration))

    override suspend fun setWLedModeHold(mode: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedModeHold(mode))

    override suspend fun setWLedModePrime(mode: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedModePrime(mode))

    override suspend fun setWLedModeReignite(mode: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedModeReignite(mode))

    override suspend fun setWLedModeShutdown(mode: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedModeShutdown(mode))

    override suspend fun setWLedModeSmoke(mode: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedModeSmoke(mode))

    override suspend fun setWLedModeStartup(mode: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedModeStartup(mode))

    override suspend fun setWLedModeStop(mode: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedModeStop(mode))

    override suspend fun setWLedEventGrill(event: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedEventGrill(event))

    override suspend fun setWLedEventPellets(event: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedEventPellets(event))

    override suspend fun setWLedEventRecipe(event: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedEventRecipe(event))

    override suspend fun setWLedEventTemp(event: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedEventTemp(event))

    override suspend fun setWLedEventTimer(event: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedEventTimer(event))

    override suspend fun setWLedUseProfiles(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setWLedUseProfiles(enabled))

    override suspend fun setWLedUseSuggestedProfiles(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setWLedUseSuggestedProfiles(enabled))

    override suspend fun setWLedNightMode(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setWLedNightMode(enabled))

    override suspend fun setWLedCookingColor(color: String): Result<Server, DataError> = transformResult(settingsApi.setWLedCookingColor(color))

    override suspend fun setWLedIdleBrightness(brightness: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedIdleBrightness(brightness))

    override suspend fun setWLedLedCount(count: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedLedCount(count))

    override suspend fun setWLedProfileColor(color: String): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileColor(color))

    override suspend fun setWLedProfileBrightness(brightness: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileBrightness(brightness))

    override suspend fun setWLedProfileCount(count: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileCount(count))

    override suspend fun setWLedProfileNight(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileNight(enabled))

    override suspend fun setWLedProfileBooting(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileBooting(profile))

    override suspend fun setWLedProfileCooking(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileCooking(profile))

    override suspend fun setWLedProfileCooldown(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileCooldown(profile))

    override suspend fun setWLedProfileFault(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileFault(profile))

    override suspend fun setWLedProfileIdle(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileIdle(profile))

    override suspend fun setWLedProfilePellets(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfilePellets(profile))

    override suspend fun setWLedProfileNight(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileNight(profile))

    override suspend fun setWLedProfileOvershoot(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileOvershoot(profile))

    override suspend fun setWLedProfilePreheat(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfilePreheat(profile))

    override suspend fun setWLedProfileProbe(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileProbe(profile))

    override suspend fun setWLedProfileTarget(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileTarget(profile))

    override suspend fun setWLedProfileTimer(profile: Int): Result<Server, DataError> = transformResult(settingsApi.setWLedProfileTimer(profile))

    override suspend fun setOneSignalEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setOneSignalEnabled(enabled))

    override suspend fun setOneSignalAppID(appID: String): Result<Server, DataError> = transformResult(settingsApi.setOneSignalAppID(appID))

    override suspend fun registerOneSignalDevice(
        device: Map<String, OneSignalDeviceInfo>,
    ): Result<Server, DataError> = transformResult(settingsApi.registerOneSignalDevice(device))

    override suspend fun setPelletWarningEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setPelletWarningEnabled(enabled))

    override suspend fun setPelletPrimeIgnition(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setPelletPrimeIgnition(enabled))

    override suspend fun setPelletWarningTime(time: Int): Result<Server, DataError> = transformResult(settingsApi.setPelletWarningTime(time))

    override suspend fun setPelletWarningLevel(level: Int): Result<Server, DataError> = transformResult(settingsApi.setPelletWarningLevel(level))

    override suspend fun setPelletsEmpty(level: Int): Result<Server, DataError> = transformResult(settingsApi.setPelletsEmpty(level))

    override suspend fun setPelletsFull(level: Int): Result<Server, DataError> = transformResult(settingsApi.setPelletsFull(level))

    override suspend fun setPelletsAugerRate(rate: Double): Result<Server, DataError> = transformResult(settingsApi.setPelletsAugerRate(rate))

    override suspend fun setUpdateProbeInfo(probeInfo: ProbeInfoUpdate): Result<Server, DataError> {
        val probesDto = ProbesDto(
            name = probeInfo.name,
            label = probeInfo.label,
            profileId = probeInfo.profileId,
            enabled = probeInfo.enabled,
        )
        return transformResult(settingsApi.setUpdateProbeInfo(probesDto))
    }

    override suspend fun setTempUnits(units: String): Result<Server, DataError> = transformResult(settingsApi.setTempUnits(units))

    override suspend fun setAugerOnTime(time: Int): Result<Server, DataError> = transformResult(settingsApi.setAugerOnTime(time))

    override suspend fun setAugerOffTime(time: Int): Result<Server, DataError> = transformResult(settingsApi.setAugerOffTime(time))

    override suspend fun setPMode(mode: Int): Result<Server, DataError> = transformResult(settingsApi.setPMode(mode))

    override suspend fun setSPlusEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setSPlusEnabled(enabled))

    override suspend fun setFanRampEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setFanRampEnabled(enabled))

    override suspend fun setFanRampDutyCycle(dutyCycle: Int): Result<Server, DataError> = transformResult(settingsApi.setFanRampDutyCycle(dutyCycle))

    override suspend fun setFanOnTime(time: Int): Result<Server, DataError> = transformResult(settingsApi.setFanOnTime(time))

    override suspend fun setFanOffTime(time: Int): Result<Server, DataError> = transformResult(settingsApi.setFanOffTime(time))

    override suspend fun setMinTemp(temp: Int): Result<Server, DataError> = transformResult(settingsApi.setMinTemp(temp))

    override suspend fun setMaxTemp(temp: Int): Result<Server, DataError> = transformResult(settingsApi.setMaxTemp(temp))

    override suspend fun setLidOpenDetectEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setLidOpenDetectEnabled(enabled))

    override suspend fun setLidOpenThresh(thresh: Int): Result<Server, DataError> = transformResult(settingsApi.setLidOpenThresh(thresh))

    override suspend fun setLidOpenPauseTime(time: Int): Result<Server, DataError> = transformResult(settingsApi.setLidOpenPauseTime(time))

    override suspend fun setFanPidEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setFanPidEnabled(enabled))

    override suspend fun setKeepWarmEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setKeepWarmEnabled(enabled))

    override suspend fun setKeepWarmTemp(temp: Int): Result<Server, DataError> = transformResult(settingsApi.setKeepWarmTemp(temp))

    override suspend fun setCntrlrSelected(selected: String): Result<Server, DataError> = transformResult(settingsApi.setCntrlrSelected(selected))

    override suspend fun setCntrlrPidPb(pb: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidPb(pb))

    override suspend fun setCntrlrPidTd(td: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidTd(td))

    override suspend fun setCntrlrPidTi(ti: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidTi(ti))

    override suspend fun setCntrlrPidCenter(center: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidCenter(center))

    override suspend fun setCntrlrPidAcPb(pb: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidAcPb(pb))

    override suspend fun setCntrlrPidAcTd(td: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidAcTd(td))

    override suspend fun setCntrlrPidAcTi(ti: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidAcTi(ti))

    override suspend fun setCntrlrPidAcStable(stable: Int): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidAcStable(stable))

    override suspend fun setCntrlrPidAcCenter(center: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidAcCenter(center))

    override suspend fun setCntrlrPidSpPb(pb: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidSpPb(pb))

    override suspend fun setCntrlrPidSpTd(td: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidSpTd(td))

    override suspend fun setCntrlrPidSpTi(ti: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidSpTi(ti))

    override suspend fun setCntrlrPidSpStable(stable: Int): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidSpStable(stable))

    override suspend fun setCntrlrPidSpCenter(center: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidSpCenter(center))

    override suspend fun setCntrlrPidSpTau(tau: Int): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidSpTau(tau))

    override suspend fun setCntrlrPidSpTheta(theta: Int): Result<Server, DataError> = transformResult(settingsApi.setCntrlrPidSpTheta(theta))

    override suspend fun setCntrlrTime(time: Int): Result<Server, DataError> = transformResult(settingsApi.setCntrlrTime(time))

    override suspend fun setCntrlruMin(uMin: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlruMin(uMin))

    override suspend fun setCntrlruMax(uMax: Double): Result<Server, DataError> = transformResult(settingsApi.setCntrlruMax(uMax))

    override suspend fun setPWMControlDefault(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setPWMControlDefault(enabled))

    override suspend fun setPWMTempUpdateTime(time: Int): Result<Server, DataError> = transformResult(settingsApi.setPWMTempUpdateTime(time))

    override suspend fun setPWMFanFrequency(frequency: Int): Result<Server, DataError> = transformResult(settingsApi.setPWMFanFrequency(frequency))

    override suspend fun setPWMMinDutyCycle(dutyCycle: Int): Result<Server, DataError> = transformResult(settingsApi.setPWMMinDutyCycle(dutyCycle))

    override suspend fun setPWMMaxDutyCycle(dutyCycle: Int): Result<Server, DataError> = transformResult(settingsApi.setPWMMaxDutyCycle(dutyCycle))

    override suspend fun setPWMControl(
        tempRange: List<Int>,
        profiles: List<PwmProfile>,
    ): Result<Server, DataError> {
        val profileDtos = profiles.map { Pwm.Profile(dutyCycle = it.dutyCycle) }
        return transformResult(settingsApi.setPWMControl(tempRange, profileDtos))
    }

    override suspend fun setSafetyStartupCheck(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setSafetyStartupCheck(enabled))

    override suspend fun setAllowManualChanges(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setAllowManualChanges(enabled))

    override suspend fun setOverrideTime(time: Int): Result<Server, DataError> = transformResult(settingsApi.setOverrideTime(time))

    override suspend fun setMinStartTemp(temp: Int): Result<Server, DataError> = transformResult(settingsApi.setMinStartTemp(temp))

    override suspend fun setMaxStartTemp(temp: Int): Result<Server, DataError> = transformResult(settingsApi.setMaxStartTemp(temp))

    override suspend fun setMaxGrillTemp(temp: Int): Result<Server, DataError> = transformResult(settingsApi.setMaxGrillTemp(temp))

    override suspend fun setReigniteRetries(retries: Int): Result<Server, DataError> = transformResult(settingsApi.setReigniteRetries(retries))

    override suspend fun setStartupDuration(duration: Int): Result<Server, DataError> = transformResult(settingsApi.setStartupDuration(duration))

    override suspend fun setPrimeOnStartup(amount: Int): Result<Server, DataError> = transformResult(settingsApi.setPrimeOnStartup(amount))

    override suspend fun setStartupPWMDuty(duty: Int): Result<Server, DataError> = transformResult(settingsApi.setStartupPWMDuty(duty))

    override suspend fun setStartExitTemp(temp: Int): Result<Server, DataError> = transformResult(settingsApi.setStartExitTemp(temp))

    override suspend fun setStartToHoldPrompt(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setStartToHoldPrompt(enabled))

    override suspend fun setSmartStartEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setSmartStartEnabled(enabled))

    override suspend fun setSmartStartExitTemp(temp: Int): Result<Server, DataError> = transformResult(settingsApi.setSmartStartExitTemp(temp))

    override suspend fun setSmartStartTable(
        tempRange: List<Int>,
        profiles: List<SmartStartProfile>,
    ): Result<Server, DataError> {
        val profileDtos = profiles.map {
            Startup.SmartStart.SSProfile(
                startUpTime = it.startUpTime,
                augerOnTime = it.augerOnTime,
                pMode = it.pMode,
            )
        }
        return transformResult(settingsApi.setSmartStartTable(tempRange, profileDtos))
    }

    override suspend fun setStartToMode(mode: String): Result<Server, DataError> = transformResult(settingsApi.setStartToMode(mode))

    override suspend fun setStartToModeTemp(temp: Int): Result<Server, DataError> = transformResult(settingsApi.setStartToModeTemp(temp))

    override suspend fun setShutdownDuration(duration: Int): Result<Server, DataError> = transformResult(settingsApi.setShutdownDuration(duration))

    override suspend fun setAutoPowerOffEnabled(enabled: Boolean): Result<Server, DataError> = transformResult(settingsApi.setAutoPowerOffEnabled(enabled))

    override fun getCurrentServerFlow(uuid: String): Flow<Server> = dataStore.data
        .mapNotNull { it.serverMap[uuid] }
        .distinctUntilChanged()

    // Every settings call returns the full settings JSON; parse it off the main thread
    private suspend fun transformResult(
        result: Result<String, DataError>,
    ): Result<Server, DataError> = withContext(Dispatchers.IO) {
        when (result) {
            is Result.Error -> Result.Error(result.error)

            is Result.Success -> {
                when (val data = parseGetResponse(result.data, json, SettingsDtoToDataMapper)) {
                    is Result.Error -> Result.Error(data.error)
                    is Result.Success -> Result.Success(data.data)
                }
            }
        }
    }
}
