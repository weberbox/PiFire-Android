package com.weberbox.pifire.core.model.push

/**
 * An alert from a PiFire server, delivered by the push relay (contract in docs/PUSH.md 3.6 and
 * 3.9). Every detail is optional: an older or newer PiFire may leave any of them out.
 */
data class PushAlert(
    val alertId: String,
    val type: AlertType,
    val serverUuid: String,
    /** Epoch millis on the PiFire server, or null if missing. */
    val timeMillis: Long?,
    val probeName: String? = null,
    val target: Double? = null,
    val limit: Double? = null,
    /** True for a high limit, false for low, null when PiFire doesn't say. */
    val isHighLimit: Boolean? = null,
    val units: String? = null,
    val level: Double? = null,
    val temp: Double? = null,
    val maxTemp: Double? = null,
    val startupTemp: Double? = null,
    val message: String? = null,
    val fallbackTitle: String? = null,
    val fallbackBody: String? = null,
)

/** Alert types and their names on the wire; a name never changes once shipped. */
enum class AlertType(val wireName: String) {
    PROBE_TARGET("probe_target"),
    PROBE_LIMIT("probe_limit"),
    TIMER_EXPIRED("timer_expired"),
    PELLET_LEVEL_LOW("pellet_level_low"),
    GRILL_ERROR("grill_error"),
    GRILL_ERROR_MAX_TEMP("grill_error_max_temp"),
    GRILL_ERROR_STARTUP_TEMP("grill_error_startup_temp"),
    GRILL_REIGNITE("grill_reignite"),
    GRILL_WARNING("grill_warning"),
    CONTROL_STOPPED("control_stopped"),
    RECIPE_MESSAGE("recipe_message"),
    TEST("test"),

    /** A type this app version doesn't know: shown with PiFire's fallback text. */
    UNKNOWN(""),
    ;

    companion object {
        fun fromWireName(name: String?): AlertType = entries.firstOrNull { it != UNKNOWN && it.wireName == name } ?: UNKNOWN
    }
}
