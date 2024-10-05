package com.weberbox.pifire.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.ServerVersions;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.enums.SettingsResult;
import com.weberbox.pifire.interfaces.SettingsSocketCallback;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeMap;
import com.weberbox.pifire.model.remote.ProbeDataModel.ProbeProfileModel;
import com.weberbox.pifire.model.remote.SettingsDataModel;
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

import java.util.Map;

import io.socket.client.Socket;
import timber.log.Timber;

public class SettingsUtils {

    private final Context context;
    private final SettingsSocketCallback callback;

    public SettingsUtils(Context context, SettingsSocketCallback callback) {
        this.context = context;
        this.callback = callback;
    }

    public void requestSettingsData(Socket socket) {
        if (socket != null && socket.connected()) {
            ServerControl.settingsGetEmit(socket, response -> {
                SettingsResult result = updateSettingsData(response);
                if (callback != null) {
                    callback.onSettingsResult(result);
                }
            });
        }
    }

    public SettingsResult updateSettingsData(String response) {
        SettingsResult result = SettingsResult.SUCCESS;
        try {
            SettingsDataModel settingsResponse = SettingsDataModel.parseJSON(response);

            try {
                Versions versions = settingsResponse.getVersions();
                if (versions != null) {
                    putString(R.string.prefs_server_version, versions.getServerVersion());
                    putString(R.string.prefs_recipe_version, versions.getRecipeVersion());
                    putString(R.string.prefs_cook_file_version, versions.getCookFileVersion());
                    putString(R.string.prefs_server_build, versions.getServerBuild());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "Versions JSON Error");
                result = SettingsResult.VERSIONS;
            }

            try {
                ProbeSettings probesSettings = settingsResponse.getProbeSettings();
                if (probesSettings != null) {
                    Map<String, ProbeProfileModel> probeProfiles = probesSettings.getProbeProfiles();
                    putString(R.string.prefs_probe_profiles, new Gson().toJson(probeProfiles));

                    ProbeMap probeMap = probesSettings.getProbeMap();
                    putString(R.string.prefs_probe_map, new Gson().toJson(probeMap));
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "ProbeSettings JSON Error");
                result = SettingsResult.PROBES;
            }

            try {
                Globals globals = settingsResponse.getGlobals();
                if (globals != null) {
                    putString(R.string.prefs_grill_name, globals.getGrillName());
                    putBoolean(R.string.prefs_admin_debug, globals.getDebugMode());
                    putBoolean(R.string.prefs_admin_boot_to_monitor, globals.getBootToMonitor());
                    putString(R.string.prefs_grill_units, globals.getUnits());
                    putBoolean(R.string.prefs_first_time_setup, globals.getFirstTimeSetup());
                    putBoolean(R.string.prefs_ext_data, globals.getExtData());
                    putBoolean(R.string.prefs_pellet_prime_igniter, globals.getPrimeIgnition());
                    putBoolean(R.string.prefs_venv, globals.getVenv());
                    putFloatString(R.string.prefs_pellet_auger_rate, globals.getAugerRate());
                    if (!VersionUtils.isSupported(ServerVersions.V_180)) {
                        putBoolean(R.string.prefs_dc_fan, globals.getDCFan());
                        putBoolean(R.string.prefs_standalone, globals.getStandalone());
                        putBoolean(R.string.prefs_real_hw, globals.getRealHw());
                    }
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "Globals JSON Error");
                result = SettingsResult.GLOBALS;
            }

            if (VersionUtils.isSupported(ServerVersions.V_180)) {
                try {
                    Platform platform = settingsResponse.getPlatform();
                    if (platform != null) {
                        putBoolean(R.string.prefs_dc_fan, platform.getDcFan());
                        putBoolean(R.string.prefs_standalone, platform.getStandalone());
                        putBoolean(R.string.prefs_real_hw, platform.getRealHw());
                    }
                } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                    Timber.e(e, "Platform JSON Error");
                    result = SettingsResult.PLATFORM;
                }
            }

            try {
                Ifttt ifttt = settingsResponse.getNotifyServices().getIfttt();
                if (ifttt != null) {
                    putBoolean(R.string.prefs_notif_ifttt_enabled, ifttt.getEnabled());
                    putString(R.string.prefs_notif_ifttt_api, ifttt.getAPIKey());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "Ifttt JSON Error");
                result = SettingsResult.IFTTT;
            }

            try {
                PushBullet pushBullet = settingsResponse.getNotifyServices().getPushBullet();
                if (pushBullet != null) {
                    putBoolean(R.string.prefs_notif_pushbullet_enabled, pushBullet.getEnabled());
                    putString(R.string.prefs_notif_pushbullet_api, pushBullet.getAPIKey());
                    putString(R.string.prefs_notif_pushbullet_url, pushBullet.getPublicURL());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "PushBullet JSON Error");
                result = SettingsResult.PUSHBULLET;
            }

            try {
                Pushover pushOver = settingsResponse.getNotifyServices().getPushover();
                if (pushOver != null) {
                    putBoolean(R.string.prefs_notif_pushover_enabled, pushOver.getEnabled());
                    putString(R.string.prefs_notif_pushover_api, pushOver.getAPIKey());
                    putString(R.string.prefs_notif_pushover_keys, pushOver.getUserKeys());
                    putString(R.string.prefs_notif_pushover_url, pushOver.getPublicURL());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "Pushover JSON Error");
                result = SettingsResult.PUSHOVER;
            }

            try {
                OneSignalPush oneSignal = settingsResponse.getNotifyServices().getOneSignal();
                if (oneSignal != null) {
                    putBoolean(R.string.prefs_notif_onesignal_enabled, oneSignal.getEnabled());
                    putString(R.string.prefs_notif_onesignal_serveruuid, oneSignal.getServerUUID());
                    if (oneSignal.getOneSignalDevices() != null) {
                        putString(R.string.prefs_notif_onesignal_device_list,
                                new Gson().toJson(oneSignal.getOneSignalDevices()));
                    }
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "OneSignalPush JSON Error");
                result = SettingsResult.ONESIGNAL;
            }

            try {
                InfluxDB influxDB = settingsResponse.getNotifyServices().getInfluxDB();
                if (influxDB != null) {
                    putBoolean(R.string.prefs_notif_influxdb_enabled, influxDB.getEnabled());
                    putString(R.string.prefs_notif_influxdb_url, influxDB.getUrl());
                    putString(R.string.prefs_notif_influxdb_token, influxDB.getToken());
                    putString(R.string.prefs_notif_influxdb_org, influxDB.getOrg());
                    putString(R.string.prefs_notif_influxdb_bucket, influxDB.getBucket());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "InfluxDB JSON Error");
                result = SettingsResult.INFLUXDB;
            }

            try {
                Apprise apprise = settingsResponse.getNotifyServices().getApprise();
                if (apprise != null) {
                    putBoolean(R.string.prefs_notif_apprise_enabled, apprise.getEnabled());
                    putString(R.string.prefs_notif_apprise_locations,
                            new Gson().toJson(apprise.getLocations()));
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "Apprise JSON Error");
                result = SettingsResult.APPRISE;
            }

            try {
                CycleData cycleData = settingsResponse.getCycleData();
                if (cycleData != null) {
                    putIntString(R.string.prefs_work_controller_cycle, cycleData.getHoldCycleTime());
                    putIntString(R.string.prefs_work_auger_on, cycleData.getSmokeOnCycleTime());
                    putIntString(R.string.prefs_work_auger_off, cycleData.getSmokeOffCycleTime());
                    putIntString(R.string.prefs_work_pmode_mode, cycleData.getPMode());
                    putFloatString(R.string.prefs_work_controller_u_max, cycleData.getuMax());
                    putFloatString(R.string.prefs_work_controller_u_min, cycleData.getuMin());
                    putBoolean(R.string.prefs_work_lid_open_detect, cycleData.getLidOpenDetectEnabled());
                    putIntString(R.string.prefs_work_lid_open_thresh, cycleData.getLidOpenThreshold());
                    putIntString(R.string.prefs_work_lid_open_pause, cycleData.getLidOpenPauseTime());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "CycleData JSON Error");
                result = SettingsResult.CYCLE_DATA;
            }

            try {
                KeepWarm keepWarm = settingsResponse.getKeepWarm();
                if (keepWarm != null) {
                    putIntString(R.string.prefs_work_keep_warm_temp, keepWarm.getTemp());
                    putBoolean(R.string.prefs_work_keep_warm_s_plus, keepWarm.getSPlus());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "KeepWarm JSON Error");
                result = SettingsResult.KEEP_WARM;
            }

            try {
                SmokePlus smokePlus = settingsResponse.getSmokePlus();
                if (smokePlus != null) {
                    putBoolean(R.string.prefs_work_splus_enabled, smokePlus.getEnabled());
                    putIntString(R.string.prefs_work_splus_min, smokePlus.getMinTemp());
                    putIntString(R.string.prefs_work_splus_max, smokePlus.getMaxTemp());
                    putIntString(R.string.prefs_work_splus_fan, smokePlus.getCycle());
                    putIntString(R.string.prefs_work_splus_on_time, smokePlus.getOnTime());
                    putIntString(R.string.prefs_work_splus_off_time, smokePlus.getOffTime());
                    putIntString(R.string.prefs_work_splus_ramp_dc, smokePlus.getDutyCycle());
                    putBoolean(R.string.prefs_work_splus_fan_ramp, smokePlus.getFanRamp());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "SmokePlus JSON Error");
                result = SettingsResult.SMOKE_PLUS;
            }

            try {
                PWM pwm = settingsResponse.getPWM();
                if (pwm != null) {
                    putBoolean(R.string.prefs_pwm_fan_control, pwm.getPWMControl());
                    putIntString(R.string.prefs_pwm_fan_update_time, pwm.getUpdateTime());
                    putIntString(R.string.prefs_pwm_frequency, pwm.getFrequency());
                    putIntString(R.string.prefs_pwm_min_duty_cycle, pwm.getMinDutyCycle());
                    putIntString(R.string.prefs_pwm_max_duty_cycle, pwm.getMaxDutyCycle());
                    putString(R.string.prefs_pwm_temp_range,
                            new Gson().toJson(pwm.getTempRangeList()));
                    putString(R.string.prefs_pwm_profiles,
                            new Gson().toJson(pwm.getProfiles()));
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "PWM JSON Error");
                result = SettingsResult.PWM;
            }

            try {
                Safety safety = settingsResponse.getSafety();
                if (safety != null) {
                    putBoolean(R.string.prefs_safety_startup_check, safety.getStartupCheck());
                    putIntString(R.string.prefs_safety_min_start, safety.getMinStartupTemp());
                    putIntString(R.string.prefs_safety_max_start, safety.getMaxStartupTemp());
                    putIntString(R.string.prefs_safety_max_temp, safety.getMaxTemp());
                    putIntString(R.string.prefs_safety_retries, safety.getReigniteRetries());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "Safety JSON Error");
                result = SettingsResult.SAFETY;
            }

            try {
                Shutdown shutdown = settingsResponse.getShutdown();
                if (shutdown != null) {
                    putIntString(R.string.prefs_shutdown_duration, shutdown.getDuration());
                    putBoolean(R.string.prefs_auto_power_off, shutdown.getAutoPowerOff());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "Shutdown JSON Error");
                result = SettingsResult.SHUTDOWN;
            }

            try {
                Startup startup = settingsResponse.getStartup();
                if (startup != null) {
                    putIntString(R.string.prefs_startup_duration, startup.getDuration());
                    putIntString(R.string.prefs_prime_on_startup, startup.getPrimeOnStartup());
                    putIntString(R.string.prefs_startup_exit_temp, startup.getStartExitTemp());

                    StartToMode startToMode = startup.getStartToMode();
                    if (startToMode != null) {
                        putString(R.string.prefs_startup_goto_mode, startToMode.getAfterStartUpMode());
                        putIntString(R.string.prefs_startup_goto_temp, startToMode.getPrimarySetPoint());
                    }

                    SmartStart smartStart = startup.getSmartStart();
                    if (smartStart != null) {
                        putBoolean(R.string.prefs_smart_start_enabled, smartStart.getEnabled());
                        putIntString(R.string.prefs_smart_start_exit_temp, smartStart.getExitTemp());
                        putString(R.string.prefs_smart_start_temp_range,
                                new Gson().toJson(smartStart.getTempRangeList()));
                        putString(R.string.prefs_smart_start_profiles,
                                new Gson().toJson(smartStart.getProfiles()));
                    }
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "Startup JSON Error");
                result = SettingsResult.STARTUP;
            }

            try {
                PelletLevel pellets = settingsResponse.getPellets();
                if (pellets != null) {
                    putIntString(R.string.prefs_pellet_empty, pellets.getEmpty());
                    putIntString(R.string.prefs_pellet_full, pellets.getFull());
                    putBoolean(R.string.prefs_pellet_warning_enabled, pellets.getWarningEnabled());
                    putIntString(R.string.prefs_pellet_warning_level, pellets.getWarningLevel());
                    putIntString(R.string.prefs_pellet_warning_time, pellets.getWarningTime());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "PelletLevel JSON Error");
                result = SettingsResult.PELLET_LEVEL;
            }

            try {
                Modules modules = settingsResponse.getModules();
                if (modules != null) {
                    putString(R.string.prefs_modules_display, modules.getDisplay());
                    putString(R.string.prefs_modules_distance, modules.getDist());
                    putString(R.string.prefs_modules_platform, modules.getGrillPlat());
                }
            } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
                Timber.e(e, "Modules JSON Error");
                result = SettingsResult.MODULES;
            }

        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.e(e, "Settings JSON Error");
            result = SettingsResult.GENERAL;
        }
        return result;
    }

    private void putString(int key, String value) {
        if (value != null) {
            if (!Prefs.getString(context.getString(key)).equals(value)) {
                Prefs.putString(context.getString(key), value);
            }
        }
    }

    private void putBoolean(int key, Boolean value) {
        if (value != null) {
            if (Prefs.getBoolean(context.getString(key)) != value) {
                Prefs.putBoolean(context.getString(key), value);
            }
        }
    }

    @SuppressWarnings("unused")
    private void putInt(int key, Integer value) {
        if (value != null) {
            if (Prefs.getInt(context.getString(key)) != value) {
                Prefs.putInt(context.getString(key), value);
            }
        }
    }

    private void putIntString(int key, Integer value) {
        if (value != null) {
            if (!Prefs.getString(context.getString(key)).equals(String.valueOf(value))) {
                Prefs.putString(context.getString(key), String.valueOf(value));
            }
        }
    }

    private void putFloatString(int key, Float value) {
        if (value != null) {
            if (!Prefs.getString(context.getString(key)).equals(String.valueOf(value))) {
                Prefs.putString(context.getString(key), String.valueOf(value));
            }
        }
    }
}
