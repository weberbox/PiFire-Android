package com.weberbox.pifire.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.ServerVersions;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.interfaces.SettingsSocketCallback;
import com.weberbox.pifire.model.remote.SettingsDataModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.*;

import java.util.List;
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
                boolean result = updateSettingsData(response);
                if (callback != null) {
                    callback.onSettingsResult(result);
                }
            });
        }
    }

    public boolean updateSettingsData(String responseData) {
        try {

            SettingsDataModel settingsResponse = SettingsDataModel.parseJSON(responseData);

            HistoryPage historyPage = settingsResponse.getHistoryPage();
            ProbeSettings probesSettings = settingsResponse.getProbeSettings();
            Globals globals = settingsResponse.getGlobals();
            Versions versions = settingsResponse.getVersions();
            Ifttt ifttt = settingsResponse.getIfttt();
            PushBullet pushBullet = settingsResponse.getPushBullet();
            Pushover pushOver = settingsResponse.getPushover();
            OneSignalPush oneSignal = settingsResponse.getOneSignal();
            InfluxDB influxDB = settingsResponse.getInfluxDB();
            ProbeTypes probeTypes = settingsResponse.getProbeTypes();
            CycleData cycleData = settingsResponse.getCycleData();
            KeepWarm keepWarm = settingsResponse.getKeepWarm();
            SmokePlus smokePlus = settingsResponse.getSmokePlus();
            PWM pwm = settingsResponse.getPWM();
            Safety safety = settingsResponse.getSafety();
            PelletLevel pellets = settingsResponse.getPellets();
            Modules modules = settingsResponse.getModules();
            SmartStart smartStart = settingsResponse.getSmartStart();

            if (probesSettings != null) {
                Map<String, ProbeProfileModel> probes = probesSettings.getProbeProfiles();
                putString(R.string.prefs_probe_profiles, new Gson().toJson(probes));

                if (VersionUtils.isSupported(ServerVersions.V_135)) {
                    List<String> probeOptions = probesSettings.getProbeOptions();
                    putString(R.string.prefs_adc_probe_options, new Gson().toJson(probeOptions));
                    List<String> probeSources = probesSettings.getProbeSources();
                    putString(R.string.prefs_adc_probe_sources, new Gson().toJson(probeSources));
                }
            }

            if (versions != null) {
                putString(R.string.prefs_server_version, versions.getServerVersion());
            }

            if (historyPage != null) {
                putIntString(R.string.prefs_history_display, historyPage.getMinutes());
                putBoolean(R.string.prefs_history_clear, historyPage.getClearHistoryOnStart());
                putBoolean(R.string.prefs_history_auto, historyPage.getAutoRefresh().equals("on"));
                putIntString(R.string.prefs_history_points, historyPage.getDataPoints());
            }

            if (globals != null) {
                putString(R.string.prefs_grill_name, globals.getGrillName());
                putBoolean(R.string.prefs_admin_debug, globals.getDebugMode());
                putIntString(R.string.prefs_shutdown_time, globals.getShutdownTimer());

                if (globals.getStartUpTimer() != null) {
                    putIntString(R.string.prefs_startup_time, globals.getStartUpTimer());
                }

                if (globals.getAutoPowerOff() != null) {
                    putBoolean(R.string.prefs_auto_power_off, globals.getAutoPowerOff());
                }

                if (globals.getUnits() != null) {
                    putString(R.string.prefs_grill_units, globals.getUnits());
                }

                if (globals.getFourProbes() != null) {
                    putBoolean(R.string.prefs_four_probe, globals.getFourProbes());
                }

                if (globals.getFourProbes() != null && globals.getFourProbes()) {
                    SettingsDataModel.GrillProbeSettings grillProbeSettings =
                            settingsResponse.getGrillProbeSettings();

                    Map<String, GrillProbeModel> grillProbes = grillProbeSettings.getGrillProbes();

                    putString(R.string.prefs_grill_probes, new Gson().toJson(grillProbes));
                    putString(R.string.prefs_grill_probe, grillProbeSettings.getGrillProbe());

                    putString(R.string.prefs_grill_probe_one_type, probeTypes.getGrill1type());
                    putString(R.string.prefs_grill_probe_two_type, probeTypes.getGrill2type());
                } else {
                    if (VersionUtils.isSupported(ServerVersions.V_129)) {
                        putString(R.string.prefs_grill_probe_type, probeTypes.getGrill1type());
                    } else {
                        putString(R.string.prefs_grill_probe_type, probeTypes.getGrill0type());
                    }
                }

                if (globals.getDCFan() != null) {
                    putBoolean(R.string.prefs_dc_fan, globals.getDCFan());
                } else {
                    if (Prefs.getBoolean(context.getString(R.string.prefs_dc_fan))) {
                        putBoolean(R.string.prefs_dc_fan, false);
                    }
                }

                if (globals.getStandalone() != null) {
                    putBoolean(R.string.prefs_standalone, globals.getStandalone());
                }

            }

            if (ifttt != null) {
                putBoolean(R.string.prefs_notif_ifttt_enabled, ifttt.getEnabled());
                putString(R.string.prefs_notif_ifttt_api, ifttt.getAPIKey());
            }

            if (pushBullet != null) {
                putBoolean(R.string.prefs_notif_pushbullet_enabled, pushBullet.getEnabled());
                putString(R.string.prefs_notif_pushbullet_api, pushBullet.getAPIKey());
                putString(R.string.prefs_notif_pushbullet_url, pushBullet.getPublicURL());
            }

            if (pushOver != null) {
                putBoolean(R.string.prefs_notif_pushover_enabled, pushOver.getEnabled());
                putString(R.string.prefs_notif_pushover_api, pushOver.getAPIKey());
                putString(R.string.prefs_notif_pushover_keys, pushOver.getUserKeys());
                putString(R.string.prefs_notif_pushover_url, pushOver.getPublicURL());
            }

            if (oneSignal != null) {
                putBoolean(R.string.prefs_notif_onesignal_enabled, oneSignal.getEnabled());
                putString(R.string.prefs_notif_onesignal_serveruuid, oneSignal.getServerUUID());

                if (oneSignal.getOneSignalDevices() != null) {
                    putString(R.string.prefs_notif_onesignal_device_list,
                            new Gson().toJson(oneSignal.getOneSignalDevices()));
                }
            }

            if (influxDB != null) {
                putBoolean(R.string.prefs_notif_influxdb_enabled, influxDB.getEnabled());
                putString(R.string.prefs_notif_influxdb_url, influxDB.getUrl());
                putString(R.string.prefs_notif_influxdb_token, influxDB.getToken());
                putString(R.string.prefs_notif_influxdb_org, influxDB.getOrg());
                putString(R.string.prefs_notif_influxdb_bucket, influxDB.getBucket());
            }

            if (probeTypes != null) {
                putString(R.string.prefs_probe_one_type, probeTypes.getProbe1type());
                putString(R.string.prefs_probe_two_type, probeTypes.getProbe2type());
            }

            if (cycleData != null) {
                putFloatString(R.string.prefs_work_pid_pb, cycleData.getPb());
                putFloatString(R.string.prefs_work_pid_ti, cycleData.getTi());
                putFloatString(R.string.prefs_work_pid_td, cycleData.getTd());
                putIntString(R.string.prefs_work_pid_cycle, cycleData.getHoldCycleTime());
                putIntString(R.string.prefs_work_auger_on, cycleData.getSmokeCycleTime());
                putIntString(R.string.prefs_work_pmode_mode, cycleData.getPMode());
                putFloatString(R.string.prefs_work_pid_u_max, cycleData.getuMax());
                putFloatString(R.string.prefs_work_pid_u_min, cycleData.getuMin());
                putFloatString(R.string.prefs_work_pid_center, cycleData.getCenter());
            }

            if (keepWarm != null) {
                putIntString(R.string.prefs_work_keep_warm_temp, keepWarm.getTemp());
                putBoolean(R.string.prefs_work_keep_warm_s_plus, keepWarm.getSPlus());
            }

            if (pellets != null) {
                putIntString(R.string.prefs_pellet_empty, pellets.getEmpty());
                putIntString(R.string.prefs_pellet_full, pellets.getFull());
                putBoolean(R.string.prefs_pellet_warning_enabled, pellets.getWarningEnabled());
                putIntString(R.string.prefs_pellet_warning_level, pellets.getWarningLevel());
                if (VersionUtils.isSupported(ServerVersions.V_135)) {
                    putIntString(R.string.prefs_pellet_warning_time, pellets.getWarningTime());
                }
            }

            if (smokePlus != null) {
                putBoolean(R.string.prefs_work_splus_enabled, smokePlus.getEnabled());
                putIntString(R.string.prefs_work_splus_min, smokePlus.getMinTemp());
                putIntString(R.string.prefs_work_splus_max, smokePlus.getMaxTemp());
                putIntString(R.string.prefs_work_splus_fan, smokePlus.getCycle());
                if (VersionUtils.isSupported(ServerVersions.V_135)) {
                    putIntString(R.string.prefs_work_splus_on_time, smokePlus.getOnTime());
                    putIntString(R.string.prefs_work_splus_off_time, smokePlus.getOffTime());
                    putIntString(R.string.prefs_work_splus_ramp_dc, smokePlus.getDutyCycle());
                    putBoolean(R.string.prefs_work_splus_fan_ramp, smokePlus.getFanRamp());
                }
            }

            if (safety != null) {
                putIntString(R.string.prefs_safety_min_start, safety.getMinStartupTemp());
                putIntString(R.string.prefs_safety_max_start, safety.getMaxStartupTemp());
                putIntString(R.string.prefs_safety_max_temp, safety.getMaxTemp());
                putIntString(R.string.prefs_safety_retries, safety.getReigniteRetries());
            }

            if (modules != null) {
                putString(R.string.prefs_modules_adc, modules.getAdc());
                putString(R.string.prefs_modules_display, modules.getDisplay());
                putString(R.string.prefs_modules_distance, modules.getDist());
                putString(R.string.prefs_modules_platform, modules.getGrillPlat());
            }

            if (smartStart != null) {
                putBoolean(R.string.prefs_smart_start_enabled, smartStart.getEnabled());
                putString(R.string.prefs_smart_start_temp_range,
                        new Gson().toJson(smartStart.getTempRangeList()));
                putString(R.string.prefs_smart_start_profiles,
                        new Gson().toJson(smartStart.getProfiles()));
            }

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
            Timber.e(e, "Settings JSON Error");
            return false;
        }
        return true;
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
