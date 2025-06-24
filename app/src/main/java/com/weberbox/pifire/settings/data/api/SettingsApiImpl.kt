package com.weberbox.pifire.settings.data.api

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.model.PostDto
import com.weberbox.pifire.common.data.model.PostDto.ControlDto
import com.weberbox.pifire.common.data.model.PostDto.ProbesDto
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.singleton.SocketManager
import com.weberbox.pifire.dashboard.presentation.model.RunningMode
import com.weberbox.pifire.settings.data.model.remote.Controller
import com.weberbox.pifire.settings.data.model.remote.Controller.Config
import com.weberbox.pifire.settings.data.model.remote.Controller.Config.Pid
import com.weberbox.pifire.settings.data.model.remote.Controller.Config.PidAc
import com.weberbox.pifire.settings.data.model.remote.Controller.Config.PidSp
import com.weberbox.pifire.settings.data.model.remote.CycleData
import com.weberbox.pifire.settings.data.model.remote.Globals
import com.weberbox.pifire.settings.data.model.remote.KeepWarm
import com.weberbox.pifire.settings.data.model.remote.ManualDto
import com.weberbox.pifire.settings.data.model.remote.ManualDto.Manual
import com.weberbox.pifire.settings.data.model.remote.NotifyServices
import com.weberbox.pifire.settings.data.model.remote.PelletLevel
import com.weberbox.pifire.settings.data.model.remote.Pwm
import com.weberbox.pifire.settings.data.model.remote.Pwm.Profile
import com.weberbox.pifire.settings.data.model.remote.Safety
import com.weberbox.pifire.settings.data.model.remote.SettingsDto
import com.weberbox.pifire.settings.data.model.remote.Shutdown
import com.weberbox.pifire.settings.data.model.remote.SmokePlus
import com.weberbox.pifire.settings.data.model.remote.Startup
import com.weberbox.pifire.settings.data.model.remote.Startup.SmartStart
import com.weberbox.pifire.settings.data.model.remote.Startup.SmartStart.SSProfile
import com.weberbox.pifire.settings.data.model.remote.Startup.StartToMode
import com.weberbox.pifire.settings.presentation.model.OneSignalPush.OneSignalDeviceInfo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.transform
import kotlinx.serialization.json.Json

