package com.weberbox.pifire.constants;

public class ServerConstants {

    // Default String
    public static final String DEFAULT_SOCKET_URL = "http://192.168.1.254";

    // Urls
    public static final String URL_RECIPE_DETAILS = "/api/recipe/details";
    public static final String URL_RECIPE_DELETE = "/recipedata";
    public static final String URL_API_CONTROL = "/api/control";
    public static final String URL_API_SETTINGS = "/api/settings";

    // Folders
    public static final String FOLDER_RECIPES = "./recipes/";

    // Socket IO Strings
    public static final String LISTEN_GRILL_DATA = "grill_control_data";

    public static final String GE_GET_DASH_DATA = "get_dash_data";
    public static final String GE_GET_APP_DATA = "get_app_data";
    public static final String GA_SETTINGS_DATA = "settings_data";
    public static final String GA_PELLETS_DATA = "pellets_data";
    public static final String GA_EVENTS_DATA = "events_data";
    public static final String GA_INFO_DATA = "info_data";
    public static final String GA_MANUAL_DATA = "manual_data";

    public static final String PE_POST_APP_DATA = "post_app_data";
    public static final String PA_UPDATE_ACTION = "update_action";
    public static final String PA_ADMIN_ACTION = "admin_action";
    public static final String PA_UNITS_ACTION = "units_action";
    public static final String PA_REMOVE_ACTION = "remove_action";
    public static final String PA_PELLETS_ACTION = "pellets_action";
    public static final String PA_TIMER_ACTION = "timer_action";

    public static final String PT_SETTINGS = "settings";
    public static final String PT_CONTROL = "control";
    public static final String PT_UNITS_F = "f_units";
    public static final String PT_UNITS_C = "c_units";
    public static final String PT_ONESIGNAL_DEVICE = "onesignal_device";
    public static final String PT_LOAD_PROFILE = "load_profile";
    public static final String PT_HOPPER_CHECK = "hopper_check";
    public static final String PT_EDIT_BRANDS = "edit_brands";
    public static final String PT_EDIT_WOODS = "edit_woods";
    public static final String PT_ADD_PROFILE = "add_profile";
    public static final String PT_EDIT_PROFILE = "edit_profile";
    public static final String PT_DELETE_PROFILE = "delete_profile";
    public static final String PT_DELETE_LOG = "delete_log";
    public static final String PT_CLEAR_HISTORY = "clear_history";
    public static final String PT_CLEAR_EVENTS = "clear_events";
    public static final String PT_CLEAR_PELLETS = "clear_pelletdb";
    public static final String PT_CLEAR_PELLETS_LOG = "clear_pelletdb_log";
    public static final String PT_FACTORY_DEFAULTS = "factory_defaults";
    public static final String PT_RESTART = "restart";
    public static final String PT_REBOOT = "reboot";
    public static final String PT_SHUTDOWN = "shutdown";
    public static final String PT_TIMER_START = "start_timer";
    public static final String PT_TIMER_PAUSE = "pause_timer";
    public static final String PT_TIMER_STOP = "stop_timer";

    public static final String G_MODE_HOLD = "Hold";
    public static final String G_MODE_START = "Startup";
    public static final String G_MODE_SMOKE = "Smoke";
    public static final String G_MODE_SHUTDOWN = "Shutdown";
    public static final String G_MODE_MONITOR = "Monitor";
    public static final String G_MODE_STOP = "Stop";
    public static final String G_MODE_MANUAL = "Manual";
    public static final String G_MODE_PRIME = "Prime";
    public static final String G_MODE_RECIPE = "Recipe";
    public static final String G_MODE_REIGNITE = "Reignite";

    // Socket Strings Dep
    public static final String MODE_MANUAL = "manual";

}
