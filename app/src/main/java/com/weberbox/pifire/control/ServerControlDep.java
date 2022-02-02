package com.weberbox.pifire.control;

import com.weberbox.pifire.constants.Constants;
import com.weberbox.pifire.constants.ServerConstants;
import com.weberbox.pifire.utils.JSONUtils;
import com.weberbox.pifire.model.remote.PelletDataModel.*;

import io.socket.client.Socket;

//Depreciated methods prior to PiFire 1.2.6
//@Deprecated
public class ServerControlDep {

    // Start Grill
    public static void modeStartGrill(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_ACTION,
                ServerConstants.MODE_START, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
    }

    // Monitor Grill
    public static void modeMonitorGrill(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_ACTION,
                ServerConstants.MODE_MONITOR, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
    }

    // Stop Grill
    public static void modeStopGrill(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_ACTION,
                ServerConstants.MODE_STOP, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
    }

    // Shutdown Grill
    public static void modeShutdownGrill(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_ACTION,
                ServerConstants.MODE_SHUTDOWN, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
    }

    // Mode Smoke
    public static void modeSmokeGrill(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_ACTION,
                ServerConstants.MODE_SMOKE, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
    }

    // Probe One Enable/Disable
    public static void probeOneToggle(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PROBES_ACTION,
                ServerConstants.PROBES_ONE_ENABLE, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Probe One Enable/Disable
    public static void probeTwoToggle(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PROBES_ACTION,
                ServerConstants.PROBES_TWO_ENABLE, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Smoke Plus Enable/Disable
    public static void setSmokePlus(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_ACTION,
                ServerConstants.MODE_SMOKE_PLUS, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
    }

    // Set Grill Temp
    public static void setGrillTemp(Socket socket, String temp) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_ACTION,
                ServerConstants.MODE_HOLD, String.valueOf(true),
                ServerConstants.MODE_TEMP_INPUT_RANGE, temp);
        socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
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
        if(payload != null) {
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
        if(payload != null) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
        }
    }

