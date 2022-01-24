package com.weberbox.pifire.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pixplicity.easyprefs.library.Prefs;
import com.weberbox.pifire.R;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.interfaces.SettingsCallback;
import com.weberbox.pifire.model.remote.GrillProbeModel;
import com.weberbox.pifire.model.remote.ProbeProfileModel;
import com.weberbox.pifire.model.remote.SettingsResponseModel;
import com.weberbox.pifire.model.remote.SettingsResponseModel.CycleData;
import com.weberbox.pifire.model.remote.SettingsResponseModel.Globals;
import com.weberbox.pifire.model.remote.SettingsResponseModel.HistoryPage;
import com.weberbox.pifire.model.remote.SettingsResponseModel.Ifttt;
import com.weberbox.pifire.model.remote.SettingsResponseModel.InfluxDB;
import com.weberbox.pifire.model.remote.SettingsResponseModel.OneSignalPush;
import com.weberbox.pifire.model.remote.SettingsResponseModel.PelletLevel;
import com.weberbox.pifire.model.remote.SettingsResponseModel.ProbeSettings;
import com.weberbox.pifire.model.remote.SettingsResponseModel.ProbeTypes;
import com.weberbox.pifire.model.remote.SettingsResponseModel.PushBullet;
import com.weberbox.pifire.model.remote.SettingsResponseModel.Pushover;
import com.weberbox.pifire.model.remote.SettingsResponseModel.Safety;
import com.weberbox.pifire.model.remote.SettingsResponseModel.SmokePlus;
import com.weberbox.pifire.model.remote.SettingsResponseModel.Versions;

import java.util.Map;

import io.socket.client.Ack;
import io.socket.client.Socket;
import timber.log.Timber;

public class SettingsUtils {

    private final Context mContext;
    private final SettingsCallback mCallback;

    public SettingsUtils(Context context, SettingsCallback callback) {
        mContext = context;
        mCallback = callback;
    }

    public void requestSettingsData(Socket socket) {
        if (socket != null && socket.connected()) {
            socket.emit(ServerConstants.REQUEST_SETTINGS_DATA, (Ack) args ->
                    mCallback.onSettingsResult(updateSettingsData(args[0].toString())));
        }
    }

    private boolean updateSettingsData(String response_data) {
        try {

            SettingsResponseModel settingsResponse = SettingsResponseModel.parseJSON(response_data);

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
                putString(R.string.prefs_history_display, historyPage.getMinutes());
                putBoolean(R.string.prefs_history_clear, historyPage.getClearHistoryOnStart());
                putBoolean(R.string.prefs_history_auto, historyPage.getAutoRefresh().equals("on"));
                putString(R.string.prefs_history_points, historyPage.getDataPoints());
            }

            if (globals != null) {
                putString(R.string.prefs_grill_name, globals.getGrillName());
                putBoolean(R.string.prefs_admin_debug, globals.getDebugMode());
                putString(R.string.prefs_shutdown_time, globals.getShutdownTimer());

                if (globals.getStartUpTimer() != null) {
                    putString(R.string.prefs_startup_time, globals.getStartUpTimer());
                }

                if (globals.getUnits() != null) {
                    putString(R.string.prefs_grill_units, globals.getUnits());
                }

                if (globals.getFourProbes() != null) {
                    putBoolean(R.string.prefs_four_probe, globals.getFourProbes());
                }

                if (globals.getFourProbes() != null && globals.getFourProbes()) {
                    SettingsResponseModel.GrillProbeSettings grillProbeSettings =
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
                putString(R.string.prefs_work_pid_pb, cycleData.getPb());
                putString(R.string.prefs_work_pid_ti, cycleData.getTi());
                putString(R.string.prefs_work_pid_td, cycleData.getTd());
                putString(R.string.prefs_work_pid_cycle, cycleData.getHoldCycleTime());
                putString(R.string.prefs_work_auger_on, cycleData.getSmokeCycleTime());
                putString(R.string.prefs_work_pmode_mode, cycleData.getPMode());
                putString(R.string.prefs_work_pid_u_max, cycleData.getuMax());
                putString(R.string.prefs_work_pid_u_min, cycleData.getuMin());
                putString(R.string.prefs_work_pid_center, cycleData.getCenter());
            }

            if (pellets != null) {
                putString(R.string.prefs_pellet_empty, pellets.getEmpty());
                putString(R.string.prefs_pellet_full, pellets.getFull());
                putBoolean(R.string.prefs_pellet_warning_enabled, pellets.getWarningEnabled());
                putString(R.string.prefs_pellet_warning_level, pellets.getWarningLevel());
            }

            if (smokePlus != null) {
                putBoolean(R.string.prefs_work_splus_enabled, smokePlus.getEnabled());
                putString(R.string.prefs_work_splus_min, smokePlus.getMinTemp());
                putString(R.string.prefs_work_splus_max, smokePlus.getMaxTemp());
                putString(R.string.prefs_work_splus_fan, smokePlus.getCycle());
            }

            if (safety != null) {
                putString(R.string.prefs_safety_min_start, safety.getMinStartupTemp());
                putString(R.string.prefs_safety_max_start, safety.getMaxStartupTemp());
                putString(R.string.prefs_safety_max_temp, safety.getMaxTemp());
                putString(R.string.prefs_safety_retries, safety.getReigniteRetries());
            }

        } catch (IllegalStateException | JsonSyntaxException | NullPointerException e) {
            Timber.w(e, "Settings JSON Error");
            return false;
        }
        return true;
    }

    private void putString(int key, String value) {
        if (value != null) {
            if (!Prefs.getString(mContext.getString(key)).equals(value)) {
                Prefs.putString(mContext.getString(key), value);
            }
        }
    }

    private void putBoolean(int key, Boolean value) {
        if (value != null) {
            if (Prefs.getBoolean(mContext.getString(key)) != value) {
                Prefs.putBoolean(mContext.getString(key), value);
            }
        }
    }

    @SuppressWarnings("unused")
    private void putInt(int key, Integer value) {
        if (value != null) {
            if (Prefs.getInt(mContext.getString(key)) != value) {
                Prefs.putInt(mContext.getString(key), value);
            }
        }
    }
}
