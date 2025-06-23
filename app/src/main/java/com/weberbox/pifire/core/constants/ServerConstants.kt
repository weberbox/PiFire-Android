package com.weberbox.pifire.core.constants

object ServerConstants {

    // Socket IO Events
    const val LISTEN_EVENTS_DATA: String = "socket_event_data"
    const val LISTEN_PELLET_DATA: String = "socket_pellet_data"
    const val LISTEN_SETTINGS_DATA: String = "socket_settings_data"
    const val LISTEN_DASH_DATA: String = "socket_dash_data"

    // Get Function
    const val GF_LISTEN_APP_DATA: String = "listen_app_data"
    const val GF_GET_APP_DATA: String = "get_app_data"

    // Get Action
    const val GA_SETTINGS_DATA: String = "settings_data"
    const val GA_PELLETS_DATA: String = "pellets_data"
    const val GA_DASH_DATA: String = "dash_data"
    const val GA_EVENTS_DATA: String = "events_data"
    const val GA_RECIPE_DATA: String = "recipe_data"
    const val GA_INFO_DATA: String = "info_data"
    const val GA_MANUAL_DATA: String = "manual_data"
    const val GA_RECIPE_DETAILS: String = "details"

    // Post Function
    const val PF_POST_APP_DATA: String = "post_app_data"

    // Post Action
    const val PA_UPDATE_ACTION: String = "update_action"
    const val PA_ADMIN_ACTION: String = "admin_action"
    const val PA_UNITS_ACTION: String = "units_action"
    const val PA_PELLETS_ACTION: String = "pellets_action"
    const val PA_TIMER_ACTION: String = "timer_action"
    const val PA_RECIPES_ACTION: String = "recipes_action"
    const val PA_PROBES_ACTION: String = "probes_action"
    const val PA_NOTIFY_ACTION: String = "notify_action"

    // Post Type
    const val PT_SETTINGS: String = "settings"
    const val PT_CONTROL: String = "control"
    const val PT_LOAD_PROFILE: String = "load_profile"
    const val PT_HOPPER_CHECK: String = "hopper_check"
    const val PT_EDIT_BRANDS: String = "edit_brands"
    const val PT_EDIT_WOODS: String = "edit_woods"
    const val PT_ADD_PROFILE: String = "add_profile"
    const val PT_EDIT_PROFILE: String = "edit_profile"
    const val PT_DELETE_PROFILE: String = "delete_profile"
    const val PT_DELETE_LOG: String = "delete_log"
    const val PT_CLEAR_HISTORY: String = "clear_history"
    const val PT_CLEAR_EVENTS: String = "clear_events"
    const val PT_CLEAR_PELLETS: String = "clear_pelletdb"
    const val PT_CLEAR_PELLETS_LOG: String = "clear_pelletdb_log"
    const val PT_FACTORY_DEFAULTS: String = "factory_defaults"
    const val PT_RESTART: String = "restart"
    const val PT_REBOOT: String = "reboot"
    const val PT_SHUTDOWN: String = "shutdown"
    const val PT_TIMER_START: String = "start_timer"
    const val PT_TIMER_PAUSE: String = "pause_timer"
    const val PT_TIMER_STOP: String = "stop_timer"
    const val PT_RECIPE_DELETE: String = "recipe_delete"
    const val PT_RECIPE_START: String = "recipe_start"
    const val PT_PROBE_UPDATE: String = "probe_update"
    const val PT_NOTIFY_UPDATE: String = "notify_update"

    // Pid Selections
    const val CNTRLR_PID: String = "pid"
    const val CNTRLR_PID_AC: String = "pid_ac"
    const val CNTRLR_PID_SP: String = "pid_sp"

    // Manual Outputs
    const val OUTPUT_POWER: String = "power"
    const val OUTPUT_IGNITER: String = "igniter"
    const val OUTPUT_FAN: String = "fan"
    const val OUTPUT_AUGER: String = "auger"
    const val OUTPUT_PWM: String = "pwm"
}