class SettingsApiImpl @Inject constructor(
    private val socketManager: SocketManager,
    private val json: Json
) : SettingsApi {

    override suspend fun getSettings(): Result<String, DataError> {
        return socketManager.emitGet(ServerConstants.GA_SETTINGS_DATA)
    }

    override suspend fun listenSettingsData(): Flow<Result<String, DataError>> {
        val flow = socketManager.onFlow(ServerConstants.LISTEN_SETTINGS_DATA)
        return flow.transform { result ->
            when (result) {
                is Result.Error -> {
                    emit(Result.Error(result.error))
                }

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

    override suspend fun deleteHistory(): Result<Unit, DataError> {
        return sendAdminAction(ServerConstants.PT_CLEAR_HISTORY)
    }

    override suspend fun deleteEvents(): Result<Unit, DataError> {
        return sendAdminAction(ServerConstants.PT_CLEAR_EVENTS)
    }

    override suspend fun deletePelletLogs(): Result<Unit, DataError> {
        return sendAdminAction(ServerConstants.PT_CLEAR_PELLETS_LOG)
    }

    override suspend fun deletePellets(): Result<Unit, DataError> {
        return sendAdminAction(ServerConstants.PT_CLEAR_PELLETS)
    }

    override suspend fun factoryReset(): Result<Unit, DataError> {
        return sendAdminAction(ServerConstants.PT_FACTORY_DEFAULTS)
    }

    override suspend fun restartControl(): Result<Unit, DataError> {
        return sendAdminAction(ServerConstants.PT_RESTART_CONTROL)
    }

    override suspend fun restartWebApp(): Result<Unit, DataError> {
        return sendAdminAction(ServerConstants.PT_RESTART_WEBAPP)
    }

    override suspend fun restartSupervisor(): Result<Unit, DataError> {
        return sendAdminAction(ServerConstants.PT_RESTART_SUPERVISOR)
    }

    override suspend fun rebootSystem(): Result<Unit, DataError> {
        return sendAdminAction(ServerConstants.PT_REBOOT)
    }

    override suspend fun shutdownSystem(): Result<Unit, DataError> {
        return sendAdminAction(ServerConstants.PT_SHUTDOWN)
    }

    override suspend fun setDebugMode(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                globals = Globals(debugMode = enabled)
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setBootToMonitor(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                globals = Globals(bootToMonitor = enabled)
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setGrillName(name: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                globals = Globals(grillName = name)
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun getManualData(): Result<String, DataError> {
        return socketManager.emitGet(ServerConstants.GA_MANUAL_DATA)
    }

    override suspend fun setManualMode(enabled: Boolean): Result<String, DataError> {
        val mode = if (enabled) RunningMode.Manual.name else RunningMode.Stop.name
        val json = json.encodeToString(
            ControlDto(
                currentMode = mode,
                updated = true
            )
        )
        return sendAction(ServerConstants.PT_CONTROL, json)
    }

    override suspend fun setManualFanOutput(enabled: Boolean): Result<String, DataError> {
        val output = if (enabled) ServerConstants.OUTPUT_FAN else null
        val json = json.encodeToString(
            ManualDto(
                manual = Manual(
                    change = ServerConstants.OUTPUT_FAN,
                    output = output
                )
            )
        )
        return sendAction(ServerConstants.PT_CONTROL, json)
    }

    override suspend fun setManualAugerOutput(enabled: Boolean): Result<String, DataError> {
        val output = if (enabled) ServerConstants.OUTPUT_AUGER else null
        val json = json.encodeToString(
            ManualDto(
                manual = Manual(
                    change = ServerConstants.OUTPUT_AUGER,
                    output = output
                )
            )
        )
        return sendAction(ServerConstants.PT_CONTROL, json)
    }

    override suspend fun setManualIgniterOutput(enabled: Boolean): Result<String, DataError> {
        val output = if (enabled) ServerConstants.OUTPUT_IGNITER else null
        val json = json.encodeToString(
            ManualDto(
                manual = Manual(
                    change = ServerConstants.OUTPUT_IGNITER,
                    output = output
                )
            )
        )
        return sendAction(ServerConstants.PT_CONTROL, json)
    }

    override suspend fun setManualPowerOutput(enabled: Boolean): Result<String, DataError> {
        val output = if (enabled) ServerConstants.OUTPUT_POWER else null
        val json = json.encodeToString(
            ManualDto(
                manual = Manual(
                    change = ServerConstants.OUTPUT_POWER,
                    output = output
                )
            )
        )
        return sendAction(ServerConstants.PT_CONTROL, json)
    }

    override suspend fun setManualPWMOutput(pwm: Int): Result<String, DataError> {
        val json = json.encodeToString(
            ManualDto(
                manual = Manual(
                    change = ServerConstants.OUTPUT_PWM,
                    pwm = pwm
                )
            )
        )
        return sendAction(ServerConstants.PT_CONTROL, json)
    }

    override suspend fun setIFTTTEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    ifttt = NotifyServices.Ifttt(enabled = enabled)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setIFTTTAPIKey(apiKey: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    ifttt = NotifyServices.Ifttt(apiKey = apiKey)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPushOverEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    pushover = NotifyServices.Pushover(enabled = enabled)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPushOverAPIKey(apiKey: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    pushover = NotifyServices.Pushover(apiKey = apiKey)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPushOverUserKeys(userKeys: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    pushover = NotifyServices.Pushover(userKeys = userKeys)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPushOverUrl(url: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    pushover = NotifyServices.Pushover(publicURL = url)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPushBulletEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    pushbullet = NotifyServices.PushBullet(enabled = enabled)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPushBulletAPIKey(apiKey: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    pushbullet = NotifyServices.PushBullet(apiKey = apiKey)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPushBulletURL(url: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    pushbullet = NotifyServices.PushBullet(publicURL = url)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setInfluxDBEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    influxDB = NotifyServices.InfluxDB(enabled = enabled)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setInfluxDBUrl(url: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    influxDB = NotifyServices.InfluxDB(url = url)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setInfluxDBToken(token: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    influxDB = NotifyServices.InfluxDB(token = token)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setInfluxDBOrg(org: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    influxDB = NotifyServices.InfluxDB(org = org)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setInfluxDBBucket(bucket: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    influxDB = NotifyServices.InfluxDB(bucket = bucket)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMqttEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    mqtt = NotifyServices.Mqtt(enabled = enabled)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMqttBroker(broker: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    mqtt = NotifyServices.Mqtt(broker = broker)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMqttTopic(topic: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    mqtt = NotifyServices.Mqtt(topic = topic)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMqttId(id: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    mqtt = NotifyServices.Mqtt(id = id)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMqttPassword(password: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    mqtt = NotifyServices.Mqtt(password = password)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMqttPort(port: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    mqtt = NotifyServices.Mqtt(port = port)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMqttUpdateSec(updateSec: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    mqtt = NotifyServices.Mqtt(updateSec = updateSec)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMqttUsername(username: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    mqtt = NotifyServices.Mqtt(username = username)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setAppriseEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    apprise = NotifyServices.Apprise(enabled = enabled)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setAppriseLocations(locations: List<String>): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    apprise = NotifyServices.Apprise(locations = locations)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setOneSignalEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    onesignal = NotifyServices.OneSignalPush(enabled = enabled)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setOneSignalAppID(appID: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    onesignal = NotifyServices.OneSignalPush(appID = appID)
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun registerOneSignalDevice(devices: Map<String, OneSignalDeviceInfo>):
            Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                notifyServices = NotifyServices(
                    onesignal = NotifyServices.OneSignalPush(
                        devices = devices.mapValues { (_, value) ->
                            NotifyServices.OneSignalPush.OneSignalDeviceInfo(
                                deviceName = value.deviceName,
                                friendlyName = value.friendlyName,
                                appVersion = value.appVersion
                            )
                        })
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPelletWarningEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(pelletLevel = PelletLevel(warningEnabled = enabled))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPelletPrimeIgnition(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(globals = Globals(primeIgnition = enabled))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPelletWarningTime(time: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(pelletLevel = PelletLevel(warningTime = time))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPelletWarningLevel(level: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(pelletLevel = PelletLevel(warningLevel = level))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPelletsEmpty(level: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(pelletLevel = PelletLevel(empty = level))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPelletsFull(level: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(pelletLevel = PelletLevel(full = level))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPelletsAugerRate(rate: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(globals = Globals(augerRate = rate))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setUpdateProbeInfo(probesDto: ProbesDto): Result<String, DataError> {
        val json = json.encodeToString(PostDto(probesDto = probesDto))
        return sendProbesAction(ServerConstants.PT_PROBE_UPDATE, json)
    }

    override suspend fun setTempUnits(units: String): Result<String, DataError> {
        return sendUnitsAction(units)
    }

    override suspend fun setAugerOnTime(time: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(cycleData = CycleData(smokeOnCycleTime = time))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setAugerOffTime(time: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(cycleData = CycleData(smokeOffCycleTime = time))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPMode(mode: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(cycleData = CycleData(pMode = mode))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setSPlusEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(smokePlus = SmokePlus(enabled = enabled))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setFanRampEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(smokePlus = SmokePlus(fanRamp = enabled))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setFanRampDutyCycle(dutyCycle: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(smokePlus = SmokePlus(dutyCycle = dutyCycle))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setFanOnTime(time: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(smokePlus = SmokePlus(onTime = time))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setFanOffTime(time: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(smokePlus = SmokePlus(offTime = time))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMinTemp(temp: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(smokePlus = SmokePlus(minTemp = temp))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMaxTemp(temp: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(smokePlus = SmokePlus(maxTemp = temp))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setLidOpenDetectEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(cycleData = CycleData(lidOpenDetectEnabled = enabled))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setLidOpenThresh(thresh: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(cycleData = CycleData(lidOpenThreshold = thresh))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setLidOpenPauseTime(time: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(cycleData = CycleData(lidOpenPauseTime = time))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setKeepWarmEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(keepWarm = KeepWarm(enabled = enabled))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setKeepWarmTemp(temp: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(keepWarm = KeepWarm(temp = temp))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrSelected(selected: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(controller = Controller(selected = selected))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidPb(pb: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pid = Pid(
                            pb = pb
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidTd(td: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pid = Pid(
                            td = td
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidTi(ti: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pid = Pid(
                            ti = ti
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidCenter(center: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pid = Pid(
                            center = center
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidAcPb(pb: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidAc = PidAc(
                            pb = pb
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidAcTd(td: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidAc = PidAc(
                            td = td
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidAcTi(ti: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidAc = PidAc(
                            ti = ti
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidAcStable(stable: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidAc = PidAc(
                            stableWindow = stable
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidAcCenter(center: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidAc = PidAc(
                            centerFactor = center
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidSpPb(pb: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidSp = PidSp(
                            pb = pb
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidSpTd(td: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidSp = PidSp(
                            td = td
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidSpTi(ti: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidSp = PidSp(
                            ti = ti
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidSpStable(stable: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidSp = PidSp(
                            stableWindow = stable
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidSpCenter(center: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidSp = PidSp(
                            centerFactor = center
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidSpTau(tau: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidSp = PidSp(
                            tau = tau
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrPidSpTheta(theta: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                controller = Controller(
                    config = Config(
                        pidSp = PidSp(
                            theta = theta
                        )
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlrTime(time: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(cycleData = CycleData(holdCycleTime = time))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlruMin(uMin: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(cycleData = CycleData(uMin = uMin))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setCntrlruMax(uMax: Double): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(cycleData = CycleData(uMax = uMax))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPWMControlDefault(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(pwm = Pwm(pwmControl = enabled))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPWMTempUpdateTime(time: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(pwm = Pwm(updateTime = time))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPWMFanFrequency(frequency: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(pwm = Pwm(frequency = frequency))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPWMMinDutyCycle(dutyCycle: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(pwm = Pwm(minDutyCycle = dutyCycle))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPWMMaxDutyCycle(dutyCycle: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(pwm = Pwm(maxDutyCycle = dutyCycle))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPWMControl(
        tempRange: List<Int>, profiles: List<Profile>
    ): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(pwm = Pwm(tempRangeList = tempRange, profiles = profiles))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setSafetyStartupCheck(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(safety = Safety(startupCheck = enabled))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMinStartTemp(temp: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(safety = Safety(minStartupTemp = temp))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMaxStartTemp(temp: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(safety = Safety(maxStartupTemp = temp))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setMaxGrillTemp(temp: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(safety = Safety(maxTemp = temp))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setReigniteRetries(retries: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(safety = Safety(reigniteRetries = retries))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setStartupDuration(duration: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(startup = Startup(duration = duration))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setPrimeOnStartup(amount: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(startup = Startup(primeOnStartup = amount))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setStartExitTemp(temp: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(startup = Startup(startExitTemp = temp))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setSmartStartEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(startup = Startup(smartStart = SmartStart(enabled = enabled)))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setSmartStartExitTemp(temp: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(startup = Startup(smartStart = SmartStart(exitTemp = temp)))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setSmartStartTable(tempRange: List<Int>, profiles: List<SSProfile>)
            : Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(
                startup = Startup(
                    smartStart = SmartStart(
                        tempRangeList = tempRange, profiles = profiles
                    )
                )
            )
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setStartToMode(mode: String): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(startup = Startup(startToMode = StartToMode(afterStartUpMode = mode)))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setStartToModeTemp(temp: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(startup = Startup(startToMode = StartToMode(primarySetPoint = temp)))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setShutdownDuration(duration: Int): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(shutdown = Shutdown(duration = duration))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    override suspend fun setAutoPowerOffEnabled(enabled: Boolean): Result<String, DataError> {
        val json = json.encodeToString(
            SettingsDto(shutdown = Shutdown(autoPowerOff = enabled))
        )
        return sendAction(ServerConstants.PT_SETTINGS, json)
    }

    private suspend fun sendAdminAction(type: String): Result<Unit, DataError> {
        return socketManager.emitPost(ServerConstants.PA_ADMIN_ACTION, type)
    }

    private suspend fun sendAction(type: String, jsonString: String): Result<String, DataError> {
        return socketManager.emitPostResult(ServerConstants.PA_UPDATE_ACTION, type, jsonString)
    }

    private suspend fun sendUnitsAction(units: String): Result<String, DataError> {
        return socketManager.emitPostResult(ServerConstants.PA_UNITS_ACTION, units)
    }

    private suspend fun sendProbesAction(
        type: String,
        jsonString: String
    ): Result<String, DataError> {
        return socketManager.emitPostResult(ServerConstants.PA_PROBES_ACTION, type, jsonString)
    }
}