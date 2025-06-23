package com.weberbox.pifire.settings.data.api

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.model.PostDto.ProbesDto
import com.weberbox.pifire.settings.data.model.remote.Pwm.Profile
import com.weberbox.pifire.settings.data.model.remote.Startup.SmartStart.SSProfile
import com.weberbox.pifire.settings.presentation.model.OneSignalPush.OneSignalDeviceInfo
import kotlinx.coroutines.flow.Flow

interface SettingsApi {
    suspend fun getSettings(): Result<String, DataError>
    suspend fun listenSettingsData(): Flow<Result<String, DataError>>
    suspend fun deleteHistory(): Result<Unit, DataError>
    suspend fun deleteEvents(): Result<Unit, DataError>
    suspend fun deletePelletLogs(): Result<Unit, DataError>
    suspend fun deletePellets(): Result<Unit, DataError>
    suspend fun factoryReset(): Result<Unit, DataError>
    suspend fun restartSystem(): Result<Unit, DataError>
    suspend fun rebootSystem(): Result<Unit, DataError>
    suspend fun shutdownSystem(): Result<Unit, DataError>
    suspend fun setDebugMode(enabled: Boolean): Result<String, DataError>
    suspend fun setBootToMonitor(enabled: Boolean): Result<String, DataError>
    suspend fun setGrillName(name: String): Result<String, DataError>
    suspend fun getManualData(): Result<String, DataError>
    suspend fun setManualMode(enabled: Boolean): Result<String, DataError>
    suspend fun setManualFanOutput(enabled: Boolean): Result<String, DataError>
    suspend fun setManualAugerOutput(enabled: Boolean): Result<String, DataError>
    suspend fun setManualIgniterOutput(enabled: Boolean): Result<String, DataError>
    suspend fun setManualPowerOutput(enabled: Boolean): Result<String, DataError>
    suspend fun setManualPWMOutput(pwm: Int): Result<String, DataError>
    suspend fun setIFTTTEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setIFTTTAPIKey(apiKey: String): Result<String, DataError>
    suspend fun setPushOverEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setPushOverAPIKey(apiKey: String): Result<String, DataError>
    suspend fun setPushOverUserKeys(userKeys: String): Result<String, DataError>
    suspend fun setPushOverUrl(url: String): Result<String, DataError>
    suspend fun setPushBulletEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setPushBulletAPIKey(apiKey: String): Result<String, DataError>
    suspend fun setPushBulletURL(url: String): Result<String, DataError>
    suspend fun setInfluxDBEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setInfluxDBUrl(url: String): Result<String, DataError>
    suspend fun setInfluxDBToken(token: String): Result<String, DataError>
    suspend fun setInfluxDBOrg(org: String): Result<String, DataError>
    suspend fun setInfluxDBBucket(bucket: String): Result<String, DataError>
    suspend fun setMqttEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setMqttBroker(broker: String): Result<String, DataError>
    suspend fun setMqttTopic(topic: String): Result<String, DataError>
    suspend fun setMqttId(id: String): Result<String, DataError>
    suspend fun setMqttPassword(password: String): Result<String, DataError>
    suspend fun setMqttPort(port: Int): Result<String, DataError>
    suspend fun setMqttUpdateSec(updateSec: Int): Result<String, DataError>
    suspend fun setMqttUsername(username: String): Result<String, DataError>
    suspend fun setAppriseEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setAppriseLocations(locations: List<String>): Result<String, DataError>
    suspend fun setOneSignalEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setOneSignalAppID(appID: String): Result<String, DataError>
    suspend fun registerOneSignalDevice(devices: Map<String, OneSignalDeviceInfo>):
            Result<String, DataError>

