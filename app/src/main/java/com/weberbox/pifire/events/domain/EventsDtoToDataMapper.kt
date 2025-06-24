package com.weberbox.pifire.events.domain

import androidx.compose.ui.graphics.toArgb
import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.common.presentation.theme.eventsDebug
import com.weberbox.pifire.common.presentation.theme.eventsError
import com.weberbox.pifire.common.presentation.theme.eventsInfo
import com.weberbox.pifire.common.presentation.theme.eventsModeEnded
import com.weberbox.pifire.common.presentation.theme.eventsModeStarted
import com.weberbox.pifire.common.presentation.theme.eventsScriptEnded
import com.weberbox.pifire.common.presentation.theme.eventsScriptStart
import com.weberbox.pifire.common.presentation.theme.eventsWarning
import com.weberbox.pifire.common.presentation.util.formatDateString
import com.weberbox.pifire.common.presentation.util.formatTimeString
import com.weberbox.pifire.events.data.model.EventsDto
import com.weberbox.pifire.events.presentation.model.EventsData.Events
import com.weberbox.pifire.events.presentation.model.EventsData.Events.Event
import java.util.UUID

internal typealias EventColor = String
internal typealias EventTitle = String

object EventsDtoToDataMapper : Mapper<EventsDto, Events> {
    override fun map(from: EventsDto) = Events(
        uuid = from.uuid.orEmpty(),
        events = from.events?.map {
            val titleColor = parseTitleColor(it.message.orEmpty())
            Event(
                id = UUID.randomUUID().toString(),
                date = formatDateString(
                    input = it.date.orEmpty(),
                    inputFormat = "yyyy-MM-dd"
                ),
                time = formatTimeString(
                    input = it.time.orEmpty(),
                    inputFormat = "HH:mm:ss"
                ),
                message = it.message.orEmpty(),
                title = titleColor.first,
                color = titleColor.second
            )
        } ?: emptyList()
    )
}

private fun parseTitleColor(message: String): Pair<EventTitle, EventColor> {
    if (message.lowercase().contains("script starting")) {
        return Pair("S", String.format("#%08X", eventsScriptStart.toArgb()))
    } else if (message.lowercase().contains("mode started")) {
        return Pair("M", String.format("#%08X", eventsModeStarted.toArgb()))
    } else if (message.lowercase().contains("script ended")) {
        return Pair("S", String.format("#%08X", eventsScriptEnded.toArgb()))
    } else if (message.lowercase().contains("error")) {
        return Pair("E", String.format("#%08X", eventsError.toArgb()))
    } else if (message.lowercase().contains("mode ended")) {
        return Pair("M", String.format("#%08X", eventsModeEnded.toArgb()))
    } else if (message.lowercase().contains("warning")) {
        return Pair("W", String.format("#%08X", eventsWarning.toArgb()))
    } else if (message.lowercase().contains("*")) {
        return Pair("D", String.format("#%08X", eventsDebug.toArgb()))
    } else {
        return Pair("I", String.format("#%08X", eventsInfo.toArgb()))
    }
}