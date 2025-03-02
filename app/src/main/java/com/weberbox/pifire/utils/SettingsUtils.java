package com.weberbox.pifire.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.ServerVersions;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.interfaces.SettingsSocketCallback;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeMap;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeProfileModel;
import com.weberbox.pifire.model.remote.ServerResponseModel;
import com.weberbox.pifire.model.remote.SettingsDataModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.Controller;
import com.weberbox.pifire.model.remote.SettingsDataModel.Controller.Config.Pid;
import com.weberbox.pifire.model.remote.SettingsDataModel.Controller.Config.PidAc;
import com.weberbox.pifire.model.remote.SettingsDataModel.Controller.Config.PidSp;
import com.weberbox.pifire.model.remote.SettingsDataModel.CycleData;
import com.weberbox.pifire.model.remote.SettingsDataModel.Globals;
import com.weberbox.pifire.model.remote.SettingsDataModel.KeepWarm;
import com.weberbox.pifire.model.remote.SettingsDataModel.Modules;
import com.weberbox.pifire.model.remote.SettingsDataModel.NotifyServices.Apprise;
import com.weberbox.pifire.model.remote.SettingsDataModel.NotifyServices.Ifttt;
import com.weberbox.pifire.model.remote.SettingsDataModel.NotifyServices.InfluxDB;
import com.weberbox.pifire.model.remote.SettingsDataModel.NotifyServices.OneSignalPush;
import com.weberbox.pifire.model.remote.SettingsDataModel.NotifyServices.PushBullet;
import com.weberbox.pifire.model.remote.SettingsDataModel.NotifyServices.Pushover;
import com.weberbox.pifire.model.remote.SettingsDataModel.PWM;
import com.weberbox.pifire.model.remote.SettingsDataModel.PelletLevel;
import com.weberbox.pifire.model.remote.SettingsDataModel.Platform;
import com.weberbox.pifire.model.remote.SettingsDataModel.ProbeSettings;
import com.weberbox.pifire.model.remote.SettingsDataModel.Safety;
import com.weberbox.pifire.model.remote.SettingsDataModel.Shutdown;
import com.weberbox.pifire.model.remote.SettingsDataModel.SmokePlus;
import com.weberbox.pifire.model.remote.SettingsDataModel.Startup;
import com.weberbox.pifire.model.remote.SettingsDataModel.Startup.SmartStart;
import com.weberbox.pifire.model.remote.SettingsDataModel.Startup.StartToMode;
import com.weberbox.pifire.model.remote.SettingsDataModel.Versions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.socket.client.Socket;
import timber.log.Timber;

public class SettingsUtils {

    private final Context context;
    private final List<String> errors = new ArrayList<>();
    private SettingsSocketCallback callback;

