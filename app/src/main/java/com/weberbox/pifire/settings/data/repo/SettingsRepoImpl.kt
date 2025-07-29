package com.weberbox.pifire.settings.data.repo

import androidx.datastore.core.DataStore
import com.google.android.datatransport.runtime.dagger.Lazy
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.model.PostDto.ProbesDto
import com.weberbox.pifire.common.data.parser.parseGetResponse
import com.weberbox.pifire.common.data.parser.parseResponse
import com.weberbox.pifire.common.presentation.state.SessionStateHolder
import com.weberbox.pifire.settings.data.api.SettingsApi
import com.weberbox.pifire.settings.data.model.remote.Pwm
import com.weberbox.pifire.settings.data.model.remote.Startup
import com.weberbox.pifire.settings.data.util.ServerDataManager
import com.weberbox.pifire.settings.domain.SettingsDtoToDataMapper
import com.weberbox.pifire.settings.presentation.model.OneSignalPush.OneSignalDeviceInfo
import com.weberbox.pifire.settings.presentation.model.SettingsData
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SettingsRepoImpl @Inject constructor(
    private val settingsApi: SettingsApi,
    private val sessionStateHolder: SessionStateHolder,
    private val serverDataManager: Lazy<ServerDataManager>,
    private val dataStore: DataStore<SettingsData>,
    private val json: Json
) : SettingsRepo {

    override suspend fun getSettings(): Result<Server, DataError> {
        return settingsApi.getSettings().let { result ->
            when (result) {
                is Result.Error -> Result.Error(result.error)
                is Result.Success -> {
                    parseGetResponse(result.data, json, SettingsDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> Result.Error(data.error)
                            is Result.Success -> {
                                sessionStateHolder.setSettingsData(data.data)
                                updateServerSettings(data.data)
                                Result.Success(data.data)
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun listenSettingsData(): Flow<Result<Server, DataError>> {
        val flow = settingsApi.listenSettingsData()
        return flow.transform { result ->
            when (result) {
                is Result.Error -> emit(Result.Error(result.error))
                is Result.Success -> {
                    parseResponse(result.data, json, SettingsDtoToDataMapper).let { data ->
                        when (data) {
                            is Result.Error -> emit(Result.Error(data.error))
                            is Result.Success -> {
                                sessionStateHolder.setSettingsData(data.data)
                                emit(Result.Success(data.data))
                            }
                        }
                    }
                }
            }
        }
    }

    override suspend fun getCachedData(): Result<Server, DataError.Local> {
        val data = dataStore.data.firstOrNull()
        val currentServer = sessionStateHolder.currentServerUuid
        return if (data != null && currentServer.isNotBlank()) {
            data.serverMap[currentServer]?.let { server ->
                Result.Success(server)
            } ?: Result.Error(DataError.Local.NO_CACHED_DATA)
        } else {
            Result.Error(DataError.Local.NO_CACHED_DATA)
        }
    }

    override suspend fun updateServerSettings(server: Server) {
        dataStore.updateData { data ->
            if (!data.serverMap.keys.contains(server.uuid) && server.uuid.isNotBlank()) {
                data.copy(
                    serverMap = data.serverMap.plus(server.uuid to server)
                )
            } else {
                data.copy(
                    serverMap = data.serverMap.mapValues { (uuid, item) ->
                        if (server.uuid == uuid) {
                            item.copy(
                                name = server.settings.grillName,
                                settings = server.settings
                            )
                        } else {
                            item
                        }
                    }
                )
            }
        }
    }

    override suspend fun getSettingsFlow(): Flow<SettingsData> {
        return dataStore.data
    }

    override suspend fun getServerMap(): Map<String, Server>? {
        return dataStore.data.firstOrNull()?.serverMap
    }

    override suspend fun getCurrentServer(): Server? {
        val server = dataStore.data.firstOrNull()?.currentServerUuid
        return getServerMap()?.get(server)
    }

    override suspend fun getCurrentServerItem(uuid: String): Server? {
        return getServerMap()?.get(uuid)
    }

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
                serverMap = data.serverMap.plus(uuid to server)
            )
        }
    }

    override suspend fun updateServerAddress(address: String, uuid: String) {
        getCurrentServerItem(uuid)?.let {
            val server = it.copy(address = address)
            updateServerList(server, uuid)
        }
    }

    override suspend fun selectServer(uuid: String): Server? {
        return dataStore.updateData { data ->
            data.copy(
                currentServerUuid = uuid
            )
        }.serverMap[uuid]
    }

    override suspend fun deleteServer(uuid: String) {
        serverDataManager.get().deleteServer(uuid)
    }

    override fun getCurrentServerFlow(): Flow<Server> = callbackFlow {
        val currentUuid = dataStore.data.firstOrNull()?.currentServerUuid
        getSettingsFlow().collect {
            it.serverMap[currentUuid]?.let { server ->
                send(server)
            }
        }
    }

    override suspend fun deleteHistory(): Result<Unit, DataError> {
        return settingsApi.deleteHistory()
    }

    override suspend fun deleteEvents(): Result<Unit, DataError> {
        return settingsApi.deleteEvents()
    }

    override suspend fun deletePelletLogs(): Result<Unit, DataError> {
        return settingsApi.deletePelletLogs()
    }

    override suspend fun deletePellets(): Result<Unit, DataError> {
        return settingsApi.deletePellets()
    }

    override suspend fun factoryReset(): Result<Unit, DataError> {
        return settingsApi.factoryReset()
    }

    override suspend fun restartControl(): Result<Unit, DataError> {
        return settingsApi.restartControl()
    }

    override suspend fun restartWebApp(): Result<Unit, DataError> {
        return settingsApi.restartWebApp()
    }

    override suspend fun restartSupervisor(): Result<Unit, DataError> {
        return settingsApi.restartSupervisor()
    }

    override suspend fun rebootSystem(): Result<Unit, DataError> {
        return settingsApi.rebootSystem()
    }

    override suspend fun shutdownSystem(): Result<Unit, DataError> {
        return settingsApi.shutdownSystem()
    }

    override suspend fun setDebugMode(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setDebugMode(enabled))
    }

    override suspend fun setBootToMonitor(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setBootToMonitor(enabled))
    }

    override suspend fun setGrillName(name: String): Result<Server, DataError> {
        return transformResult(settingsApi.setGrillName(name))
    }

    override suspend fun getManualData(): Result<Server, DataError> {
        return transformResult(settingsApi.getManualData())
    }

    override suspend fun setManualMode(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setManualMode(enabled))
    }

    override suspend fun setManualFanOutput(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setManualFanOutput(enabled))
    }

    override suspend fun setManualAugerOutput(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setManualAugerOutput(enabled))
    }

    override suspend fun setManualIgniterOutput(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setManualIgniterOutput(enabled))
    }

    override suspend fun setManualPowerOutput(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setManualPowerOutput(enabled))
    }

    override suspend fun setManualPWMOutput(pwm: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setManualPWMOutput(pwm))
    }

    override suspend fun setIFTTTEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setIFTTTEnabled(enabled))
    }

    override suspend fun setIFTTTAPIKey(apiKey: String): Result<Server, DataError> {
        return transformResult(settingsApi.setIFTTTAPIKey(apiKey))
    }

    override suspend fun setPushOverEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setPushOverEnabled(enabled))
    }

    override suspend fun setPushOverAPIKey(apiKey: String): Result<Server, DataError> {
        return transformResult(settingsApi.setPushOverAPIKey(apiKey))
    }

    override suspend fun setPushOverUserKeys(userKeys: String): Result<Server, DataError> {
        return transformResult(settingsApi.setPushOverUserKeys(userKeys))
    }

    override suspend fun setPushOverUrl(url: String): Result<Server, DataError> {
        return transformResult(settingsApi.setPushOverUrl(url))
    }

    override suspend fun setPushBulletEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setPushBulletEnabled(enabled))
    }

    override suspend fun setPushBulletAPIKey(apiKey: String): Result<Server, DataError> {
        return transformResult(settingsApi.setPushBulletAPIKey(apiKey))
    }

    override suspend fun setPushBulletURL(url: String): Result<Server, DataError> {
        return transformResult(settingsApi.setPushBulletURL(url))
    }

    override suspend fun setInfluxDBEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setInfluxDBEnabled(enabled))
    }

    override suspend fun setInfluxDBUrl(url: String): Result<Server, DataError> {
        return transformResult(settingsApi.setInfluxDBUrl(url))
    }

    override suspend fun setInfluxDBToken(token: String): Result<Server, DataError> {
        return transformResult(settingsApi.setInfluxDBToken(token))
    }

    override suspend fun setInfluxDBOrg(org: String): Result<Server, DataError> {
        return transformResult(settingsApi.setInfluxDBOrg(org))
    }

    override suspend fun setInfluxDBBucket(bucket: String): Result<Server, DataError> {
        return transformResult(settingsApi.setInfluxDBBucket(bucket))
    }

    override suspend fun setMqttEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setMqttEnabled(enabled))
    }

    override suspend fun setMqttBroker(broker: String): Result<Server, DataError> {
        return transformResult(settingsApi.setMqttBroker(broker))
    }

    override suspend fun setMqttTopic(topic: String): Result<Server, DataError> {
        return transformResult(settingsApi.setMqttTopic(topic))
    }

    override suspend fun setMqttId(id: String): Result<Server, DataError> {
        return transformResult(settingsApi.setMqttId(id))
    }

    override suspend fun setMqttPassword(password: String): Result<Server, DataError> {
        return transformResult(settingsApi.setMqttPassword(password))
    }

    override suspend fun setMqttPort(port: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setMqttPort(port))
    }

    override suspend fun setMqttUpdateSec(updateSec: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setMqttUpdateSec(updateSec))
    }

    override suspend fun setMqttUsername(username: String): Result<Server, DataError> {
        return transformResult(settingsApi.setMqttUsername(username))
    }

    override suspend fun setAppriseEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setAppriseEnabled(enabled))
    }

    override suspend fun setAppriseLocations(locations: List<String>): Result<Server, DataError> {
        return transformResult(settingsApi.setAppriseLocations(locations))
    }

    override suspend fun setOneSignalEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setOneSignalEnabled(enabled))
    }

    override suspend fun setOneSignalAppID(appID: String): Result<Server, DataError> {
        return transformResult(settingsApi.setOneSignalAppID(appID))
    }

    override suspend fun registerOneSignalDevice(
        device: Map<String, OneSignalDeviceInfo>
    ): Result<Server, DataError> {
        return transformResult(settingsApi.registerOneSignalDevice(device))
    }

    override suspend fun setPelletWarningEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setPelletWarningEnabled(enabled))
    }

    override suspend fun setPelletPrimeIgnition(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setPelletPrimeIgnition(enabled))
    }

    override suspend fun setPelletWarningTime(time: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setPelletWarningTime(time))
    }

    override suspend fun setPelletWarningLevel(level: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setPelletWarningLevel(level))
    }

    override suspend fun setPelletsEmpty(level: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setPelletsEmpty(level))
    }

    override suspend fun setPelletsFull(level: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setPelletsFull(level))
    }

    override suspend fun setPelletsAugerRate(rate: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setPelletsAugerRate(rate))
    }

    override suspend fun setUpdateProbeInfo(probesDto: ProbesDto): Result<Server, DataError> {
        return transformResult(settingsApi.setUpdateProbeInfo(probesDto))
    }

    override suspend fun setTempUnits(units: String): Result<Server, DataError> {
        return transformResult(settingsApi.setTempUnits(units))
    }

    override suspend fun setAugerOnTime(time: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setAugerOnTime(time))
    }

    override suspend fun setAugerOffTime(time: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setAugerOffTime(time))
    }

    override suspend fun setPMode(mode: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setPMode(mode))
    }

    override suspend fun setSPlusEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setSPlusEnabled(enabled))
    }

    override suspend fun setFanRampEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setFanRampEnabled(enabled))
    }

    override suspend fun setFanRampDutyCycle(dutyCycle: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setFanRampDutyCycle(dutyCycle))
    }

    override suspend fun setFanOnTime(time: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setFanOnTime(time))
    }

    override suspend fun setFanOffTime(time: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setFanOffTime(time))
    }

    override suspend fun setMinTemp(temp: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setMinTemp(temp))
    }

    override suspend fun setMaxTemp(temp: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setMaxTemp(temp))
    }

    override suspend fun setLidOpenDetectEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setLidOpenDetectEnabled(enabled))
    }

    override suspend fun setLidOpenThresh(thresh: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setLidOpenThresh(thresh))
    }

    override suspend fun setLidOpenPauseTime(time: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setLidOpenPauseTime(time))
    }

    override suspend fun setKeepWarmEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setKeepWarmEnabled(enabled))
    }

    override suspend fun setKeepWarmTemp(temp: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setKeepWarmTemp(temp))
    }

    override suspend fun setCntrlrSelected(selected: String): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrSelected(selected))
    }

    override suspend fun setCntrlrPidPb(pb: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidPb(pb))
    }

    override suspend fun setCntrlrPidTd(td: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidTd(td))
    }

    override suspend fun setCntrlrPidTi(ti: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidTi(ti))
    }

    override suspend fun setCntrlrPidCenter(center: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidCenter(center))
    }

    override suspend fun setCntrlrPidAcPb(pb: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidAcPb(pb))
    }

    override suspend fun setCntrlrPidAcTd(td: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidAcTd(td))
    }

    override suspend fun setCntrlrPidAcTi(ti: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidAcTi(ti))
    }

    override suspend fun setCntrlrPidAcStable(stable: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidAcStable(stable))
    }

    override suspend fun setCntrlrPidAcCenter(center: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidAcCenter(center))
    }

    override suspend fun setCntrlrPidSpPb(pb: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidSpPb(pb))
    }

    override suspend fun setCntrlrPidSpTd(td: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidSpTd(td))
    }

    override suspend fun setCntrlrPidSpTi(ti: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidSpTi(ti))
    }

    override suspend fun setCntrlrPidSpStable(stable: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidSpStable(stable))
    }

    override suspend fun setCntrlrPidSpCenter(center: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidSpCenter(center))
    }

    override suspend fun setCntrlrPidSpTau(tau: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidSpTau(tau))
    }

    override suspend fun setCntrlrPidSpTheta(theta: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrPidSpTheta(theta))
    }

    override suspend fun setCntrlrTime(time: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlrTime(time))
    }

    override suspend fun setCntrlruMin(uMin: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlruMin(uMin))
    }

    override suspend fun setCntrlruMax(uMax: Double): Result<Server, DataError> {
        return transformResult(settingsApi.setCntrlruMax(uMax))
    }

    override suspend fun setPWMControlDefault(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setPWMControlDefault(enabled))
    }

    override suspend fun setPWMTempUpdateTime(time: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setPWMTempUpdateTime(time))
    }

    override suspend fun setPWMFanFrequency(frequency: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setPWMFanFrequency(frequency))
    }

    override suspend fun setPWMMinDutyCycle(dutyCycle: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setPWMMinDutyCycle(dutyCycle))
    }

    override suspend fun setPWMMaxDutyCycle(dutyCycle: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setPWMMaxDutyCycle(dutyCycle))
    }

    override suspend fun setPWMControl(
        tempRange: List<Int>,
        profiles: List<Pwm.Profile>
    ): Result<Server, DataError> {
        return transformResult(settingsApi.setPWMControl(tempRange, profiles))
    }

    override suspend fun setSafetyStartupCheck(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setSafetyStartupCheck(enabled))
    }

    override suspend fun setAllowManualChanges(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setAllowManualChanges(enabled))
    }

    override suspend fun setOverrideTime(time: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setOverrideTime(time))
    }

    override suspend fun setMinStartTemp(temp: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setMinStartTemp(temp))
    }

    override suspend fun setMaxStartTemp(temp: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setMaxStartTemp(temp))
    }

    override suspend fun setMaxGrillTemp(temp: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setMaxGrillTemp(temp))
    }

    override suspend fun setReigniteRetries(retries: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setReigniteRetries(retries))
    }

    override suspend fun setStartupDuration(duration: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setStartupDuration(duration))
    }

    override suspend fun setPrimeOnStartup(amount: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setPrimeOnStartup(amount))
    }

    override suspend fun setStartExitTemp(temp: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setStartExitTemp(temp))
    }

    override suspend fun setStartToHoldPrompt(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setStartToHoldPrompt(enabled))
    }

    override suspend fun setSmartStartEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setSmartStartEnabled(enabled))
    }

    override suspend fun setSmartStartExitTemp(temp: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setSmartStartExitTemp(temp))
    }

    override suspend fun setSmartStartTable(
        tempRange: List<Int>,
        profiles: List<Startup.SmartStart.SSProfile>
    ): Result<Server, DataError> {
        return transformResult(settingsApi.setSmartStartTable(tempRange, profiles))
    }

    override suspend fun setStartToMode(mode: String): Result<Server, DataError> {
        return transformResult(settingsApi.setStartToMode(mode))
    }

    override suspend fun setStartToModeTemp(temp: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setStartToModeTemp(temp))
    }

    override suspend fun setShutdownDuration(duration: Int): Result<Server, DataError> {
        return transformResult(settingsApi.setShutdownDuration(duration))
    }

    override suspend fun setAutoPowerOffEnabled(enabled: Boolean): Result<Server, DataError> {
        return transformResult(settingsApi.setAutoPowerOffEnabled(enabled))
    }

    override fun getCurrentServerFlow(uuid: String): Flow<Server> = callbackFlow {
        getSettingsFlow().collect {
            it.serverMap[uuid]?.let { server ->
                send(server)
            }
        }
    }

    private suspend fun transformResult(
        result: Result<String, DataError>
    ): Result<Server, DataError> {
        return when (result) {
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