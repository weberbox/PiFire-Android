package com.weberbox.pifire.core.model.events

import kotlinx.serialization.Serializable

/** What an event message is about; the UI picks its badge letter and color from this. */
@Serializable
enum class EventType {
    ScriptStarted,
    ScriptEnded,
    ModeStarted,
    ModeEnded,
    Error,
    Warning,
    Debug,
    Info,
}
