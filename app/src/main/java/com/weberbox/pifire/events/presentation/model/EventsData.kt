package com.weberbox.pifire.events.presentation.model

import androidx.compose.runtime.Immutable
import com.weberbox.pifire.common.domain.Uuid
import kotlinx.serialization.Serializable

@Serializable
data class EventsData(
    val eventsMap: Map<Uuid, Events> = emptyMap()
) {
    @Immutable
    @Serializable
    data class Events(
        val uuid: String = "",
        val events: List<Event> = emptyList()
    ) {
        @Serializable
        class Event(
            val id: String = "",
            val date: String = "",
            val time: String = "",
            val message: String = "",
            val title: String = "",
            val color: String = "",
        )
    }
}
