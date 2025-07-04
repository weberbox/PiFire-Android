package com.weberbox.pifire.events.data.model

import kotlinx.serialization.Serializable

@Serializable
data class EventsDto(
    val uuid: String? = null,
    val events: List<Event>? = null,
) {
    @Serializable
    class Event(
        val date: String? = null,
        val time: String? = null,
        val message: String? = null
    )
}