package com.weberbox.pifire.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.control.ServerControl;
import com.weberbox.pifire.interfaces.SettingsCallback;
import com.weberbox.pifire.model.remote.SettingsDataModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.CycleData;
import com.weberbox.pifire.model.remote.SettingsDataModel.Globals;
import com.weberbox.pifire.model.remote.SettingsDataModel.GrillProbeModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.HistoryPage;
import com.weberbox.pifire.model.remote.SettingsDataModel.Ifttt;
import com.weberbox.pifire.model.remote.SettingsDataModel.InfluxDB;
import com.weberbox.pifire.model.remote.SettingsDataModel.OneSignalPush;
import com.weberbox.pifire.model.remote.SettingsDataModel.PelletLevel;
import com.weberbox.pifire.model.remote.SettingsDataModel.ProbeProfileModel;
import com.weberbox.pifire.model.remote.SettingsDataModel.ProbeSettings;
import com.weberbox.pifire.model.remote.SettingsDataModel.ProbeTypes;
import com.weberbox.pifire.model.remote.SettingsDataModel.PushBullet;
import com.weberbox.pifire.model.remote.SettingsDataModel.Pushover;
import com.weberbox.pifire.model.remote.SettingsDataModel.Safety;
import com.weberbox.pifire.model.remote.SettingsDataModel.SmokePlus;
import com.weberbox.pifire.model.remote.SettingsDataModel.Versions;

import java.util.Map;

import io.socket.client.Socket;
import timber.log.Timber;

public class SettingsUtils {

    private final Context context;
    private final SettingsCallback callback;

    public SettingsUtils(Context context, SettingsCallback callback) {
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
            SmokePlus smokePlus = settingsResponse.getSmokePlus();
            Safety safety = settingsResponse.getSafety();
            PelletLevel pellets = settingsResponse.getPellets();

            if (probesSettings != null) {
                Map<String, ProbeProfileModel> probes = probesSettings.getProbeProfiles();
                putString(R.string.prefs_probe_profiles, new Gson().toJson(probes));
            }

            if (versions != null) {
                putString(R.string.prefs_server_version, versions.getServerVersion());
            }

            if (historyPage != null) {
                putIntegerString(R.string.prefs_history_display, historyPage.getMinutes());
                putBoolean(R.string.prefs_history_clear, historyPage.getClearHistoryOnStart());
                putBoolean(R.string.prefs_history_auto, historyPage.getAutoRefresh().equals("on"));
                putIntegerString(R.string.prefs_history_points, historyPage.getDataPoints());
            }

            if (globals != null) {
                putString(R.string.prefs_grill_name, globals.getGrillName());
                putBoolean(R.string.prefs_admin_debug, globals.getDebugMode());
                putString(R.string.prefs_shutdown_time, String.valueOf(globals.getShutdownTimer()));

                if (globals.getStartUpTimer() != null) {
                    putIntegerString(R.string.prefs_startup_time, globals.getStartUpTimer());
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
                    putString(R.string.prefs_grill_probe_type, probeTypes.getGrill0type());
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
                putIntegerString(R.string.prefs_work_pid_cycle, cycleData.getHoldCycleTime());
                putIntegerString(R.string.prefs_work_auger_on, cycleData.getSmokeCycleTime());
                putIntegerString(R.string.prefs_work_pmode_mode, cycleData.getPMode());
                putFloatString(R.string.prefs_work_pid_u_max, cycleData.getuMax());
                putFloatString(R.string.prefs_work_pid_u_min, cycleData.getuMin());
                putFloatString(R.string.prefs_work_pid_center, cycleData.getCenter());
            }

            if (pellets != null) {
                putIntegerString(R.string.prefs_pellet_empty, pellets.getEmpty());
                putIntegerString(R.string.prefs_pellet_full, pellets.getFull());
                putBoolean(R.string.prefs_pellet_warning_enabled, pellets.getWarningEnabled());
                putIntegerString(R.string.prefs_pellet_warning_level, pellets.getWarningLevel());
            }

            if (smokePlus != null) {
                putBoolean(R.string.prefs_work_splus_enabled, smokePlus.getEnabled());
                putIntegerString(R.string.prefs_work_splus_min, smokePlus.getMinTemp());
                putIntegerString(R.string.prefs_work_splus_max, smokePlus.getMaxTemp());
                putIntegerString(R.string.prefs_work_splus_fan, smokePlus.getCycle());
            }

            if (safety != null) {
                putIntegerString(R.string.prefs_safety_min_start, safety.getMinStartupTemp());
                putIntegerString(R.string.prefs_safety_max_start, safety.getMaxStartupTemp());
                putIntegerString(R.string.prefs_safety_max_temp, safety.getMaxTemp());
                putIntegerString(R.string.prefs_safety_retries, safety.getReigniteRetries());
            }

        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.w(e, "Settings JSON Error");
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

    private void putIntegerString(int key, Integer value) {
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
