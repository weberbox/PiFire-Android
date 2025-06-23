package com.weberbox.pifire.core.constants

import com.weberbox.pifire.BuildConfig

@Suppress("KotlinConstantConditions")
object AppConfig {

    var DEBUG: Boolean = BuildConfig.DEBUG

    const val IS_DEV_BUILD: Boolean = BuildConfig.FLAVOR == "dev"
    const val IS_PLAY_BUILD: Boolean = BuildConfig.FLAVOR == "playstore"

    // DataStore
    const val SETTINGS_DATA_NAME = "settings_data"
    const val EVENTS_DATA_NAME = "events_data"
    const val PELLETS_DATA_NAME = "pellets_data"
    const val RECIPES_DATA_NAME = "recipes_data"
    const val INFO_DATA_NAME = "info_data"
    const val DASH_DATA_NAME = "dash_data"
    const val HEADERS_DATA_NAME = "headers_data"

    // Pellet Warning
    const val MEDIUM_PELLET_WARNING: Int = 50
    const val LOW_PELLET_WARNING: Int = 30
    const val DANGER_PELLET_WARNING: Int = 15

    // Lazy Limited View
    const val LIST_VIEW_LIMIT: Int = 3

    // Pager Beyond Viewport Page Count
    const val PAGER_BEYOND_COUNT: Int = 1

    // Temp Selector Temps F
    const val MIN_GRILL_TEMP_SET_F: Int = 100
    const val DEFAULT_GRILL_TEMP_SET_F: Int = 225
    const val MIN_PROBE_TEMP_SET_F: Int = 40
    const val DEFAULT_PROBE_TEMP_SET_F: Int = 135

    // Temp Selector Temps C
    const val MIN_GRILL_TEMP_SET_C: Int = 37
    const val DEFAULT_GRILL_TEMP_SET_C: Int = 100
    const val MIN_PROBE_TEMP_SET_C: Int = 4
    const val DEFAULT_PROBE_TEMP_SET_C: Int = 55

    // Prime Amounts
    const val PRIME_MIN_GRAMS: Int = 10
    const val PRIME_MAX_GRAMS: Int = 50

    // In App Updates
    const val APP_UPDATE_CONFIG: String = "app_update_config"

    // Server Version Support
    const val SERVER_SUPPORT_CONFIG: String = "server_support_config"
}