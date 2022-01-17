package com.weberbox.pifire.constants;

@SuppressWarnings("unused")
public class ServerConstants {

    // Default String
    public static final String DEFAULT_SOCKET_URL = "http://192.168.1.254";

    // Socket Strings
    public static final String REQUEST_GRILL_DATA = "request_grill_data";
    public static final String LISTEN_GRILL_DATA = "grill_control_data";
    public static final String REQUEST_PELLET_DATA = "request_pellet_data";
    public static final String REQUEST_HISTORY_DATA = "request_history_data";
    public static final String REQUEST_EVENT_DATA = "request_event_data";
    public static final String REQUEST_SETTINGS_DATA = "request_settings_data";
    public static final String REQUEST_INFO_DATA = "request_info_data";
    public static final String REQUEST_MANUAL_DATA = "request_manual_data";
    public static final String REQUEST_BACKUP_DATA = "request_backup_data";
    public static final String REQUEST_BACKUP_LIST = "request_backup_list";

    // Socket Updating
    public static final String UPDATE_CONTROL_DATA = "update_control_data";
    public static final String UPDATE_SETTINGS_DATA = "update_settings_data";
    public static final String UPDATE_PELLET_DATA = "update_pellet_data";
    public static final String UPDATE_ADMIN_DATA = "update_admin_data";
    public static final String UPDATE_MANUAL_DATA = "update_manual_data";
    public static final String UPDATE_RESTORE_DATA = "update_restore_data";

    public static final String EVENTS_LIST = "events_list";

    public static final String TIMER_ACTION = "timer";
    public static final String TIMER_START = "start";
    public static final String TIMER_PAUSE = "pause";
    public static final String TIMER_STOP = "stop";
    public static final String TIMER_HOURS = "hoursInputRange";
    public static final String TIMER_MINS = "minsInputRange";
    public static final String TIMER_SHUTDOWN = "shutdownTimer";

    public static final String NOTIFY_ACTION = "notify";
    public static final String NOTIFY_SET = "setnotify";
    public static final String NOTIFY_GRILL = "grillnotify";
    public static final String NOTIFY_PROBE1 = "probe1notify";
    public static final String NOTIFY_PROBE2 = "probe2notify";
    public static final String NOTIFY_SHUTDOWN_P1 = "shutdownP1";
    public static final String NOTIFY_SHUTDOWN_P2 = "shutdownP2";

    public static final String NOTIFY_TEMP_GRILL = "grilltempInputRange";
    public static final String NOTIFY_TEMP_PROBE1 = "probe1tempInputRange";
    public static final String NOTIFY_TEMP_PROBE2 = "probe2tempInputRange";

    public static final String MODE_ACTION = "setmode";
    public static final String MODE_HOLD = "setpointtemp";
    public static final String MODE_START = "setmodestartup";
    public static final String MODE_SMOKE = "setmodesmoke";
    public static final String MODE_SHUTDOWN = "setmodeshutdown";
    public static final String MODE_MONITOR = "setmodemonitor";
    public static final String MODE_STOP = "setmodestop";
    public static final String MODE_SMOKE_PLUS = "setmodesmokeplus";
    public static final String MODE_TEMP_INPUT_RANGE = "tempInputRange";
    public static final String MODE_MANUAL = "manual";

    public static final String UNITS_ACTION = "units";
    public static final String TEMP_UNITS = "temp_units";

    public static final String PROBES_ACTION = "probes";
    public static final String PROBES_ONE_ENABLE = "probe1enable";
    public static final String PROBES_TWO_ENABLE = "probe2enable";
    public static final String PROBES_GRILL_PROBE = "grill_probes";
    public static final String PROBES_GRILL_TYPE = "grill_probe_type";
    public static final String PROBES_GRILL1_TYPE = "grill_probe1_type";
    public static final String PROBES_GRILL2_TYPE = "grill_probe2_type";
    public static final String PROBES_ONE_TYPE = "probe1_type";
    public static final String PROBES_TWO_TYPE = "probe2_type";

    public static final String NOTIF_ACTION = "notifications";
    public static final String NOTIF_IFTTT_ENABLED = "ifttt_enabled";
    public static final String NOTIF_IFTTTAPI = "iftttapi";
    public static final String NOTIF_PUSHOVER_ENABLED = "pushover_enabled";
    public static final String NOTIF_PUSHOVER_API = "pushover_apikey";
    public static final String NOTIF_PUSHOVER_USER = "pushover_userkeys";
    public static final String NOTIF_PUSHOVER_URL = "pushover_publicurl";
    public static final String NOTIF_PUSHBULLET_ENABLED = "pushbullet_enabled";
    public static final String NOTIF_PUSHBULLET_KEY = "pushbullet_apikey";
    public static final String NOTIF_PUSHBULLET_CHANNEL = "pushbullet_channel";
    public static final String NOTIF_PUSHBULLET_URL = "pushbullet_publicurl";
    public static final String NOTIF_FIREBASE_ENABLED = "firebase_enabled";
    public static final String NOTIF_FIREBASE_SERVERURL = "firebase_serverurl";

