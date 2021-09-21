package com.weberbox.pifire.constants;

public class Constants {

    // Firebase topics
    public static final String FIREBASE_TOPIC_GRILL = "GrillAlerts";

    // Notifications Channel
    public static final String NOTIFICATIONS_CHANNEL = "notification_channel";
    public static final String NOTIFICATIONS_TOPIC = "Grill Alerts";

    // Grill Modes
    public static final String GRILL_CURRENT_STOP = "Stop";
    public static final String GRILL_CURRENT_STARTUP = "Startup";
    public static final String GRILL_CURRENT_REIGNITE = "Reignite";
    public static final String GRILL_CURRENT_SMOKE = "Smoke";
    public static final String GRILL_CURRENT_HOLD = "Hold";
    public static final String GRILL_CURRENT_SHUTDOWN = "Shutdown";
    public static final String GRILL_CURRENT_MONITOR = "Monitor";

    // Temp Selector Temps
    public static final int MIN_GRILL_TEMP_SET = 160;
    public static final int MAX_GRILL_TEMP_SET = 500;
    public static final int DEFAULT_GRILL_TEMP_SET = 225;
    public static final int MIN_PROBE_TEMP_SET = 80;
    public static final int MAX_PROBE_TEMP_SET = 300;
    public static final int DEFAULT_PROBE_TEMP_SET = 135;

    // Callback Probe/Grill Types
    public static final int PICKER_TYPE_GRILL = 0;
    public static final int PICKER_TYPE_PROBE_ONE = 1;
    public static final int PICKER_TYPE_PROBE_TWO = 2;

    // Callback Mode Type
    public static final int ACTION_MODE_START = 0;
    public static final int ACTION_MODE_MONITOR = 1;
    public static final int ACTION_MODE_STOP = 2;
    public static final int ACTION_MODE_SMOKE = 3;
    public static final int ACTION_MODE_HOLD = 4;
    public static final int ACTION_MODE_SHUTDOWN = 5;
    public static final int ACTION_MODE_PROBE_ONE = 6;
    public static final int ACTION_MODE_PROBE_TWO = 7;

    // Callback Timer Type
    public static final int ACTION_TIMER_RESTART = 0;
    public static final int ACTION_TIMER_STOP = 1;
    public static final int ACTION_TIMER_PAUSE = 2;

    // Callback Admin Type
    public static final int ACTION_ADMIN_HISTORY = 0;
    public static final int ACTION_ADMIN_EVENTS = 1;
    public static final int ACTION_ADMIN_PELLET_LOG = 2;
    public static final int ACTION_ADMIN_PELLET = 3;
    public static final int ACTION_ADMIN_RESET = 4;
    public static final int ACTION_ADMIN_REBOOT = 5;
    public static final int ACTION_ADMIN_SHUTDOWN = 6;

    // Settings Fragment
    public static final int FRAG_APP_SETTINGS = 0;
    public static final int FRAG_PROBE_SETTINGS = 1;
    public static final int FRAG_NAME_SETTINGS = 2;
    public static final int FRAG_WORK_SETTINGS = 3;
    public static final int FRAG_PELLET_SETTINGS = 4;
    public static final int FRAG_SHUTDOWN_SETTINGS = 5;
    public static final int FRAG_HISTORY_SETTINGS = 6;
    public static final int FRAG_SAFETY_SETTINGS = 7;
    public static final int FRAG_NOTIF_SETTINGS = 8;
    public static final int FRAG_ADMIN_SETTINGS = 9;
    public static final int FRAG_INFO_SETTINGS = 10;

    // Intents
    public static final String INTENT_SETUP_RESTART = "setup_restart";
    public static final String INTENT_SETTINGS_FRAGMENT = "setting_fragment";

    // JSON File Names
    public static final String JSON_EVENTS = "events.json";
    public static final String JSON_HISTORY = "history.json";
    public static final String JSON_PELLETS = "pellets.json";
    public static final String JSON_SETTINGS = "settings.json";
    public static final String JSON_INFO = "info.json";

    // Licenses
    public static final String LICENSES_LIST = "licenses";

    // Pellet Types
    public static final String PELLET_WOOD = "woods";
    public static final String PELLET_BRAND = "brand";
    public static final String PELLET_PROFILE = "profile";

    public static final String PELLET_RATING_5 = "★ ★ ★ ★ ★";
    public static final String PELLET_RATING_4 = "★ ★ ★ ★";
    public static final String PELLET_RATING_3 = "★ ★ ★";
    public static final String PELLET_RATING_2 = "★ ★";
    public static final String PELLET_RATING_1 = "★";

}