    public SettingsUtils(@NotNull Context context, @NotNull SettingsSocketCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public SettingsUtils(@NotNull Context context) {
        this.context = context;
    }

    public void requestSettingsData(@NotNull Socket socket) {
        if (socket.connected()) {
            ServerControl.settingsGetEmit(socket, response -> {
                List<String> results = updateSettingsData(response);
                if (callback != null) {
                    callback.onSettingsResult(results);
                }
            });
        }
    }

    public void requestSettingsData(@NotNull Socket socket, SettingsSocketCallback callback) {
        this.callback = callback;
        if (socket.connected()) {
            ServerControl.settingsGetEmit(socket, response -> {
                List<String> results = updateSettingsData(response);
                if (callback != null) {
                    callback.onSettingsResult(results);
                }
            });
        }
    }

    public List<String> updateSettingsData(String response) {
        try {
            SettingsDataModel settingsResponse = SettingsDataModel.parseJSON(response);

            Versions versions = settingsResponse.getVersions();
            if (versions != null) {
                putString(R.string.prefs_server_version, versions.getServerVersion(),
                        "Server Version");
                putString(R.string.prefs_recipe_version, versions.getRecipeVersion(),
                        "Recipe Version");
                putString(R.string.prefs_cook_file_version, versions.getCookFileVersion(),
                        "Cookfile Version");
                putString(R.string.prefs_server_build, versions.getServerBuild(), "Server Build");
            } else {
                errors.add("Versions");
            }

            ProbeSettings probesSettings = settingsResponse.getProbeSettings();
            if (probesSettings != null) {
                Map<String, ProbeProfileModel> probeProfiles = probesSettings.getProbeProfiles();
                putString(R.string.prefs_probe_profiles, new Gson().toJson(probeProfiles),
                        "ProbeSettings Probe Profiles");

                ProbeMap probeMap = probesSettings.getProbeMap();
                putString(R.string.prefs_probe_map, new Gson().toJson(probeMap),
                        "ProbeSettings Probe Map");
            } else {
                errors.add("Probe Settings");
            }

            Globals globals = settingsResponse.getGlobals();
            if (globals != null) {
                putString(R.string.prefs_grill_name, globals.getGrillName(),
                        "Globals Grill Name");
                putBoolean(R.string.prefs_admin_debug, globals.getDebugMode(),
                        "Globals Debug Mode");
                putBoolean(R.string.prefs_admin_boot_to_monitor, globals.getBootToMonitor(),
                        "Globals Boot to Monitor");
                putString(R.string.prefs_grill_units, globals.getUnits(),
                        "Globals Units");
                putBoolean(R.string.prefs_first_time_setup, globals.getFirstTimeSetup(),
                        "Globals First Time Setup");
                putBoolean(R.string.prefs_ext_data, globals.getExtData(),
                        "Globals Ext Data");
                putBoolean(R.string.prefs_pellet_prime_igniter, globals.getPrimeIgnition(),
                        "Globals Prime Ignition");
                putBoolean(R.string.prefs_venv, globals.getVenv(),
                        "Globals Venv");
                putFloatString(R.string.prefs_pellet_auger_rate, globals.getAugerRate(),
                        "Globals Auger Rate");
                if (!VersionUtils.isSupported(ServerVersions.V_180)) {
                    putBoolean(R.string.prefs_dc_fan, globals.getDCFan(),
                            "Globals DC Fan");
                    putBoolean(R.string.prefs_standalone, globals.getStandalone(),
                            "Globals Standalone");
                    putBoolean(R.string.prefs_real_hw, globals.getRealHw(),
                            "Globals Real HW");
                }
            } else {
                errors.add("Globals");
            }

            if (VersionUtils.isSupported(ServerVersions.V_180)) {
                Platform platform = settingsResponse.getPlatform();
                if (platform != null) {
                    putBoolean(R.string.prefs_dc_fan, platform.getDcFan(),
                            "Platform DC Fan");
                    putBoolean(R.string.prefs_standalone, platform.getStandalone(),
                            "Platform Standalone");
                    putBoolean(R.string.prefs_real_hw, platform.getRealHw(),
                            "Platform Real HW");
                    putString(R.string.prefs_platform_current, platform.getCurrent(),
                            "Platform Current");
                    putString(R.string.prefs_platform_type, platform.getSystemType(),
                            "Platform Type");
                } else {
                    errors.add("Platform");
                }
            }

            Ifttt ifttt = settingsResponse.getNotifyServices().getIfttt();
            if (ifttt != null) {
                putBoolean(R.string.prefs_notif_ifttt_enabled, ifttt.getEnabled(),
                        "Ifttt Enabled");
                putString(R.string.prefs_notif_ifttt_api, ifttt.getAPIKey(),
                        "Ifttt API Key");
            } else {
                errors.add("Ifttt");
            }

            PushBullet pushBullet = settingsResponse.getNotifyServices().getPushBullet();
            if (pushBullet != null) {
                putBoolean(R.string.prefs_notif_pushbullet_enabled, pushBullet.getEnabled(),
                        "PushBullet Enabled");
                putString(R.string.prefs_notif_pushbullet_api, pushBullet.getAPIKey(),
                        "PushBullet API Key");
                putString(R.string.prefs_notif_pushbullet_url, pushBullet.getPublicURL(),
                        "PushBullet URL");
            } else {
                errors.add("PushBullet");
            }

            Pushover pushOver = settingsResponse.getNotifyServices().getPushover();
            if (pushOver != null) {
                putBoolean(R.string.prefs_notif_pushover_enabled, pushOver.getEnabled(),
                        "Pushover Enabled");
                putString(R.string.prefs_notif_pushover_api, pushOver.getAPIKey(),
                        "Pushover API Key");
                putString(R.string.prefs_notif_pushover_keys, pushOver.getUserKeys(),
                        "Pushover User Keys");
                putString(R.string.prefs_notif_pushover_url, pushOver.getPublicURL(),
                        "Pushover URL");
            } else {
                errors.add("Pushover");
            }

            OneSignalPush oneSignal = settingsResponse.getNotifyServices().getOneSignal();
            if (oneSignal != null) {
                putBoolean(R.string.prefs_notif_onesignal_enabled, oneSignal.getEnabled(),
                        "Onesignal Enabled");
                putString(R.string.prefs_notif_onesignal_serveruuid, oneSignal.getServerUUID(),
                        "Onesignal Server UUID");
                if (oneSignal.getOneSignalDevices() != null) {
                    putString(R.string.prefs_notif_onesignal_device_list,
                            new Gson().toJson(oneSignal.getOneSignalDevices()),
                            "Onesignal Devices");
                }
            } else {
                errors.add("OneSignalPush");
            }

            InfluxDB influxDB = settingsResponse.getNotifyServices().getInfluxDB();
            if (influxDB != null) {
                putBoolean(R.string.prefs_notif_influxdb_enabled, influxDB.getEnabled(),
                        "InfluxDB Enabled");
                putString(R.string.prefs_notif_influxdb_url, influxDB.getUrl(),
                        "InfluxDB URL");
                putString(R.string.prefs_notif_influxdb_token, influxDB.getToken(),
                        "InfluxDB Token");
                putString(R.string.prefs_notif_influxdb_org, influxDB.getOrg(),
                        "InfluxDB Org");
                putString(R.string.prefs_notif_influxdb_bucket, influxDB.getBucket(),
                        "InfluxDB Bucket");
            } else {
                errors.add("InfluxDB");
            }

            Apprise apprise = settingsResponse.getNotifyServices().getApprise();
            if (apprise != null) {
                putBoolean(R.string.prefs_notif_apprise_enabled, apprise.getEnabled(),
                        "Apprise Enabled");
                putString(R.string.prefs_notif_apprise_locations,
                        new Gson().toJson(apprise.getLocations()),
                        "Apprise Locations");
            } else {
                errors.add("Apprise");
            }

            if (VersionUtils.isSupported(ServerVersions.V_180)) {
                Controller controller = settingsResponse.getController();
                if (controller != null) {
                    Pid pid = controller.getConfig().getPid();
                    PidAc pidAc = controller.getConfig().getPidAc();
                    PidSp pidSp = controller.getConfig().getPidSp();
                    putString(R.string.prefs_cntrlr_selected, controller.getSelected(),
                            "Controller Selected");
                    putDoubleString(R.string.prefs_pid_cntrlr_pb, pid.getPb(),
                            "Pid PB");
                    putDoubleString(R.string.prefs_pid_cntrlr_td, pid.getTd(),
                            "Pid Td");
                    putDoubleString(R.string.prefs_pid_cntrlr_ti, pid.getTi(),
                            "Pid Ti");
                    putDoubleString(R.string.prefs_pid_cntrlr_center, pid.getCenter(),
                            "Pid Center");
                    putDoubleString(R.string.prefs_pid_ac_cntrlr_pb, pidAc.getPb(),
                            "PidAc PB");
                    putDoubleString(R.string.prefs_pid_ac_cntrlr_td, pidAc.getTd(),
                            "PidAc Td");
                    putDoubleString(R.string.prefs_pid_ac_cntrlr_ti, pidAc.getTi(),
                            "PidAc Ti");
                    putDoubleString(R.string.prefs_pid_ac_cntrlr_center, pidAc.getCenterFactor(),
                            "PidAc Center Factor");
                    putIntString(R.string.prefs_pid_ac_cntrlr_sp, pidAc.getStableWindow(),
                            "PidAc Stable Window");
                    putDoubleString(R.string.prefs_pid_sp_cntrlr_pb, pidSp.getPb(),
                            "PidSp PB");
                    putDoubleString(R.string.prefs_pid_sp_cntrlr_td, pidSp.getTd(),
                            "PidSp Td");
                    putDoubleString(R.string.prefs_pid_sp_cntrlr_ti, pidSp.getTi(),
                            "PidSp Ti");
                    putDoubleString(R.string.prefs_pid_sp_cntrlr_center, pidSp.getCenterFactor(),
                            "PidSp Center Factor");
                    putIntString(R.string.prefs_pid_sp_cntrlr_sp, pidSp.getStableWindow(),
                            "PidSp Stable Window");
                    putIntString(R.string.prefs_pid_sp_cntrlr_tau, pidSp.getTau(),
                            "PidSp Tau");
                    putIntString(R.string.prefs_pid_sp_cntrlr_theta, pidSp.getTheta(),
                            "PidSp Theta");
                } else {
                    errors.add("Controller");
                }
            }

            CycleData cycleData = settingsResponse.getCycleData();
            if (cycleData != null) {
                putIntString(R.string.prefs_cycle_cntrlr_cycle, cycleData.getHoldCycleTime(),
                        "CycleData HoldCycleTime");
                putFloatString(R.string.prefs_cycle_cntrlr_u_max, cycleData.getuMax(),
                        "CycleData uMax");
                putFloatString(R.string.prefs_cycle_cntrlr_u_min, cycleData.getuMin(),
                        "CycleData uMin");
                putIntString(R.string.prefs_work_auger_on, cycleData.getSmokeOnCycleTime(),
                        "CycleData SmokeOnCycleTime");
                putIntString(R.string.prefs_work_auger_off, cycleData.getSmokeOffCycleTime(),
                        "CycleData SmokeOffCycleTime");
                putIntString(R.string.prefs_work_pmode_mode, cycleData.getPMode(),
                        "CycleData PMode");
                putBoolean(R.string.prefs_work_lid_open_detect, cycleData.getLidOpenDetectEnabled(),
                        "CycleData LidDetection");
                putIntString(R.string.prefs_work_lid_open_thresh, cycleData.getLidOpenThreshold(),
                        "CycleData LidOpenThreshold");
                putIntString(R.string.prefs_work_lid_open_pause, cycleData.getLidOpenPauseTime(),
                        "CycleData LidOpenPauseTme");
            } else {
                errors.add("CycleData");
            }

            KeepWarm keepWarm = settingsResponse.getKeepWarm();
            if (keepWarm != null) {
                putIntString(R.string.prefs_work_keep_warm_temp, keepWarm.getTemp(),
                        "KeepWarm Enabled");
                putBoolean(R.string.prefs_work_keep_warm_s_plus, keepWarm.getSPlus(),
                        "KeepWarm SPlus");
            } else {
                errors.add("KeepWarm");
            }

            SmokePlus smokePlus = settingsResponse.getSmokePlus();
            if (smokePlus != null) {
                putBoolean(R.string.prefs_work_splus_enabled, smokePlus.getEnabled(),
                        "SmokePlus Enabled");
                putIntString(R.string.prefs_work_splus_min, smokePlus.getMinTemp(),
                        "SmokePlus MinTemp");
                putIntString(R.string.prefs_work_splus_max, smokePlus.getMaxTemp(),
                        "SmokePlus MaxTemp");
                putIntString(R.string.prefs_work_splus_on_time, smokePlus.getOnTime(),
                        "SmokePlus OnTime");
                putIntString(R.string.prefs_work_splus_off_time, smokePlus.getOffTime(),
                        "SmokePlus OffTime");
                putIntString(R.string.prefs_work_splus_ramp_dc, smokePlus.getDutyCycle(),
                        "SmokePlus DutyCycle");
                putBoolean(R.string.prefs_work_splus_fan_ramp, smokePlus.getFanRamp(),
                        "SmokePlus FanRamp");
            } else {
                errors.add("SmokePlus");
            }

            PWM pwm = settingsResponse.getPWM();
            if (pwm != null) {
                putBoolean(R.string.prefs_pwm_fan_control, pwm.getPWMControl(),
                        "PWM Control");
                putIntString(R.string.prefs_pwm_fan_update_time, pwm.getUpdateTime(),
                        "PWM UpdateTime");
                putIntString(R.string.prefs_pwm_frequency, pwm.getFrequency(),
                        "PWM Frequency");
                putIntString(R.string.prefs_pwm_min_duty_cycle, pwm.getMinDutyCycle(),
                        "PWM MinDutyCycle");
                putIntString(R.string.prefs_pwm_max_duty_cycle, pwm.getMaxDutyCycle(),
                        "PWM MaxDutyCycle");
                putString(R.string.prefs_pwm_temp_range,
                        new Gson().toJson(pwm.getTempRangeList()),
                        "PWM Temp Range");
                putString(R.string.prefs_pwm_profiles,
                        new Gson().toJson(pwm.getProfiles()),
                        "PWM Profiles");
            } else {
                errors.add("PWM");
            }

            Safety safety = settingsResponse.getSafety();
            if (safety != null) {
                putBoolean(R.string.prefs_safety_startup_check, safety.getStartupCheck(),
                        "Safety Startup Check");
                putIntString(R.string.prefs_safety_min_start, safety.getMinStartupTemp(),
                        "Safety MinStartupTemp");
                putIntString(R.string.prefs_safety_max_start, safety.getMaxStartupTemp(),
                        "Safety MaxStartupTemp");
                putIntString(R.string.prefs_safety_max_temp, safety.getMaxTemp(),
                        "Safety MaxTemp");
                putIntString(R.string.prefs_safety_retries, safety.getReigniteRetries(),
                        "Safety ReigniteRetries");
            } else {
                errors.add("Safety");
            }

            Shutdown shutdown = settingsResponse.getShutdown();
            if (shutdown != null) {
                putIntString(R.string.prefs_shutdown_duration, shutdown.getDuration(),
                        "Shutdown Duration");
                putBoolean(R.string.prefs_auto_power_off, shutdown.getAutoPowerOff(),
                        "Shutdown AutoPowerOff");
            } else {
                errors.add("Shutdown");
            }

            Startup startup = settingsResponse.getStartup();
            if (startup != null) {
                putIntString(R.string.prefs_startup_duration, startup.getDuration(),
                        "Startup Duration");
                putIntString(R.string.prefs_prime_on_startup, startup.getPrimeOnStartup(),
                        "Startup Prime");
                putIntString(R.string.prefs_startup_exit_temp, startup.getStartExitTemp(),
                        "Startup ExitTemp");

                StartToMode startToMode = startup.getStartToMode();
                if (startToMode != null) {
                    putString(R.string.prefs_startup_goto_mode, startToMode.getAfterStartUpMode(),
                            "StartToMode AfterMode");
                    putIntString(R.string.prefs_startup_goto_temp, startToMode.getPrimarySetPoint(),
                            "StartToMode SetPoint");
                } else {
                    errors.add("StartToMode");
                }

                SmartStart smartStart = startup.getSmartStart();
                if (smartStart != null) {
                    putBoolean(R.string.prefs_smart_start_enabled, smartStart.getEnabled(),
                            "SmartStart Enabled");
                    putIntString(R.string.prefs_smart_start_exit_temp, smartStart.getExitTemp(),
                            "SmartStart ExitTemp");
                    putString(R.string.prefs_smart_start_temp_range,
                            new Gson().toJson(smartStart.getTempRangeList()),
                            "SmartStart RangeList");
                    putString(R.string.prefs_smart_start_profiles,
                            new Gson().toJson(smartStart.getProfiles()),
                            "SmartStart Profiles");
                } else {
                    errors.add("SmartStart");
                }
            } else {
                errors.add("Startup");
            }

            PelletLevel pellets = settingsResponse.getPellets();
            if (pellets != null) {
                putIntString(R.string.prefs_pellet_empty, pellets.getEmpty(),
                        "PelletLevel Empty");
                putIntString(R.string.prefs_pellet_full, pellets.getFull(),
                        "PelletLevel Full");
                putBoolean(R.string.prefs_pellet_warning_enabled, pellets.getWarningEnabled(),
                        "PelletLevel Warning Enabled");
                putIntString(R.string.prefs_pellet_warning_level, pellets.getWarningLevel(),
                        "PelletLevel Warning Level");
                putIntString(R.string.prefs_pellet_warning_time, pellets.getWarningTime(),
                        "PelletLevel Warning Time");
            } else {
                errors.add("PelletLevel");
            }

            Modules modules = settingsResponse.getModules();
            if (modules != null) {
                putString(R.string.prefs_modules_display, modules.getDisplay(),
                        "Modules Display");
                putString(R.string.prefs_modules_distance, modules.getDist(),
                        "Modules Dist");
                putString(R.string.prefs_modules_platform, modules.getGrillPlat(),
                        "Modules GrillPlat");
            } else {
                errors.add("Modules");
            }

        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            try {
                ServerResponseModel responseModel = ServerResponseModel.parseJSON(response);
                String result = responseModel.getResult();
                String message = responseModel.getMessage();
                if (result != null && result.equalsIgnoreCase("error")) {
                    if (message != null && !message.isBlank()) {
                        errors.add(message);
                    } else {
                        errors.add("JsonSyntaxException");
                    }
                }
            } catch (JsonSyntaxException jse) {
                Timber.e(e, "ServerResponseModel JSON Error: %s", response);
                errors.add("JsonSyntaxException");
            }
        }
        return errors;
    }

    private void putString(int key, String value, String item) {
        if (value != null) {
            if (!Prefs.getString(context.getString(key)).equals(value)) {
                Prefs.putString(context.getString(key), value);
            }
        } else {
            if (!errors.contains(item)) {
                errors.add(item);
            }
        }
    }

    private void putBoolean(int key, Boolean value, String item) {
        if (value != null) {
            if (Prefs.getBoolean(context.getString(key)) != value) {
                Prefs.putBoolean(context.getString(key), value);
            }
        } else {
            if (!errors.contains(item)) {
                errors.add(item);
            }
        }
    }

    private void putIntString(int key, Integer value, String item) {
        if (value != null) {
            if (!Prefs.getString(context.getString(key)).equals(String.valueOf(value))) {
                Prefs.putString(context.getString(key), String.valueOf(value));
            }
        } else {
            if (!errors.contains(item)) {
                errors.add(item);
            }
        }
    }

    private void putDoubleString(int key, Double value, String item) {
        if (value != null) {
            if (!Prefs.getString(context.getString(key)).equals(String.valueOf(value))) {
                Prefs.putString(context.getString(key), String.valueOf(value));
            }
        } else {
            if (!errors.contains(item)) {
                errors.add(item);
            }
        }
    }

    private void putFloatString(int key, Float value, String item) {
        if (value != null) {
            if (!Prefs.getString(context.getString(key)).equals(String.valueOf(value))) {
                Prefs.putString(context.getString(key), String.valueOf(value));
            }
        } else {
            if (!errors.contains(item)) {
                errors.add(item);
            }
        }
    }
}
