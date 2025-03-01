package com.weberbox.pifire.constants;


public class Constants {


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

    // Recipe Fragment
    public static final int FRAG_ALL_RECIPES = 0;
    public static final int FRAG_VIEW_RECIPE = 1;
    public static final int FRAG_EDIT_RECIPE = 2;

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
    public static final String INTENT_RECIPE_FRAGMENT = "recipe_fragment";
    public static final String INTENT_RECIPE_FILENAME = "recipe_filename";
    public static final String INTENT_RECIPE_STEP = "recipe_step";
    public static final String INTENT_RECIPE_IMAGE = "recipe_image";
    public static final String INTENT_EXTRA_HEADERS = "extra_headers_update";
    public static final String INTENT_SCROLL_DASH = "scroll_dash";
    public static final String INTENT_SETUP_BACK = "setup_back";
    public static final String INTENT_TRANS_ADMIN = "trans_admin";


    // JSON File Names
    public static final String JSON_EVENTS = "events.json";
    public static final String JSON_PELLETS = "pellets.json";
    public static final String JSON_INFO = "info.json";
    public static final String JSON_RECIPES = "recipes.json";

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
