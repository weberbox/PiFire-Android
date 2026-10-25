package com.weberbox.pifire.feature.events.data.mapper

import com.weberbox.pifire.core.model.events.EventType
import com.weberbox.pifire.feature.events.data.model.EventsDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class EventsDtoToDataMapperTest {

    private val dto = EventsDto(
        events = listOf(
            EventsDto.Event(date = "2026-10-25", time = "18:18:00", message = "Smoke Mode started"),
            EventsDto.Event(date = "2026-10-25", time = "18:18:00", message = "Smoke Mode started"),
            EventsDto.Event(date = "2026-10-25", time = "18:19:00", message = "[WARNING] Hopper low"),
        )
    )

    @Test
    fun `ids are stable across mappings and unique for repeated events`() {
        val first = EventsDtoToDataMapper.map(dto).events.map { it.id }
        val second = EventsDtoToDataMapper.map(dto).events.map { it.id }
        assertEquals(first, second)
        assertNotEquals(first[0], first[1])
    }

    @Test
    fun `events are typed from their message`() {
        val types = EventsDtoToDataMapper.map(dto).events.map { it.type }
        assertEquals(listOf(EventType.ModeStarted, EventType.ModeStarted, EventType.Warning), types)
    }
}
