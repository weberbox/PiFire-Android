package com.weberbox.pifire.constants;

@SuppressWarnings("unused")
public class Constants {

    // Grill Modes
    public static final String GRILL_CURRENT_STOP = "Stop";
    public static final String GRILL_CURRENT_STARTUP = "Startup";
    public static final String GRILL_CURRENT_REIGNITE = "Reignite";
    public static final String GRILL_CURRENT_SMOKE = "Smoke";
    public static final String GRILL_CURRENT_HOLD = "Hold";
    public static final String GRILL_CURRENT_SHUTDOWN = "Shutdown";
    public static final String GRILL_CURRENT_MONITOR = "Monitor";
    public static final String GRILL_CURRENT_MANUAL = "Manual";
    public static final String GRILL_CURRENT_PRIME = "Prime";

    // Probe Types
    public static final String DASH_PROBE_PRIMARY = "Primary";
    public static final String DASH_PROBE_FOOD = "Food";

    // Main Fragments
    public static final int FRAG_PELLETS = 0;
    public static final int FRAG_DASHBOARD = 1;
    public static final int FRAG_EVENTS = 2;

    // Settings Fragment
    public static final int FRAG_APP_SETTINGS = 0;
    public static final int FRAG_PROBE_SETTINGS = 1;
    public static final int FRAG_NAME_SETTINGS = 2;
    public static final int FRAG_WORK_SETTINGS = 3;
    public static final int FRAG_PELLET_SETTINGS = 4;
    public static final int FRAG_TIMERS_SETTINGS = 5;
    public static final int FRAG_SAFETY_SETTINGS = 6;
    public static final int FRAG_NOTIF_SETTINGS = 7;
    public static final int FRAG_ADMIN_SETTINGS = 8;
    public static final int FRAG_MANUAL_SETTINGS = 9;
    public static final int FRAG_PWM_SETTINGS = 10;

    // Probe Notify Types
    public static final String TYPE_TARGET = "probe";
    public static final String TYPE_LIMIT_HIGH = "probe_limit_high";
    public static final String TYPE_LIMIT_LOW = "probe_limit_low";

    // Fade Directions
    public static final int FADE_OUT = 0;
    public static final int FADE_IN = 1;

    // Intents
    public static final String INTENT_SETUP_RESTART = "setup_restart";
    public static final String INTENT_SETTINGS_FRAGMENT = "setting_fragment";

    // JSON File Names
    public static final String JSON_EVENTS = "events.json";
    public static final String JSON_PELLETS = "pellets.json";
    public static final String JSON_INFO = "info.json";
    public static final String JSON_SERVER_INFO = "server_info.json";

    // Licenses
    public static final String LICENSES_LIST = "licenses";

    // Pellet Types
    public static final String PELLET_WOOD = "woods";
    public static final String PELLET_BRAND = "brand";
    public static final String PELLET_PROFILE = "profile";
    public static final String PELLET_LOG = "log";

    public static final String ITEM_RATING_5 = "★ ★ ★ ★ ★";
    public static final String ITEM_RATING_4 = "★ ★ ★ ★";
    public static final String ITEM_RATING_3 = "★ ★ ★";
    public static final String ITEM_RATING_2 = "★ ★";
    public static final String ITEM_RATING_1 = "★";

    // Update
    public static final String UPDATE_FILENAME = "app-update.apk";

}
