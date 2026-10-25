package com.weberbox.pifire.feature.events.data.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.core.ui.util.formatDateString
import com.weberbox.pifire.core.ui.util.formatTimeString
import com.weberbox.pifire.feature.events.data.model.EventsDto
import com.weberbox.pifire.feature.events.presentation.model.EventType
import com.weberbox.pifire.feature.events.presentation.model.EventsData.Events
import com.weberbox.pifire.feature.events.presentation.model.EventsData.Events.Event

object EventsDtoToDataMapper : Mapper<EventsDto, Events> {
    override fun map(from: EventsDto): Events {
        // Ids come from the event itself so list keys stay stable across updates; repeats of the
        // same message in the same second get a counter
        val seen = mutableMapOf<String, Int>()
        return Events(
            uuid = from.uuid.orEmpty(),
            events = from.events?.map {
                val baseId = "${it.date}|${it.time}|${it.message}"
                val count = seen.getOrDefault(baseId, 0)
                seen[baseId] = count + 1
                mapEvent(
                    from = it,
                    id = if (count == 0) baseId else "$baseId#$count",
                )
            } ?: emptyList()
        )
    }

    private fun mapEvent(from: EventsDto.Event, id: String) = Event(
        id = id,
        date = formatDateString(
            input = from.date.orEmpty(),
            inputFormat = "yyyy-MM-dd"
        ),
        time = formatTimeString(
            input = from.time.orEmpty(),
            inputFormat = "HH:mm:ss"
        ),
        message = from.message.orEmpty(),
        type = parseEventType(from.message.orEmpty()),
    )
}

private fun parseEventType(message: String): EventType {
    val lowercase = message.lowercase()
    return when {
        lowercase.contains("script starting") -> EventType.ScriptStarted
        lowercase.contains("mode started") -> EventType.ModeStarted
        lowercase.contains("script ended") -> EventType.ScriptEnded
        lowercase.contains("error") -> EventType.Error
        lowercase.contains("mode ended") -> EventType.ModeEnded
        lowercase.contains("warning") -> EventType.Warning
        lowercase.contains("*") -> EventType.Debug
        else -> EventType.Info
    }
}
