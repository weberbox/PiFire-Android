package com.weberbox.pifire.settings.data.model.local

import com.weberbox.pifire.settings.presentation.model.OneSignalPush.OneSignalDeviceInfo
import com.weberbox.pifire.settings.presentation.model.ProbeMap
import com.weberbox.pifire.settings.presentation.model.ProbeProfile
import com.weberbox.pifire.settings.presentation.model.PwmControl
import com.weberbox.pifire.settings.presentation.model.PwmProfile
import com.weberbox.pifire.settings.presentation.model.SmartStart
import com.weberbox.pifire.settings.presentation.model.SmartStartProfile

data class Setting<T>(
    val key: String,
    val value: T,
) {
    companion object {
        // Versions
        val serverVersion = Setting("prefs_server_version", "1.0.0")
        val serverBuild = Setting("prefs_server_build", "1")
        val recipesVersion = Setting("prefs_recipe_version", "1.0.0")
        val cookFileVersion = Setting("prefs_cook_file_version", "1.0.0")

        // Probes
        val probeProfiles = Setting("prefs_probe_profiles", mapOf<String, ProbeProfile>())
        val probeMap = Setting("prefs_probe_map", ProbeMap())

        // Globals
        val grillName = Setting("grill_name", "")
        val adminDebug = Setting("debug_mode", false)
        val primeIgnition = Setting("prime_ignition", false)
        val bootToMonitor = Setting("boot_to_monitor", false)
        val tempUnits = Setting("units", "F")
        val augerRate = Setting("augerrate", 0.3)
        val firstTimeSetup = Setting("first_time_setup", true)
        val extData = Setting("ext_data", false)
        val updatedMessage = Setting("updated_message", false)
        val venv = Setting("venv", false)

        // Platform
        val dcFan = Setting("dc_fan", false)
        val standalone = Setting("standalone", false)
        val realHw = Setting("real_hw", true)
        val platformCurrent = Setting("current", "custom")
        val platformType = Setting("system_type", "prototype")

        // Controller
        val cntrlrSelected = Setting("selected", "pid")
        val cntrlrPidPb = Setting("PB", 60.0)
        val cntrlrPidTd = Setting("Td", 45.0)
        val cntrlrPidTi = Setting("Ti", 180.0)
        val cntrlrPidCenter = Setting("center", 0.5)
        val cntrlrPidAcPb = Setting("PB", 60.0)
        val cntrlrPidAcTd = Setting("Td", 45.0)
        val cntrlrPidAcTi = Setting("Ti", 180.0)
        val cntrlrPidAcCenter = Setting("center_factor", 0.001)
        val cntrlrPidAcStable = Setting("stable_window", 12)
        val cntrlrPidSpPb = Setting("PB", 60.0)
        val cntrlrPidSpTd = Setting("Td", 45.0)
        val cntrlrPidSpTi = Setting("Ti", 180.0)
        val cntrlrPidSpCenter = Setting("center_factor", 0.001)
        val cntrlrPidSpStable = Setting("stable_window", 12)
        val cntrlrPidSpTau = Setting("tau", 115)
        val cntrlrPidSpTheta = Setting("theta", 65)

        // Cycle Data
        val holdCycleTime = Setting("HoldCycleTime", 25)
        val smokeOnCycleTime = Setting("SmokeOnCycleTime", 15)
        val smokeOffCycleTime = Setting("SmokeOffCycleTime", 45)
        val pMode = Setting("PMode", 2)
        val uMin = Setting("u_min", 0.1)
        val uMax = Setting("u_max", 0.9)
        val lidOpenDetectEnabled = Setting("LidOpenDetectEnabled", false)
        val lidOpenThreshold = Setting("LidOpenThreshold", 15)
        val lidOpenPauseTime = Setting("LidOpenPauseTime", 60)

        // Dashboard
        val dashSelected = Setting("current", "Default")
        val dashMaxFoodTempF = Setting("max_food_temp_F", 300)
        val dashMaxFoodTempC = Setting("max_food_temp_C", 150)
        val dashMaxPrimaryTempF = Setting("max_primary_temp_F", 600)
        val dashMaxPrimaryTempC = Setting("max_primary_temp_C", 300)

        // Keep Warm
        val keepWarmTemp = Setting("temp", 165)
        val keepWarmSPlus = Setting("s_plus", false)

        // Smoke Plus
        val sPlusEnabled = Setting("enabled", false)
        val sPlusMinTemp = Setting("min_temp", 160)
        val sPlusMaxTemp = Setting("max_temp", 220)
        val sPlusOnTime = Setting("on_time", 5)
        val sPlusOffTime = Setting("off_time", 5)
        val sPlusDutyCycle = Setting("duty_cycle", 75)
        val sPlusFanRamp = Setting("fan_ramp", false)

        // PWM
        val pwmControl = Setting("pwm_control", false)
        val pwmUpdateTime = Setting("update_time", 10)
        val pwmFrequency = Setting("frequency", 25000)
        val pwmMinDutyCycle = Setting("min_duty_cycle", 20)
        val pwmMaxDutyCycle = Setting("max_duty_cycle", 100)
        val pwmTempRangeList = Setting("temp_range_list", listOf<Int>())
        val pwmProfiles = Setting("profiles", listOf<PwmProfile>())
        val pwmControlList =  Setting("pwm_control", listOf<PwmControl>())

        // Safety
        val safetyStartupCheck = Setting("startup_check", true)
        val safetyMinStartupTemp = Setting("minstartuptemp", 75)
        val safetyMaxStartupTemp = Setting("maxstartuptemp", 100)
        val safetyMaxTemp = Setting("maxtemp", 550)
        val safetyReigniteRetries = Setting("reigniteretries", 1)
        val safetyAllowManualChanges = Setting("allow_manual_changes", false)

        // Shutdown
        val shutdownDuration = Setting("shutdown_duration", 240)
        val shutdownAutoPowerOff = Setting("auto_power_off", false)

        // Startup
        val startupDuration = Setting("duration", 240)
        val startupPrime = Setting("prime_on_startup", 0)
        val startupExitTemp = Setting("startup_exit_temp", 0)
        val startupGotoMode = Setting("after_startup_mode", "Smoke")
        val startupGotoTemp = Setting("primary_setpoint", 165)
        val smartStartEnabled = Setting("enabled", false)
        val smartStartExitTemp = Setting("exit_temp", 120)
        val smartStartTempList = Setting("temp_range_list", listOf<Int>())
        val smartStartProfiles = Setting("profiles", listOf<SmartStartProfile>())
        val smartStartList = Setting("smart_start_list", listOf<SmartStart>())

        // Pellets
        val pelletsEmpty = Setting("empty", 22)
        val pelletsFull = Setting("full", 4)
        val pelletsWarningEnabled = Setting("warning_enabled", true)
        val pelletsWarningLevel = Setting("warning_level", 25)
        val pelletsWarningTime = Setting("warning_time", 20)

        // Modules
        val modulesDisplay = Setting("display", "none")
        val modulesDistance = Setting("dist", "none")
        val modulesPlatform = Setting("grillplat", "prototype")

        // Ifttt
        val iftttEnabled = Setting("enabled", false)
        val iftttApiKey = Setting("APIKey", "")

        // Pushbullet
        val pushbulletEnabled = Setting("enabled", false)
        val pushbulletApiKey = Setting("APIKey", "")
        val pushbulletUrl = Setting("PublicURL", "")

        // Pushover
        val pushoverEnabled = Setting("enabled", false)
        val pushoverApiKey = Setting("APIKey", "")
        val pushoverUserKeys = Setting("UserKeys", "")
        val pushoverUrl = Setting("PublicURL", "")

        // Onesignal
        val onesignalEnabled = Setting("enabled", true)
        val onesignalUuid = Setting("uuid", "")
        val onesignalDevices = Setting("devices", mapOf<String, OneSignalDeviceInfo>())

        // InfluxDb
        val influxdbEnabled = Setting("enabled", false)
        val influxdbUrl = Setting("url", "")
        val influxdbToken = Setting("token", "")
        val influxdbOrg = Setting("org", "")
        val influxdbBucket = Setting("bucket", "")

        // Apprise
        val appriseEnabled = Setting("enabled", false)
        val appriseLocations = Setting("locations", listOf<String>())

        // Mqtt
        val mqttEnabled = Setting("enabled", false)
        val mqttBroker = Setting("broker", "homeassistant.local")
        val mqttTopic = Setting("homeassistant_autodiscovery_topic", "homeassistant")
        val mqttId = Setting("id", "PiFire")
        val mqttUser= Setting("username", "")
        val mqttPass = Setting("password", "")
        val mqttPort = Setting("port", 1883)
        val mqttUpdateSec = Setting("update_sec", 30)
    }
}

