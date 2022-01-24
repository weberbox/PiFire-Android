package com.weberbox.pifire.control;


import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.utils.JSONUtils;

import io.socket.client.Socket;

//Depreciated methods prior to PiFire 1.2.3
//@Deprecated
public class GrillControlDep {

    // Start Grill
    public static void modeStartGrill(Socket socket) {
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_ACTION,
                        ServerConstants.MODE_START, String.valueOf(true)));
    }

    // Monitor Grill
    public static void modeMonitorGrill(Socket socket) {
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_ACTION,
                        ServerConstants.MODE_MONITOR, String.valueOf(true)));
    }

    // Stop Grill
    public static void modeStopGrill(Socket socket) {
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_ACTION,
                        ServerConstants.MODE_STOP, String.valueOf(true)));
    }

    // Shutdown Grill
    public static void modeShutdownGrill(Socket socket) {
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_ACTION,
                        ServerConstants.MODE_SHUTDOWN, String.valueOf(true)));
    }

    // Mode Smoke
    public static void modeSmokeGrill(Socket socket) {
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_ACTION,
                        ServerConstants.MODE_SMOKE, String.valueOf(true)));
    }

    // Probe One Enable/Disable
    public static void probeOneToggle(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PROBES_ACTION,
                        ServerConstants.PROBES_ONE_ENABLE, String.valueOf(enabled)));
    }

    // Probe One Enable/Disable
    public static void probeTwoToggle(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PROBES_ACTION,
                        ServerConstants.PROBES_TWO_ENABLE, String.valueOf(enabled)));
    }

    // Smoke Plus Enable/Disable
    public static void setSmokePlus(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_ACTION,
                        ServerConstants.MODE_SMOKE_PLUS, String.valueOf(enabled)));
    }

    // Set Grill Temp
    public static void setGrillTemp(Socket socket, String temp) {
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_ACTION,
                        ServerConstants.MODE_HOLD, String.valueOf(true),
                        ServerConstants.MODE_TEMP_INPUT_RANGE, temp));
    }

    // Set Temp Notify
    public static void setProbeNotify(Socket socket, int probe, String temp, boolean shutdown) {
        String payload = null;
        switch (probe) {
            case Constants.PICKER_TYPE_GRILL:
                payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                        ServerConstants.NOTIFY_GRILL, String.valueOf(true),
                        ServerConstants.NOTIFY_TEMP_GRILL, temp);
                break;
            case Constants.PICKER_TYPE_PROBE_ONE:
                if (shutdown) {
                    payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                            ServerConstants.NOTIFY_PROBE1, String.valueOf(true),
                            ServerConstants.NOTIFY_TEMP_PROBE1, temp,
                            ServerConstants.NOTIFY_SHUTDOWN_P1, String.valueOf(true));
                } else {
                    payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                            ServerConstants.NOTIFY_PROBE1, String.valueOf(true),
                            ServerConstants.NOTIFY_TEMP_PROBE1, temp);
                }
                break;
            case Constants.PICKER_TYPE_PROBE_TWO:
                if (shutdown) {
                    payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                            ServerConstants.NOTIFY_PROBE2, String.valueOf(true),
                            ServerConstants.NOTIFY_TEMP_PROBE2, temp,
                            ServerConstants.NOTIFY_SHUTDOWN_P2, String.valueOf(true));
                } else {
                    payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                            ServerConstants.NOTIFY_PROBE2, String.valueOf(true),
                            ServerConstants.NOTIFY_TEMP_PROBE2, temp);
                }
                break;
        }
        if (payload != null) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
        }
    }

    // Clear Temp Notify
    public static void clearProbeNotify(Socket socket, int probe) {
        String payload = null;
        switch (probe) {
            case Constants.PICKER_TYPE_GRILL:
                payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                        ServerConstants.NOTIFY_GRILL, String.valueOf(false));
                break;
            case Constants.PICKER_TYPE_PROBE_ONE:
                payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                        ServerConstants.NOTIFY_PROBE1, String.valueOf(false));
                break;
            case Constants.PICKER_TYPE_PROBE_TWO:
                payload = JSONUtils.encodeJSON(ServerConstants.NOTIFY_ACTION,
                        ServerConstants.NOTIFY_PROBE2, String.valueOf(false));
                break;
        }
        if (payload != null) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
        }
    }

    // Timer Start/Stop
    public static void setTimerAction(Socket socket, int type) {
        String payload = null;
        switch (type) {
            case Constants.ACTION_TIMER_RESTART:
                payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
                        ServerConstants.TIMER_START, String.valueOf(true));
                break;
            case Constants.ACTION_TIMER_PAUSE:
                payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
                        ServerConstants.TIMER_PAUSE, String.valueOf(true));
                break;
            case Constants.ACTION_TIMER_STOP:
                payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
                        ServerConstants.TIMER_STOP, String.valueOf(true));
                break;
        }
        if (payload != null) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
        }
    }

    // Timer Set Time
    public static void setTimerTime(Socket socket, String hours, String minutes, boolean shutdown) {
        String payload;
        if (shutdown) {
            payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
                    ServerConstants.TIMER_START, String.valueOf(true),
                    ServerConstants.TIMER_HOURS, hours,
                    ServerConstants.TIMER_MINS, minutes,
                    ServerConstants.TIMER_SHUTDOWN, String.valueOf(true));
        } else {
            payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
                    ServerConstants.TIMER_START, String.valueOf(true),
                    ServerConstants.TIMER_HOURS, hours,
                    ServerConstants.TIMER_MINS, minutes);
        }
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
    }

    // History Refresh
    public static void setHistoryDelete(Socket socket) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.HISTORY_ACTION,
                        ServerConstants.HISTORY_CLEAR_DATA, String.valueOf(true)));
    }

    // Enable IFTTT
    public static void setIFTTTEnabled(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_IFTTT_ENABLED, String.valueOf(enabled)));
    }

    // Enable PushOver
    public static void setPushOverEnabled(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_PUSHOVER_ENABLED, String.valueOf(enabled)));
    }

    // Enable PushBullet
    public static void setPushBulletEnabled(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.NOTIF_ACTION,
                        ServerConstants.NOTIF_PUSHBULLET_ENABLED, String.valueOf(enabled)));
    }

    // Set History Auto Refresh
    public static void setHistoryRefresh(Socket socket, boolean refresh) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.HISTORY_ACTION,
                        ServerConstants.HISTORY_AUTO_REFRESH, String.valueOf(refresh)));
    }

    // Set History Clear on Start
    public static void setHistoryClear(Socket socket, boolean clear) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.HISTORY_ACTION,
                        ServerConstants.HISTORY_CLEAR_STARTUP, String.valueOf(clear)));
    }

    // Set Smoke Plus Default
    public static void setSmokePlusDefault(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.CYCLE_ACTION,
                        ServerConstants.CYCLE_DEFAULT_SP, String.valueOf(enabled)));
    }

    // Set Pellets Warning Enabled
    public static void setPelletWarningEnabled(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_ACTION,
                        ServerConstants.PELLETS_WARNING_ENABLED, String.valueOf(enabled)));
    }

    // Set Debug Mode
    public static void setDebugMode(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.ADMIN_ACTION,
                        ServerConstants.ADMIN_DEBUG, String.valueOf(enabled)));
    }

    // Delete History
    public static void setDeleteHistory(Socket socket) {
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.ADMIN_ACTION,
                        ServerConstants.ADMIN_DELETE_HISTORY, String.valueOf(true)));
    }

    // Delete Events
    public static void setDeleteEvents(Socket socket) {
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.ADMIN_ACTION,
                        ServerConstants.ADMIN_DELETE_EVENTS, String.valueOf(true)));
    }

    // Delete Pellets
    public static void setDeletePellets(Socket socket) {
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.ADMIN_ACTION,
                        ServerConstants.ADMIN_DELETE_PELLETS, String.valueOf(true)));
    }

    // Delete Pellets Log
    public static void setDeletePelletsLog(Socket socket) {
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.ADMIN_ACTION,
                        ServerConstants.ADMIN_DELETE_PELLETS_LOG, String.valueOf(true)));
    }

    // Factory Reset
    public static void setFactoryReset(Socket socket) {
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.ADMIN_ACTION,
                        ServerConstants.ADMIN_FACTORY_RESET, String.valueOf(true)));
    }

    // Reboot System
    public static void setRebootSystem(Socket socket) {
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.ADMIN_ACTION,
                        ServerConstants.ADMIN_REBOOT, String.valueOf(true)));
    }

    // Shutdown System
    public static void setShutdownSystem(Socket socket) {
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.ADMIN_ACTION,
                        ServerConstants.ADMIN_SHUTDOWN, String.valueOf(true)));
    }

    // Check Hopper Level
    public static void setCheckHopperLevel(Socket socket) {
        socket.emit(ServerConstants.UPDATE_PELLET_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.PELLETS_HOPPER_CHECK,
                        ServerConstants.PELLETS_HOPPER_LEVEL, String.valueOf(true)));
    }

    // Set Manual Mode
    public static void setManualMode(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_MANUAL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_MANUAL,
                        ServerConstants.MANUAL_SET_MODE, String.valueOf(enabled)));
    }

    // Set Manual Fan Output
    public static void setManualFanOutput(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_MANUAL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_MANUAL,
                        ServerConstants.MANUAL_OUTPUT_FAN, String.valueOf(enabled)));
    }

    // Set Manual Auger Output
    public static void setManualAugerOutput(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_MANUAL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_MANUAL,
                        ServerConstants.MANUAL_OUTPUT_AUGER, String.valueOf(enabled)));
    }

    // Set Manual Igniter Output
    public static void setManualIgniterOutput(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_MANUAL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_MANUAL,
                        ServerConstants.MANUAL_OUTPUT_IGNITER, String.valueOf(enabled)));
    }

    // Set Manual Power Output
    public static void setManualPowerOutput(Socket socket, boolean enabled) {
        socket.emit(ServerConstants.UPDATE_MANUAL_DATA,
                JSONUtils.encodeJSON(
                        ServerConstants.MODE_MANUAL,
                        ServerConstants.MANUAL_OUTPUT_POWER, String.valueOf(enabled)));
    }

}
