package com.weberbox.pifire.control;

import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.model.remote.PelletProfileModel;
import com.weberbox.pifire.utils.JSONUtils;
import com.weberbox.pifire.utils.VersionUtils;

import io.socket.client.Socket;

public class GrillControl {

    // Start Grill
    public static void modeStartGrill(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_ACTION,
                            ServerConstants.MODE_START, true));
        } else {
            GrillControlDep.modeStartGrill(socket);
        }
    }

    // Monitor Grill
    public static void modeMonitorGrill(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_ACTION,
                            ServerConstants.MODE_MONITOR, true));
        } else {
            GrillControlDep.modeMonitorGrill(socket);
        }
    }

    // Stop Grill
    public static void modeStopGrill(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_ACTION,
                            ServerConstants.MODE_STOP, true));
        } else {
            GrillControlDep.modeStopGrill(socket);
        }
    }

    // Shutdown Grill
    public static void modeShutdownGrill(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_ACTION,
                            ServerConstants.MODE_SHUTDOWN, true));
        } else {
            GrillControlDep.modeShutdownGrill(socket);
        }
    }

    // Mode Smoke
    public static void modeSmokeGrill(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_ACTION,
                            ServerConstants.MODE_SMOKE, true));
        } else {
            GrillControlDep.modeSmokeGrill(socket);
        }
    }

    // Probe One Enable/Disable
    public static void probeOneToggle(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.PROBES_ACTION,
                            ServerConstants.PROBES_ONE_ENABLE, enabled));
        } else {
            GrillControlDep.probeOneToggle(socket, enabled);
        }
    }

    // Probe One Enable/Disable
    public static void probeTwoToggle(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.PROBES_ACTION,
                            ServerConstants.PROBES_TWO_ENABLE, enabled));
        } else {
            GrillControlDep.probeTwoToggle(socket, enabled);
        }
    }

    // Smoke Plus Enable/Disable
    public static void setSmokePlus(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_ACTION,
                            ServerConstants.MODE_SMOKE_PLUS, enabled));
        } else {
            GrillControlDep.setSmokePlus(socket, enabled);
        }
    }

    // Set Grill Temp
    public static void setGrillTemp(Socket socket, String temp) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_ACTION,
                            ServerConstants.MODE_HOLD, true,
                            ServerConstants.MODE_TEMP_INPUT_RANGE, temp));
        } else {
            GrillControlDep.setGrillTemp(socket, temp);
        }
    }

    // Set Temp Notify
    public static void setProbeNotify(Socket socket, int probe, String temp, boolean shutdown) {
        if (VersionUtils.isSupported("1.2.3")) {
            String payload = null;
            switch (probe) {
                case Constants.PICKER_TYPE_GRILL:
                    payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                            ServerConstants.NOTIFY_GRILL, true,
                            ServerConstants.NOTIFY_TEMP_GRILL, temp);
                    break;
                case Constants.PICKER_TYPE_PROBE_ONE:
                    payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                            ServerConstants.NOTIFY_PROBE1, true,
                            ServerConstants.NOTIFY_TEMP_PROBE1, temp,
                            ServerConstants.NOTIFY_SHUTDOWN_P1, shutdown);
                    break;
                case Constants.PICKER_TYPE_PROBE_TWO:
                    payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                            ServerConstants.NOTIFY_PROBE2, true,
                            ServerConstants.NOTIFY_TEMP_PROBE2, temp,
                            ServerConstants.NOTIFY_SHUTDOWN_P2, shutdown);
                    break;
            }
            if (payload != null) {
                socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
            }
        } else {
            GrillControlDep.setProbeNotify(socket, probe, temp, shutdown);
        }
    }

    // Clear Temp Notify
    public static void clearProbeNotify(Socket socket, int probe) {
        if (VersionUtils.isSupported("1.2.3")) {
            String payload = null;
            switch (probe) {
                case Constants.PICKER_TYPE_GRILL:
                    payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                            ServerConstants.NOTIFY_GRILL, false);
                    break;
                case Constants.PICKER_TYPE_PROBE_ONE:
                    payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                            ServerConstants.NOTIFY_PROBE1, false);
                    break;
                case Constants.PICKER_TYPE_PROBE_TWO:
                    payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                            ServerConstants.NOTIFY_PROBE2, false);
                    break;
            }
            if (payload != null) {
                socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
            }
        } else {
            GrillControlDep.clearProbeNotify(socket, probe);
        }
    }

    // Timer Start/Stop
    public static void setTimerAction(Socket socket, int type) {
        if (VersionUtils.isSupported("1.2.3")) {
            String payload = null;
            switch (type) {
                case Constants.ACTION_TIMER_RESTART:
                    payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
                            ServerConstants.TIMER_START, true);
                    break;
                case Constants.ACTION_TIMER_PAUSE:
                    payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
                            ServerConstants.TIMER_PAUSE, true);
                    break;
                case Constants.ACTION_TIMER_STOP:
                    payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
                            ServerConstants.TIMER_STOP, true);
                    break;
            }
            if (payload != null) {
                socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
            }
        } else {
            GrillControlDep.setTimerAction(socket, type);
        }
    }

    // Timer Set Time
    public static void setTimerTime(Socket socket, String hours, String minutes, boolean shutdown) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.TIMER_ACTION,
                            ServerConstants.TIMER_START, true,
                            ServerConstants.TIMER_HOURS, hours,
                            ServerConstants.TIMER_MINS, minutes,
                            ServerConstants.TIMER_SHUTDOWN, shutdown));
        } else {
            GrillControlDep.setTimerTime(socket, hours, minutes, shutdown);
        }
    }

    // History Refresh
    public static void setHistoryDelete(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.HISTORY_ACTION,
                            ServerConstants.HISTORY_CLEAR_DATA, true));
        } else {
            GrillControlDep.setHistoryDelete(socket);
        }
    }

    // Shutdown Timer Depreciated
    public static void setShutdownTimeDep(Socket socket, String shutDownTime) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.SHUTDOWN_ACTION,
                        ServerConstants.SHUTDOWN_TIMER, shutDownTime));
    }

    // Shutdown Timer
    public static void setShutdownTime(Socket socket, String shutDownTime) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.TIMERS_ACTION,
                        ServerConstants.SHUTDOWN_TIMER, shutDownTime));
    }

    // Startup Timer
    public static void setStartupTime(Socket socket, String startUpTime) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.TIMERS_ACTION,
                        ServerConstants.STARTUP_TIMER, startUpTime));
    }

    // Grill Probe
    public static void setGrillProbe(Socket socket, String grillProbe) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PROBES_ACTION,
                        ServerConstants.PROBES_GRILL_PROBE, grillProbe));
    }

    // Grill Probe 0 Type
    public static void setGrillProbeType(Socket socket, String grillProbeType) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PROBES_ACTION,
                        ServerConstants.PROBES_GRILL_TYPE, grillProbeType));
    }

    // Grill Probe 1 Type
    public static void setGrillProbe1Type(Socket socket, String grillProbe1Type) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PROBES_ACTION,
                        ServerConstants.PROBES_GRILL1_TYPE, grillProbe1Type));
    }

    // Grill Probe 2 Type
    public static void setGrillProbe2Type(Socket socket, String grillProbe2Type) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PROBES_ACTION,
                        ServerConstants.PROBES_GRILL2_TYPE, grillProbe2Type));
    }

    // Probe One Type
    public static void setProbe1Type(Socket socket, String probe1Type) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PROBES_ACTION,
                        ServerConstants.PROBES_ONE_TYPE, probe1Type));
    }

    // Probe Two Type
    public static void setProbe2Type(Socket socket, String probe2Type) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PROBES_ACTION,
                ServerConstants.PROBES_TWO_TYPE, probe2Type);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Enable IFTTT
    public static void setIFTTTEnabled(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.NOTIF_ACTION,
                            ServerConstants.NOTIF_IFTTT_ENABLED, enabled));
        } else {
            GrillControlDep.setIFTTTEnabled(socket, enabled);
        }
    }

    // Enable PushOver
    public static void setPushOverEnabled(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.NOTIF_ACTION,
                            ServerConstants.NOTIF_PUSHOVER_ENABLED, enabled));
        } else {
            GrillControlDep.setPushOverEnabled(socket, enabled);
        }
    }

    // Enable PushBullet
    public static void setPushBulletEnabled(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.NOTIF_ACTION,
                            ServerConstants.NOTIF_PUSHBULLET_ENABLED, enabled));
        } else {
            GrillControlDep.setPushBulletEnabled(socket, enabled);
        }
    }

    // Set Firebase Enabled
    public static void setFirebaseEnabled(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.NOTIF_ACTION,
                            ServerConstants.NOTIF_FIREBASE_ENABLED, enabled));
        } else {
            GrillControlDep.setFirebaseEnabled(socket, enabled);
        }
    }

    // Set InfluxDB Enabled
    public static void setInfluxDBEnabled(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_INFLUXDB_ENABLED, enabled));
    }

    // Set IFTTT APIKey
    public static void setIFTTTAPIKey(Socket socket, String apiKey) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_IFTTTAPI, apiKey));
    }

    // Set PushOver APIKey
    public static void setPushOverAPIKey(Socket socket, String apiKey) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_PUSHOVER_API, apiKey));
    }

    // Set PushOver APIKey
    public static void setPushOverUserKeys(Socket socket, String userKeys) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_PUSHOVER_USER, userKeys));
    }

    // Set PushOver PublicURL
    public static void setPushOverURL(Socket socket, String url) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_PUSHOVER_URL, url));
    }

    // Set PushBullet APIKey
    public static void setPushBulletAPIKey(Socket socket, String apiKey) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_PUSHBULLET_KEY, apiKey));
    }

    // Set PushBullet Channel
    public static void setPushBulletChannel(Socket socket, String channel) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_PUSHBULLET_CHANNEL, channel));
    }

    // Set PushBullet URL
    public static void setPushBulletURL(Socket socket, String url) {
        String payload = JSONUtils.encodeJSON(ServerConstants.NOTIF_ACTION,
                ServerConstants.NOTIF_PUSHBULLET_URL, url);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Firebase ServerUrl
    public static void setFirebaseServerUrl(Socket socket, String serverUrl) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_FIREBASE_SERVERURL, serverUrl));
    }

    // Set InfluxDB URL
    public static void setInfluxDBUrl(Socket socket, String url) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_INFLUXDB_URL, url));
    }

    // Set InfluxDB Token
    public static void setInfluxDBToken(Socket socket, String token) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_INFLUXDB_TOKEN, token));
    }

    // Set InfluxDB Org
    public static void setInfluxDBOrg(Socket socket, String org) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_INFLUXDB_ORG, org));
    }

    // Set InfluxDB Bucket
    public static void setInfluxDBBucket(Socket socket, String bucket) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_INFLUXDB_BUCKET, bucket));
    }

    // Set History Auto Refresh
    public static void setHistoryRefresh(Socket socket, boolean refresh) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.HISTORY_ACTION,
                            ServerConstants.HISTORY_AUTO_REFRESH, refresh));
        } else {
            GrillControlDep.setHistoryRefresh(socket, refresh);
        }
    }

    // Set History Clear on Start
    public static void setHistoryClear(Socket socket, boolean clear) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.HISTORY_ACTION,
                            ServerConstants.HISTORY_CLEAR_STARTUP, clear));
        } else {
            GrillControlDep.setHistoryClear(socket, clear);
        }
    }

    // Set History to Display
    public static void setHistoryMins(Socket socket, String mins) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.HISTORY_ACTION,
                        ServerConstants.HISTORY_MINS, mins));
    }

    // Set History Points
    public static void setHistoryPoints(Socket socket, String points) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.HISTORY_ACTION,
                        ServerConstants.HISTORY_DATA_POINTS, points));
    }

    // Set Min Start Temp
    public static void setMinStartTemp(Socket socket, String temp) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.SAFETY_ACTION,
                        ServerConstants.SAFETY_MIN_START_TEMP, temp));
    }

    // Set Max Start Temp
    public static void setMaxStartTemp(Socket socket, String temp) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.SAFETY_ACTION,
                        ServerConstants.SAFETY_MAX_START_TEMP, temp));
    }

    // Set Reignite Retries
    public static void setReigniteRetries(Socket socket, String retries) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.SAFETY_ACTION,
                        ServerConstants.SAFETY_REIGNITE_RETRIES, retries));
    }

    // Set Max Temp
    public static void setMaxGrillTemp(Socket socket, String temp) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.SAFETY_ACTION,
                        ServerConstants.SAFETY_MAX_TEMP, temp));
    }

    // Set Grill Name
    public static void setGrillName(Socket socket, String name) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.GRILL_NAME_ACTION,
                        ServerConstants.GRILL_NAME, name));
    }

    // Set Auger Time
    public static void setAugerTime(Socket socket, String time) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_SMOKE_CYCLE_TIME, time));
    }

    // Set P-Mode
    public static void setPMode(Socket socket, String mode) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_PMODE, mode));
    }

    // Set Smoke Plus Default
    public static void setSmokePlusDefault(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.CYCLE_ACTION,
                            ServerConstants.CYCLE_DEFAULT_SP, enabled));
        } else {
            GrillControlDep.setSmokePlusDefault(socket, enabled);
        }
    }

    // Set Smoke Fan Cycle Time
    public static void setSmokeFan(Socket socket, String time) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_SP_CYCLE, time));
    }

    // Set Smoke Min Temp
    public static void setSmokeMinTemp(Socket socket, String temp) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_MIN_SP_TEMP, temp));
    }

    // Set Smoke Max Temp
    public static void setSmokeMaxTemp(Socket socket, String temp) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_MAX_SP_TEMP, temp));
    }

    // Set PID Cycle Time
    public static void setPIDTime(Socket socket, String time) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_HOLD_CYCLE_TIME, time));
    }

    // Set PID PB
    public static void setPIDPB(Socket socket, String pb) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_PROP_BAND, pb));
    }

    // Set PID Ti
    public static void setPIDTi(Socket socket, String ti) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_INTEGRAL_TIME, ti));
    }

    // Set PID Td
    public static void setPIDTd(Socket socket, String td) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_DERIV_TIME, td));
    }

    // Set PID U Min
    public static void setPIDuMin(Socket socket, String uMin) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_U_MIN, uMin));
    }

    // Set PID U Max
    public static void setPIDuMax(Socket socket, String uMax) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_U_MAX, uMax));
    }

    // Set PID Center Ratio
    public static void setPIDCenter(Socket socket, String cycleTime) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_CENTER_RATIO, cycleTime));
    }

    // Set Pellets Warning Enabled
    public static void setPelletWarningEnabled(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.PELLETS_ACTION,
                            ServerConstants.PELLETS_WARNING_ENABLED, enabled));
        } else {
            GrillControlDep.setPelletWarningEnabled(socket, enabled);
        }
    }

    // Set Pellets Warning Level
    public static void setPelletWarningLevel(Socket socket, String level) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_ACTION,
                        ServerConstants.PELLETS_WARNING_LEVEL, level));
    }

    // Set Pellets Full
    public static void setPelletsFull(Socket socket, String full) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_ACTION,
                        ServerConstants.PELLETS_FULL, full));
    }

    // Set Pellets Empty
    public static void setPelletsEmpty(Socket socket, String empty) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_ACTION,
                        ServerConstants.PELLETS_EMPTY, empty));
    }

    // Set Debug Mode
    public static void setDebugMode(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.ADMIN_ACTION,
                            ServerConstants.ADMIN_DEBUG, enabled));
        } else {
            GrillControlDep.setDebugMode(socket, enabled);
        }
    }

    // Delete History
    public static void setDeleteHistory(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.ADMIN_ACTION,
                            ServerConstants.ADMIN_DELETE_HISTORY, true));
        } else {
            GrillControlDep.setDeleteHistory(socket);
        }
    }

    // Delete Events
    public static void setDeleteEvents(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.ADMIN_ACTION,
                            ServerConstants.ADMIN_DELETE_EVENTS, true));
        } else {
            GrillControlDep.setDeleteEvents(socket);
        }
    }

    // Delete Pellets
    public static void setDeletePellets(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.ADMIN_ACTION,
                            ServerConstants.ADMIN_DELETE_PELLETS, true));
        } else {
            GrillControlDep.setDeletePellets(socket);
        }
    }

    // Delete Pellets Log
    public static void setDeletePelletsLog(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.ADMIN_ACTION,
                            ServerConstants.ADMIN_DELETE_PELLETS_LOG, true));
        } else {
            GrillControlDep.setDeletePelletsLog(socket);
        }
    }

    // Factory Reset
    public static void setFactoryReset(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.ADMIN_ACTION,
                            ServerConstants.ADMIN_FACTORY_RESET, true));
        } else {
            GrillControlDep.setFactoryReset(socket);
        }
    }

    // Reboot System
    public static void setRebootSystem(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.ADMIN_ACTION,
                            ServerConstants.ADMIN_REBOOT, true));
        } else {
            GrillControlDep.setRebootSystem(socket);
        }
    }

    // Shutdown System
    public static void setShutdownSystem(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.ADMIN_ACTION,
                            ServerConstants.ADMIN_SHUTDOWN, true));
        } else {
            GrillControlDep.setShutdownSystem(socket);
        }
    }

    // Delete Pellet Profile
    public static void setDeletePelletProfile(Socket socket, String pelletId) {
        socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_DELETE_PROFILE,
                        ServerConstants.PELLETS_PROFILE, pelletId));
    }

    // Add Pellet Profile
    public static void setAddPelletProfile(Socket socket, PelletProfileModel profile) {
        socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_ADD_PROFILE,
                        ServerConstants.PELLETS_BRAND_NAME, profile.getBrand(),
                        ServerConstants.PELLETS_WOOD_TYPE, profile.getWood(),
                        ServerConstants.PELLETS_RATING, profile.getRating().toString(),
                        ServerConstants.PELLETS_COMMENTS, profile.getComments()));
    }

    // Add Pellet Profile Load
    public static void setAddPelletProfileLoad(Socket socket, PelletProfileModel profile) {
        socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_ADD_PROFILE_LOAD,
                        ServerConstants.PELLETS_BRAND_NAME, profile.getBrand(),
                        ServerConstants.PELLETS_WOOD_TYPE, profile.getWood(),
                        ServerConstants.PELLETS_RATING, profile.getRating().toString(),
                        ServerConstants.PELLETS_COMMENTS, profile.getComments()));
    }

    // Edit Pellet Profile
    public static void setEditPelletProfile(Socket socket, PelletProfileModel profile) {
        socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_EDIT_PROFILE,
                        ServerConstants.PELLETS_PROFILE, profile.getId(),
                        ServerConstants.PELLETS_BRAND_NAME, profile.getBrand(),
                        ServerConstants.PELLETS_WOOD_TYPE, profile.getWood(),
                        ServerConstants.PELLETS_RATING, profile.getRating().toString(),
                        ServerConstants.PELLETS_COMMENTS, profile.getComments()));
    }

    // Load Pellet Profile
    public static void setLoadPelletProfile(Socket socket, String pelletId) {
        socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_LOAD_PROFILE,
                        ServerConstants.PELLETS_PROFILE, pelletId));
    }

    // Delete Pellet Wood
    public static void setDeletePelletWood(Socket socket, String wood) {
        socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_EDIT_WOODS,
                        ServerConstants.PELLETS_WOOD_DELETE, wood));
    }

    // Add Pellet Wood
    public static void setAddPelletWood(Socket socket, String wood) {
        socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_EDIT_WOODS,
                        ServerConstants.PELLETS_WOOD_NEW, wood));
    }

    // Delete Pellet Brands
    public static void setDeletePelletBrand(Socket socket, String brand) {
        socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_EDIT_BRANDS,
                        ServerConstants.PELLETS_BRAND_DELETE, brand));
    }

    // Add Pellet Brands
    public static void setAddPelletBrand(Socket socket, String brand) {
        socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_EDIT_BRANDS,
                        ServerConstants.PELLETS_BRAND_NEW, brand));
    }

    // Delete Pellet Log
    public static void setDeletePelletLog(Socket socket, String log) {
        socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_DELETE_LOG,
                        ServerConstants.PELLETS_LOG_DELETE, log));
    }

    // Check Hopper Level
    public static void setCheckHopperLevel(Socket socket) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.PELLETS_HOPPER_CHECK,
                            ServerConstants.PELLETS_HOPPER_LEVEL, true));
        } else {
            GrillControlDep.setCheckHopperLevel(socket);
        }
    }

    // Set Manual Mode
    public static void setManualMode(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_MANUAL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_MANUAL,
                            ServerConstants.MANUAL_SET_MODE, enabled));
        } else {
            GrillControlDep.setManualMode(socket, enabled);
        }
    }

    // Set Manual Fan Output
    public static void setManualFanOutput(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_MANUAL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_MANUAL,
                            ServerConstants.MANUAL_OUTPUT_FAN, enabled));
        } else {
            GrillControlDep.setManualFanOutput(socket, enabled);
        }
    }

    // Set Manual Auger Output
    public static void setManualAugerOutput(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_MANUAL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_MANUAL,
                            ServerConstants.MANUAL_OUTPUT_AUGER, enabled));
        } else {
            GrillControlDep.setManualAugerOutput(socket, enabled);
        }
    }

    // Set Manual Igniter Output
    public static void setManualIgniterOutput(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_MANUAL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_MANUAL,
                            ServerConstants.MANUAL_OUTPUT_IGNITER, enabled));
        } else {
            GrillControlDep.setManualIgniterOutput(socket, enabled);
        }
    }

    // Set Manual Power Output
    public static void setManualPowerOutput(Socket socket, boolean enabled) {
        if (VersionUtils.isSupported("1.2.3")) {
            socket.emit(ServerConstants.UPDATE_MANUAL_DATA,
                    JSONUtils.encodeJSON(
                            ServerConstants.MODE_MANUAL,
                            ServerConstants.MANUAL_OUTPUT_POWER, enabled));
        } else {
            GrillControlDep.setManualPowerOutput(socket, enabled);
        }
    }

    // Grill Temp Units
    public static void setTempUnits(Socket socket, String units) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.UNITS_ACTION,
                        ServerConstants.TEMP_UNITS, units));
    }

}
