package com.weberbox.pifire.core.model.dashboard

/** A grill mode as the server names it; the dashboard maps it to a title and icon. */
enum class RunningMode {
    Stop,
    Startup,
    Shutdown,
    Smoke,
    Hold,
    Prime,
    Reignite,
    Monitor,
    Recipe,
    Manual,
    Unknown;

    companion object {
        infix fun from(mode: String): RunningMode = entries.firstOrNull {
            it.name.equals(mode, ignoreCase = true)
        } ?: Unknown
    }
}