    // Timer Start/Stop
    public static void setTimerAction(Socket socket, String action) {
        String payload = null;
        switch (action) {
            case ServerConstants.PT_TIMER_START:
                payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
                        ServerConstants.TIMER_START, String.valueOf(true));
                break;
            case ServerConstants.PT_TIMER_PAUSE:
                payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
                        ServerConstants.TIMER_PAUSE, String.valueOf(true));
                break;
            case ServerConstants.PT_TIMER_STOP:
                payload = JSONUtils.encodeJSON(ServerConstants.TIMER_ACTION,
                        ServerConstants.TIMER_STOP, String.valueOf(true));
                break;
        }
        if(payload != null) {
            socket.emit(ServerConstants.UPDATE_CONTROL_DATA, payload);
        }
    }

    // Timer Set Time
    public static void setTimerTime(Socket socket, String hours, String minutes, Boolean shutdown) {
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
        String payload = JSONUtils.encodeJSON(ServerConstants.HISTORY_ACTION,
                ServerConstants.HISTORY_CLEAR_DATA, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Shutdown Timer
    public static void setShutdownTime(Socket socket, String shutDownTime) {
        String payload = JSONUtils.encodeJSON(ServerConstants.SHUTDOWN_ACTION,
                ServerConstants.SHUTDOWN_TIMER, shutDownTime);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Grill Probe
    public static void setGrillProbe(Socket socket, String grillProbe) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PROBES_ACTION,
                ServerConstants.PROBES_GRILL_PROBE, grillProbe);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Grill Probe 0 Type
    public static void setGrillProbeType(Socket socket, String grillProbeType) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PROBES_ACTION,
                ServerConstants.PROBES_GRILL_TYPE, grillProbeType);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Grill Probe 1 Type
    public static void setGrillProbe1Type(Socket socket, String grillProbe1Type) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PROBES_ACTION,
                ServerConstants.PROBES_GRILL1_TYPE, grillProbe1Type);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Grill Probe 2 Type
    public static void setGrillProbe2Type(Socket socket, String grillProbe2Type) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PROBES_ACTION,
                ServerConstants.PROBES_GRILL2_TYPE, grillProbe2Type);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Probe One Type
    public static void setProbe1Type(Socket socket, String probe1Type) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PROBES_ACTION,
                ServerConstants.PROBES_ONE_TYPE, probe1Type);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Probe Two Type
    public static void setProbe2Type(Socket socket, String probe2Type) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PROBES_ACTION,
                ServerConstants.PROBES_TWO_TYPE, probe2Type);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Enable IFTTT
    public static void setIFTTTEnabled(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.NOTIF_ACTION,
                ServerConstants.NOTIF_IFTTT_ENABLED, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Enable PushOver
    public static void setPushOverEnabled(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.NOTIF_ACTION,
                ServerConstants.NOTIF_PUSHOVER_ENABLED, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Enable PushBullet
    public static void setPushBulletEnabled(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.NOTIF_ACTION,
                ServerConstants.NOTIF_PUSHBULLET_ENABLED, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set IFTTT APIKey
    public static void setIFTTTAPIKey(Socket socket, String apiKey) {
        String payload = JSONUtils.encodeJSON(ServerConstants.NOTIF_ACTION,
                ServerConstants.NOTIF_IFTTTAPI, apiKey);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PushOver APIKey
    public static void setPushOverAPIKey(Socket socket, String apiKey) {
        String payload = JSONUtils.encodeJSON(ServerConstants.NOTIF_ACTION,
                ServerConstants.NOTIF_PUSHOVER_API, apiKey);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PushOver APIKey
    public static void setPushOverUserKeys(Socket socket, String userKeys) {
        String payload = JSONUtils.encodeJSON(ServerConstants.NOTIF_ACTION,
                ServerConstants.NOTIF_PUSHOVER_USER, userKeys);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PushOver PublicURL
    public static void setPushOverURL(Socket socket, String url) {
        String payload = JSONUtils.encodeJSON(ServerConstants.NOTIF_ACTION,
                ServerConstants.NOTIF_PUSHOVER_URL, url);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PushBullet APIKey
    public static void setPushBulletAPIKey(Socket socket, String apiKey) {
        String payload = JSONUtils.encodeJSON(ServerConstants.NOTIF_ACTION,
                ServerConstants.NOTIF_PUSHBULLET_KEY, apiKey);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PushBullet URL
    public static void setPushBulletURL(Socket socket, String url) {
        String payload = JSONUtils.encodeJSON(ServerConstants.NOTIF_ACTION,
                ServerConstants.NOTIF_PUSHBULLET_URL, url);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set History Auto Refresh
    public static void setHistoryRefresh(Socket socket, boolean refresh) {
        String payload = JSONUtils.encodeJSON(ServerConstants.HISTORY_ACTION,
                ServerConstants.HISTORY_AUTO_REFRESH, String.valueOf(refresh));
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set History Clear on Start
    public static void setHistoryClear(Socket socket, boolean clear) {
        String payload = JSONUtils.encodeJSON(ServerConstants.HISTORY_ACTION,
                ServerConstants.HISTORY_CLEAR_STARTUP, String.valueOf(clear));
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set History to Display
    public static void setHistoryMins(Socket socket, String mins) {
        String payload = JSONUtils.encodeJSON(ServerConstants.HISTORY_ACTION,
                ServerConstants.HISTORY_MINS, mins);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set History Points
    public static void setHistoryPoints(Socket socket, String points) {
        String payload = JSONUtils.encodeJSON(ServerConstants.HISTORY_ACTION,
                ServerConstants.HISTORY_DATA_POINTS, points);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Min Start Temp
    public static void setMinStartTemp(Socket socket, String temp) {
        String payload = JSONUtils.encodeJSON(ServerConstants.SAFETY_ACTION,
                ServerConstants.SAFETY_MIN_START_TEMP, temp);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Max Start Temp
    public static void setMaxStartTemp(Socket socket, String temp) {
        String payload = JSONUtils.encodeJSON(ServerConstants.SAFETY_ACTION,
                ServerConstants.SAFETY_MAX_START_TEMP, temp);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Reignite Retries
    public static void setReigniteRetries(Socket socket, String retries) {
        String payload = JSONUtils.encodeJSON(ServerConstants.SAFETY_ACTION,
                ServerConstants.SAFETY_REIGNITE_RETRIES, retries);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Max Temp
    public static void setMaxGrillTemp(Socket socket, String temp) {
        String payload = JSONUtils.encodeJSON(ServerConstants.SAFETY_ACTION,
                ServerConstants.SAFETY_MAX_TEMP, temp);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Grill Name
    public static void setGrillName(Socket socket, String name) {
        String payload = JSONUtils.encodeJSON(ServerConstants.GRILL_NAME_ACTION,
                ServerConstants.GRILL_NAME, name);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Auger Time
    public static void setAugerTime(Socket socket, String time) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_SMOKE_CYCLE_TIME, time);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set P-Mode
    public static void setPMode(Socket socket, String mode) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_PMODE, mode);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Smoke Plus Default
    public static void setSmokePlusDefault(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_DEFAULT_SP, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Smoke Fan Cycle Time
    public static void setSmokeFan(Socket socket, String time) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_SP_CYCLE, time);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Smoke Min Temp
    public static void setSmokeMinTemp(Socket socket, String temp) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_MIN_SP_TEMP, temp);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Smoke Max Temp
    public static void setSmokeMaxTemp(Socket socket, String temp) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_MAX_SP_TEMP, temp);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PID Cycle Time
    public static void setPIDTime(Socket socket, String time) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_HOLD_CYCLE_TIME, time);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PID PB
    public static void setPIDPB(Socket socket, String pb) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_PROP_BAND, pb);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PID Ti
    public static void setPIDTi(Socket socket, String ti) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_INTEGRAL_TIME, ti);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PID Td
    public static void setPIDTd(Socket socket, String td) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_DERIV_TIME, td);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PID U Min
    public static void setPIDuMin(Socket socket, String uMin) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_U_MIN, uMin);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PID U Max
    public static void setPIDuMax(Socket socket, String uMax) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_U_MAX, uMax);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set PID Center Ratio
    public static void setPIDCenter(Socket socket, String cycleTime) {
        String payload = JSONUtils.encodeJSON(ServerConstants.CYCLE_ACTION,
                ServerConstants.CYCLE_CENTER_RATIO, cycleTime);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Pellets Warning Enabled
    public static void setPelletWarningEnabled(Socket socket, Boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_ACTION,
                ServerConstants.PELLETS_WARNING_ENABLED, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Pellets Warning Level
    public static void setPelletWarningLevel(Socket socket, String level) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_ACTION,
                ServerConstants.PELLETS_WARNING_LEVEL, level);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Pellets Full
    public static void setPelletsFull(Socket socket, String full) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_ACTION,
                ServerConstants.PELLETS_FULL, full);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Pellets Empty
    public static void setPelletsEmpty(Socket socket, String empty) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_ACTION,
                ServerConstants.PELLETS_EMPTY, empty);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

    // Set Debug Mode
    public static void setDebugMode(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.ADMIN_ACTION,
                ServerConstants.ADMIN_DEBUG, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA, payload);
    }

    // Delete History
    public static void setDeleteHistory(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.ADMIN_ACTION,
                ServerConstants.ADMIN_DELETE_HISTORY, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA, payload);
    }

    // Delete Events
    public static void setDeleteEvents(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.ADMIN_ACTION,
                ServerConstants.ADMIN_DELETE_EVENTS, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA, payload);
    }

    // Delete Pellets
    public static void setDeletePellets(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.ADMIN_ACTION,
                ServerConstants.ADMIN_DELETE_PELLETS, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA, payload);
    }

    // Delete Pellets Log
    public static void setDeletePelletsLog(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.ADMIN_ACTION,
                ServerConstants.ADMIN_DELETE_PELLETS_LOG, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA, payload);
    }

    // Factory Reset
    public static void setFactoryReset(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.ADMIN_ACTION,
                ServerConstants.ADMIN_FACTORY_RESET, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA, payload);
    }

    // Reboot System
    public static void setRebootSystem(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.ADMIN_ACTION,
                ServerConstants.ADMIN_REBOOT, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA, payload);
    }

    // Shutdown System
    public static void setShutdownSystem(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.ADMIN_ACTION,
                ServerConstants.ADMIN_SHUTDOWN, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_ADMIN_DATA, payload);
    }

    // Delete Pellet Profile
    public static void setDeletePelletProfile(Socket socket, String pelletId) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_DELETE_PROFILE,
                ServerConstants.PELLETS_PROFILE, pelletId);
        socket.emit(ServerConstants.UPDATE_PELLET_DATA, payload);
    }

    // Add Pellet Profile
    public static void setAddPelletProfile(Socket socket, PelletProfileModel profile) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_ADD_PROFILE,
                ServerConstants.PELLETS_BRAND_NAME, profile.getBrand(),
                ServerConstants.PELLETS_WOOD_TYPE, profile.getWood(),
                ServerConstants.PELLETS_RATING, profile.getRating().toString(),
                ServerConstants.PELLETS_COMMENTS, profile.getComments());
        socket.emit(ServerConstants.UPDATE_PELLET_DATA, payload);
    }

    // Add Pellet Profile Load
    public static void setAddPelletProfileLoad(Socket socket, PelletProfileModel profile) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_ADD_PROFILE_LOAD,
                ServerConstants.PELLETS_BRAND_NAME, profile.getBrand(),
                ServerConstants.PELLETS_WOOD_TYPE, profile.getWood(),
                ServerConstants.PELLETS_RATING, profile.getRating().toString(),
                ServerConstants.PELLETS_COMMENTS, profile.getComments());
        socket.emit(ServerConstants.UPDATE_PELLET_DATA, payload);
    }

    // Edit Pellet Profile
    public static void setEditPelletProfile(Socket socket, PelletProfileModel profile) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_EDIT_PROFILE,
                ServerConstants.PELLETS_PROFILE, profile.getId(),
                ServerConstants.PELLETS_BRAND_NAME, profile.getBrand(),
                ServerConstants.PELLETS_WOOD_TYPE, profile.getWood(),
                ServerConstants.PELLETS_RATING, profile.getRating().toString(),
                ServerConstants.PELLETS_COMMENTS, profile.getComments());
        socket.emit(ServerConstants.UPDATE_PELLET_DATA, payload);
    }

    // Load Pellet Profile
    public static void setLoadPelletProfile(Socket socket, String pelletId) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_LOAD_PROFILE,
                ServerConstants.PELLETS_PROFILE, pelletId);
        socket.emit(ServerConstants.UPDATE_PELLET_DATA, payload);
    }

    // Delete Pellet Wood
    public static void setDeletePelletWood(Socket socket, String wood) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_EDIT_WOODS,
                ServerConstants.PELLETS_WOOD_DELETE, wood);
        socket.emit(ServerConstants.UPDATE_PELLET_DATA, payload);
    }

    // Add Pellet Wood
    public static void setAddPelletWood(Socket socket, String wood) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_EDIT_WOODS,
                ServerConstants.PELLETS_WOOD_NEW, wood);
        socket.emit(ServerConstants.UPDATE_PELLET_DATA, payload);
    }

    // Delete Pellet Brands
    public static void setDeletePelletBrand(Socket socket, String brand) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_EDIT_BRANDS,
                ServerConstants.PELLETS_BRAND_DELETE, brand);
        socket.emit(ServerConstants.UPDATE_PELLET_DATA, payload);
    }

    // Add Pellet Brands
    public static void setAddPelletBrand(Socket socket, String brand) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_EDIT_BRANDS,
                ServerConstants.PELLETS_BRAND_NEW, brand);
        socket.emit(ServerConstants.UPDATE_PELLET_DATA, payload);
    }

    // Delete Pellet Log
    public static void setDeletePelletLog(Socket socket, String log) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_DELETE_LOG,
                ServerConstants.PELLETS_LOG_DELETE, log);
        socket.emit(ServerConstants.UPDATE_PELLET_DATA, payload);
    }

    // Check Hopper Level
    public static void setCheckHopperLevel(Socket socket) {
        String payload = JSONUtils.encodeJSON(ServerConstants.PELLETS_HOPPER_CHECK,
                ServerConstants.PELLETS_HOPPER_LEVEL, String.valueOf(true));
        socket.emit(ServerConstants.UPDATE_PELLET_DATA, payload);
    }

    // Set Manual Mode
    public static void setManualMode(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_MANUAL,
                ServerConstants.MANUAL_SET_MODE, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_MANUAL_DATA, payload);
    }

    // Set Manual Fan Output
    public static void setManualFanOutput(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_MANUAL,
                ServerConstants.MANUAL_OUTPUT_FAN, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_MANUAL_DATA, payload);
    }

    // Set Manual Auger Output
    public static void setManualAugerOutput(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_MANUAL,
                ServerConstants.MANUAL_OUTPUT_AUGER, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_MANUAL_DATA, payload);
    }

    // Set Manual Igniter Output
    public static void setManualIgniterOutput(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_MANUAL,
                ServerConstants.MANUAL_OUTPUT_IGNITER, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_MANUAL_DATA, payload);
    }

    // Set Manual Power Output
    public static void setManualPowerOutput(Socket socket, boolean enabled) {
        String payload = JSONUtils.encodeJSON(ServerConstants.MODE_MANUAL,
                ServerConstants.MANUAL_OUTPUT_POWER, String.valueOf(enabled));
        socket.emit(ServerConstants.UPDATE_MANUAL_DATA, payload);
    }

    // Grill Temp Units
    public static void setTempUnits(Socket socket, String units) {
        String payload = JSONUtils.encodeJSON(ServerConstants.UNITS_ACTION,
                ServerConstants.TEMP_UNITS, units);
        socket.emit(ServerConstants.UPDATE_SETTINGS_DATA, payload);
    }

}
