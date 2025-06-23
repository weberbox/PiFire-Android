package com.weberbox.pifire.settings.data.repo

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.model.PostDto.ProbesDto
import com.weberbox.pifire.settings.data.model.remote.Pwm.Profile
import com.weberbox.pifire.settings.data.model.remote.Startup.SmartStart.SSProfile
import com.weberbox.pifire.settings.presentation.model.OneSignalPush.OneSignalDeviceInfo
import com.weberbox.pifire.settings.presentation.model.SettingsData
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import kotlinx.coroutines.flow.Flow

interface SettingsRepo {
    suspend fun getSettings(): Result<Server, DataError>
    suspend fun listenSettingsData(): Flow<Result<Server, DataError>>
    suspend fun getCachedData(): Result<Server, DataError.Local>
    suspend fun updateServerSettings(server: Server)
    suspend fun getSettingsFlow(): Flow<SettingsData>
    suspend fun getServerMap(): Map<String, Server>?
    suspend fun getCurrentServer(): Server?
    suspend fun getCurrentServerItem(uuid: String): Server?
    suspend fun toggleHeadersEnabled(enabled: Boolean, uuid: String)
    suspend fun toggleCredentialsEnabled(enabled: Boolean, uuid: String)
    suspend fun updateServerList(server: Server, uuid: String)
    suspend fun updateServerAddress(address: String, uuid: String)
    suspend fun selectServer(uuid: String): Server?
    suspend fun deleteServer(uuid: String)
    fun getCurrentServerFlow(uuid: String): Flow<Server>
    fun getCurrentServerFlow(): Flow<Server>
    suspend fun deleteHistory(): Result<Unit, DataError>
    suspend fun deleteEvents(): Result<Unit, DataError>
    suspend fun deletePelletLogs(): Result<Unit, DataError>
    suspend fun deletePellets(): Result<Unit, DataError>
    suspend fun factoryReset(): Result<Unit, DataError>
    suspend fun restartSystem(): Result<Unit, DataError>
    suspend fun rebootSystem(): Result<Unit, DataError>
    suspend fun shutdownSystem(): Result<Unit, DataError>
    suspend fun setDebugMode(enabled: Boolean): Result<Server, DataError>
    suspend fun setBootToMonitor(enabled: Boolean): Result<Server, DataError>
    suspend fun setGrillName(name: String): Result<Server, DataError>
    suspend fun getManualData(): Result<Server, DataError>
    suspend fun setManualMode(enabled: Boolean): Result<Server, DataError>
    suspend fun setManualFanOutput(enabled: Boolean): Result<Server, DataError>
    suspend fun setManualAugerOutput(enabled: Boolean): Result<Server, DataError>
    suspend fun setManualIgniterOutput(enabled: Boolean): Result<Server, DataError>
    suspend fun setManualPowerOutput(enabled: Boolean): Result<Server, DataError>
    suspend fun setManualPWMOutput(pwm: Int): Result<Server, DataError>
    suspend fun setIFTTTEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setIFTTTAPIKey(apiKey: String): Result<Server, DataError>
    suspend fun setPushOverEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setPushOverAPIKey(apiKey: String): Result<Server, DataError>
    suspend fun setPushOverUserKeys(userKeys: String): Result<Server, DataError>
    suspend fun setPushOverUrl(url: String): Result<Server, DataError>
    suspend fun setPushBulletEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setPushBulletAPIKey(apiKey: String): Result<Server, DataError>
    suspend fun setPushBulletURL(url: String): Result<Server, DataError>
    suspend fun setInfluxDBEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setInfluxDBUrl(url: String): Result<Server, DataError>
    suspend fun setInfluxDBToken(token: String): Result<Server, DataError>
    suspend fun setInfluxDBOrg(org: String): Result<Server, DataError>
    suspend fun setInfluxDBBucket(bucket: String): Result<Server, DataError>
    suspend fun setMqttEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setMqttBroker(broker: String): Result<Server, DataError>
    suspend fun setMqttTopic(topic: String): Result<Server, DataError>
    suspend fun setMqttId(id: String): Result<Server, DataError>
    suspend fun setMqttPassword(password: String): Result<Server, DataError>
    suspend fun setMqttPort(port: Int): Result<Server, DataError>
    suspend fun setMqttUpdateSec(updateSec: Int): Result<Server, DataError>
    suspend fun setMqttUsername(username: String): Result<Server, DataError>
    suspend fun setAppriseEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setAppriseLocations(locations: List<String>): Result<Server, DataError>
    suspend fun setOneSignalEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setOneSignalAppID(appID: String): Result<Server, DataError>
    suspend fun registerOneSignalDevice(device: Map<String, OneSignalDeviceInfo>):
            Result<Server, DataError>

