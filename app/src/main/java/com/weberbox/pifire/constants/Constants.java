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

    // Callback Probe/Grill Types
    public static final int PICKER_TYPE_GRILL = 0;
    public static final int PICKER_TYPE_PROBE_ONE = 1;
    public static final int PICKER_TYPE_PROBE_TWO = 2;

    // Callback Backup/Restore Type
    public static final int ACTION_BACKUP_SETTINGS = 0;
    public static final int ACTION_BACKUP_PELLETDB = 1;
    public static final int ACTION_RESTORE_SETTINGS = 2;
    public static final int ACTION_RESTORE_PELLETDB = 3;

    // Main Fragments
    public static final int FRAG_RECIPES = 0;
    public static final int FRAG_PELLETS = 1;
    public static final int FRAG_DASHBOARD = 2;
    public static final int FRAG_HISTORY = 3;
    public static final int FRAG_EVENTS = 4;

    // Settings Fragment
    public static final int FRAG_APP_SETTINGS = 0;
    public static final int FRAG_PROBE_SETTINGS = 1;
    public static final int FRAG_NAME_SETTINGS = 2;
    public static final int FRAG_WORK_SETTINGS = 3;
    public static final int FRAG_PELLET_SETTINGS = 4;
    public static final int FRAG_TIMERS_SETTINGS = 5;
    public static final int FRAG_HISTORY_SETTINGS = 6;
    public static final int FRAG_SAFETY_SETTINGS = 7;
    public static final int FRAG_NOTIF_SETTINGS = 8;
    public static final int FRAG_ADMIN_SETTINGS = 9;
    public static final int FRAG_MANUAL_SETTINGS = 10;

    // Recipe Fragment
    public static final int FRAG_VIEW_RECIPE = 0;
    public static final int FRAG_EDIT_RECIPE = 1;

    // Fade Directions
    public static final int FADE_OUT = 0;
    public static final int FADE_IN = 1;

    // Intents
    public static final String INTENT_SETUP_RESTART = "setup_restart";
    public static final String INTENT_SETTINGS_FRAGMENT = "setting_fragment";
    public static final String INTENT_RECIPE_FRAGMENT = "recipe_fragment";
    public static final String INTENT_RECIPE_ID = "recipe_id";
    public static final String INTENT_CRASHED_ID = "crashed_id";

    // Results
    public static final int RESULT_PERMISSIONS = 405;

    // JSON File Names
    public static final String JSON_EVENTS = "events.json";
    public static final String JSON_HISTORY = "history.json";
    public static final String JSON_PELLETS = "pellets.json";
    public static final String JSON_SETTINGS = "settings.json";
    public static final String JSON_INFO = "info.json";
    public static final String JSON_RECIPES = "recipes.json";

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

    // Recipe Difficulties
    public static final int RECIPE_DIF_BEGIN = 0;
    public static final int RECIPE_DIF_EASY = 1;
    public static final int RECIPE_DIF_MOD = 2;
    public static final int RECIPE_DIF_HARD = 3;
    public static final int RECIPE_DIF_V_HARD = 4;

    // Update
    public static final String UPDATE_FILENAME = "app-update.apk";

    // PiFire Backup
    public static final String BACKUP_SETTINGS = "settings";
    public static final String BACKUP_PELLETDB = "pelletdb";
    public static final String BACKUP_SETTINGS_FILENAME = "PiFire_";
    public static final String BACKUP_PELLETDB_FILENAME = "PelletDB_";

    // Database
    public static final String DB_RECIPES_TABLE = "recipes";
    public static final String DB_RECIPES = "recipes.db";
    public static final String DB_RECIPES_BACKUP_FILENAME = "Recipes_";

    // One Signal Status
    public static final int ONESIGNAL_NO_ID = 0;
    public static final int ONESIGNAL_NO_CONSENT = 1;
    public static final int ONESIGNAL_NOT_REGISTERED = 2;
    public static final int ONESIGNAL_DEVICE_ERROR = 3;
    public static final int ONESIGNAL_REGISTERED = 4;
    public static final int ONESIGNAL_NOT_SUBSCRIBED = 5;
    public static final int ONESIGNAL_APP_UPDATED = 6;
    public static final int ONESIGNAL_NULL_TOKEN = 7;

}