    public static final String CYCLE_ACTION = "cycle";
    public static final String CYCLE_PMODE = "pmode";
    public static final String CYCLE_HOLD_CYCLE_TIME = "holdcycletime";
    public static final String CYCLE_SMOKE_CYCLE_TIME = "smokecycletime";
    public static final String CYCLE_PROP_BAND = "propband";
    public static final String CYCLE_INTEGRAL_TIME = "integraltime";
    public static final String CYCLE_DERIV_TIME = "derivtime";
    public static final String CYCLE_U_MAX= "u_max";
    public static final String CYCLE_U_MIN = "u_min";
    public static final String CYCLE_CENTER_RATIO= "center";
    public static final String CYCLE_SP_CYCLE = "sp_cycle";
    public static final String CYCLE_MIN_SP_TEMP = "minsptemp";
    public static final String CYCLE_MAX_SP_TEMP = "maxsptemp";
    public static final String CYCLE_DEFAULT_SP = "defaultsmokeplus";

    public static final String SHUTDOWN_ACTION = "shutdown";
    public static final String TIMERS_ACTION = "timers";
    public static final String SHUTDOWN_TIMER = "shutdown_timer";
    public static final String STARTUP_TIMER = "startup_timer";

    public static final String HISTORY_ACTION = "history";
    public static final String HISTORY_MINS = "historymins";
    public static final String HISTORY_CLEAR_STARTUP = "clearhistorystartup";
    public static final String HISTORY_AUTO_REFRESH = "historyautorefresh";
    public static final String HISTORY_DATA_POINTS = "datapoints";
    public static final String HISTORY_CLEAR_DATA = "clearhistory";

    public static final String SAFETY_ACTION = "safety";
    public static final String SAFETY_MIN_START_TEMP = "minstartuptemp";
    public static final String SAFETY_MAX_START_TEMP = "maxstartuptemp";
    public static final String SAFETY_REIGNITE_RETRIES = "reigniteretries";
    public static final String SAFETY_MAX_TEMP = "maxtemp";

    public static final String GRILL_NAME_ACTION = "grillname";
    public static final String GRILL_NAME = "grill_name";

    public static final String ADMIN_ACTION = "admin";
    public static final String ADMIN_DEBUG = "debugenabled";
    public static final String ADMIN_REBOOT = "reboot";
    public static final String ADMIN_SHUTDOWN = "shutdown";
    public static final String ADMIN_DELETE_HISTORY = "clearhistory";
    public static final String ADMIN_DELETE_EVENTS = "clearevents";
    public static final String ADMIN_DELETE_PELLETS = "clearpelletdb";
    public static final String ADMIN_DELETE_PELLETS_LOG = "clearpelletdblog";
    public static final String ADMIN_FACTORY_RESET = "factorydefaults";

    public static final String PELLETS_ACTION = "pellets";
    public static final String PELLETS_FULL = "full";
    public static final String PELLETS_EMPTY = "empty";

    public static final String PELLETS_LOAD_PROFILE = "loadprofile";
    public static final String PELLETS_EDIT_BRANDS = "editbrands";
    public static final String PELLETS_EDIT_WOODS = "editwoods";
    public static final String PELLETS_ADD_PROFILE = "addprofile";
    public static final String PELLETS_ADD_PROFILE_LOAD = "addprofileload";
    public static final String PELLETS_EDIT_PROFILE = "editprofile";
    public static final String PELLETS_DELETE_PROFILE = "deleteprofile";
    public static final String PELLETS_DELETE_LOG = "deletelog";
    public static final String PELLETS_PROFILE = "profile";
    public static final String PELLETS_BRAND_NAME = "brand_name";
    public static final String PELLETS_WOOD_TYPE = "wood_type";
    public static final String PELLETS_RATING = "rating";
    public static final String PELLETS_COMMENTS = "comments";
    public static final String PELLETS_WOOD_DELETE = "delWood";
    public static final String PELLETS_WOOD_NEW = "newWood";
    public static final String PELLETS_BRAND_DELETE = "delBrand";
    public static final String PELLETS_BRAND_NEW = "newBrand";
    public static final String PELLETS_LOG_DELETE = "delLog";
    public static final String PELLETS_HOPPER_CHECK = "hoppercheck";
    public static final String PELLETS_HOPPER_LEVEL = "hopperlevel";
    public static final String PELLETS_WARNING_ENABLED = "pelletwarning";
    public static final String PELLETS_WARNING_LEVEL = "warninglevel";

    public static final String MANUAL_SET_MODE = "setmode";
    public static final String MANUAL_OUTPUT_FAN = "change_output_fan";
    public static final String MANUAL_OUTPUT_AUGER = "change_output_auger";
    public static final String MANUAL_OUTPUT_IGNITER = "change_output_igniter";
    public static final String MANUAL_OUTPUT_POWER = "change_output_power";

}