    suspend fun setPelletWarningEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setPelletPrimeIgnition(enabled: Boolean): Result<String, DataError>
    suspend fun setPelletWarningTime(time: Int): Result<String, DataError>
    suspend fun setPelletWarningLevel(level: Int): Result<String, DataError>
    suspend fun setPelletsEmpty(level: Int): Result<String, DataError>
    suspend fun setPelletsFull(level: Int): Result<String, DataError>
    suspend fun setPelletsAugerRate(rate: Double): Result<String, DataError>
    suspend fun setUpdateProbeInfo(probesDto: ProbesDto): Result<String, DataError>
    suspend fun setTempUnits(units: String): Result<String, DataError>
    suspend fun setAugerOnTime(time: Int): Result<String, DataError>
    suspend fun setAugerOffTime(time: Int): Result<String, DataError>
    suspend fun setPMode(mode: Int): Result<String, DataError>
    suspend fun setSPlusEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setFanRampEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setFanRampDutyCycle(dutyCycle: Int): Result<String, DataError>
    suspend fun setFanOnTime(time: Int): Result<String, DataError>
    suspend fun setFanOffTime(time: Int): Result<String, DataError>
    suspend fun setMinTemp(temp: Int): Result<String, DataError>
    suspend fun setMaxTemp(temp: Int): Result<String, DataError>
    suspend fun setLidOpenDetectEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setLidOpenThresh(thresh: Int): Result<String, DataError>
    suspend fun setLidOpenPauseTime(time: Int): Result<String, DataError>
    suspend fun setKeepWarmEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setKeepWarmTemp(temp: Int): Result<String, DataError>
    suspend fun setCntrlrSelected(selected: String): Result<String, DataError>
    suspend fun setCntrlrPidPb(pb: Double): Result<String, DataError>
    suspend fun setCntrlrPidTd(td: Double): Result<String, DataError>
    suspend fun setCntrlrPidTi(ti: Double): Result<String, DataError>
    suspend fun setCntrlrPidCenter(center: Double): Result<String, DataError>
    suspend fun setCntrlrPidAcPb(pb: Double): Result<String, DataError>
    suspend fun setCntrlrPidAcTd(td: Double): Result<String, DataError>
    suspend fun setCntrlrPidAcTi(ti: Double): Result<String, DataError>
    suspend fun setCntrlrPidAcStable(stable: Int): Result<String, DataError>
    suspend fun setCntrlrPidAcCenter(center: Double): Result<String, DataError>
    suspend fun setCntrlrPidSpPb(pb: Double): Result<String, DataError>
    suspend fun setCntrlrPidSpTd(td: Double): Result<String, DataError>
    suspend fun setCntrlrPidSpTi(ti: Double): Result<String, DataError>
    suspend fun setCntrlrPidSpStable(stable: Int): Result<String, DataError>
    suspend fun setCntrlrPidSpCenter(center: Double): Result<String, DataError>
    suspend fun setCntrlrPidSpTau(tau: Int): Result<String, DataError>
    suspend fun setCntrlrPidSpTheta(theta: Int): Result<String, DataError>
    suspend fun setCntrlrTime(time: Int): Result<String, DataError>
    suspend fun setCntrlruMin(uMin: Double): Result<String, DataError>
    suspend fun setCntrlruMax(uMax: Double): Result<String, DataError>
    suspend fun setPWMControlDefault(enabled: Boolean): Result<String, DataError>
    suspend fun setPWMTempUpdateTime(time: Int): Result<String, DataError>
    suspend fun setPWMFanFrequency(frequency: Int): Result<String, DataError>
    suspend fun setPWMMinDutyCycle(dutyCycle: Int): Result<String, DataError>
    suspend fun setPWMMaxDutyCycle(dutyCycle: Int): Result<String, DataError>
    suspend fun setPWMControl(
        tempRange: List<Int>,
        profiles: List<Profile>
    ): Result<String, DataError>

    suspend fun setSafetyStartupCheck(enabled: Boolean): Result<String, DataError>
    suspend fun setMinStartTemp(temp: Int): Result<String, DataError>
    suspend fun setMaxStartTemp(temp: Int): Result<String, DataError>
    suspend fun setMaxGrillTemp(temp: Int): Result<String, DataError>
    suspend fun setReigniteRetries(retries: Int): Result<String, DataError>
    suspend fun setStartupDuration(duration: Int): Result<String, DataError>
    suspend fun setPrimeOnStartup(amount: Int): Result<String, DataError>
    suspend fun setStartExitTemp(temp: Int): Result<String, DataError>
    suspend fun setSmartStartEnabled(enabled: Boolean): Result<String, DataError>
    suspend fun setSmartStartExitTemp(temp: Int): Result<String, DataError>
    suspend fun setSmartStartTable(tempRange: List<Int>, profiles: List<SSProfile>)
            : Result<String, DataError>

    suspend fun setStartToMode(mode: String): Result<String, DataError>
    suspend fun setStartToModeTemp(temp: Int): Result<String, DataError>
    suspend fun setShutdownDuration(duration: Int): Result<String, DataError>
    suspend fun setAutoPowerOffEnabled(enabled: Boolean): Result<String, DataError>
}