    suspend fun setPelletWarningEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setPelletPrimeIgnition(enabled: Boolean): Result<Server, DataError>
    suspend fun setPelletWarningTime(time: Int): Result<Server, DataError>
    suspend fun setPelletWarningLevel(level: Int): Result<Server, DataError>
    suspend fun setPelletsEmpty(level: Int): Result<Server, DataError>
    suspend fun setPelletsFull(level: Int): Result<Server, DataError>
    suspend fun setPelletsAugerRate(rate: Double): Result<Server, DataError>
    suspend fun setUpdateProbeInfo(probesDto: ProbesDto): Result<Server, DataError>
    suspend fun setTempUnits(units: String): Result<Server, DataError>
    suspend fun setAugerOnTime(time: Int): Result<Server, DataError>
    suspend fun setAugerOffTime(time: Int): Result<Server, DataError>
    suspend fun setPMode(mode: Int): Result<Server, DataError>
    suspend fun setSPlusEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setFanRampEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setFanRampDutyCycle(dutyCycle: Int): Result<Server, DataError>
    suspend fun setFanOnTime(time: Int): Result<Server, DataError>
    suspend fun setFanOffTime(time: Int): Result<Server, DataError>
    suspend fun setMinTemp(temp: Int): Result<Server, DataError>
    suspend fun setMaxTemp(temp: Int): Result<Server, DataError>
    suspend fun setLidOpenDetectEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setLidOpenThresh(thresh: Int): Result<Server, DataError>
    suspend fun setLidOpenPauseTime(time: Int): Result<Server, DataError>
    suspend fun setKeepWarmEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setKeepWarmTemp(temp: Int): Result<Server, DataError>
    suspend fun setCntrlrSelected(selected: String): Result<Server, DataError>
    suspend fun setCntrlrPidPb(pb: Double): Result<Server, DataError>
    suspend fun setCntrlrPidTd(td: Double): Result<Server, DataError>
    suspend fun setCntrlrPidTi(ti: Double): Result<Server, DataError>
    suspend fun setCntrlrPidCenter(center: Double): Result<Server, DataError>
    suspend fun setCntrlrPidAcPb(pb: Double): Result<Server, DataError>
    suspend fun setCntrlrPidAcTd(td: Double): Result<Server, DataError>
    suspend fun setCntrlrPidAcTi(ti: Double): Result<Server, DataError>
    suspend fun setCntrlrPidAcStable(stable: Int): Result<Server, DataError>
    suspend fun setCntrlrPidAcCenter(center: Double): Result<Server, DataError>
    suspend fun setCntrlrPidSpPb(pb: Double): Result<Server, DataError>
    suspend fun setCntrlrPidSpTd(td: Double): Result<Server, DataError>
    suspend fun setCntrlrPidSpTi(ti: Double): Result<Server, DataError>
    suspend fun setCntrlrPidSpStable(stable: Int): Result<Server, DataError>
    suspend fun setCntrlrPidSpCenter(center: Double): Result<Server, DataError>
    suspend fun setCntrlrPidSpTau(tau: Int): Result<Server, DataError>
    suspend fun setCntrlrPidSpTheta(theta: Int): Result<Server, DataError>
    suspend fun setCntrlrTime(time: Int): Result<Server, DataError>
    suspend fun setCntrlruMin(uMin: Double): Result<Server, DataError>
    suspend fun setCntrlruMax(uMax: Double): Result<Server, DataError>
    suspend fun setPWMControlDefault(enabled: Boolean): Result<Server, DataError>
    suspend fun setPWMTempUpdateTime(time: Int): Result<Server, DataError>
    suspend fun setPWMFanFrequency(frequency: Int): Result<Server, DataError>
    suspend fun setPWMMinDutyCycle(dutyCycle: Int): Result<Server, DataError>
    suspend fun setPWMMaxDutyCycle(dutyCycle: Int): Result<Server, DataError>
    suspend fun setPWMControl(
        tempRange: List<Int>,
        profiles: List<Profile>
    ): Result<Server, DataError>

    suspend fun setSafetyStartupCheck(enabled: Boolean): Result<Server, DataError>
    suspend fun setMinStartTemp(temp: Int): Result<Server, DataError>
    suspend fun setMaxStartTemp(temp: Int): Result<Server, DataError>
    suspend fun setMaxGrillTemp(temp: Int): Result<Server, DataError>
    suspend fun setReigniteRetries(retries: Int): Result<Server, DataError>
    suspend fun setStartupDuration(duration: Int): Result<Server, DataError>
    suspend fun setPrimeOnStartup(amount: Int): Result<Server, DataError>
    suspend fun setStartExitTemp(temp: Int): Result<Server, DataError>
    suspend fun setSmartStartEnabled(enabled: Boolean): Result<Server, DataError>
    suspend fun setSmartStartExitTemp(temp: Int): Result<Server, DataError>
    suspend fun setSmartStartTable(tempRange: List<Int>, profiles: List<SSProfile>)
            : Result<Server, DataError>

    suspend fun setStartToMode(mode: String): Result<Server, DataError>
    suspend fun setStartToModeTemp(temp: Int): Result<Server, DataError>
    suspend fun setShutdownDuration(duration: Int): Result<Server, DataError>
    suspend fun setAutoPowerOffEnabled(enabled: Boolean): Result<Server, DataError>
}