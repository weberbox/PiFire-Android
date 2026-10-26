package com.weberbox.pifire.core.repo.mapper

import com.weberbox.pifire.core.di.JsonModule
import com.weberbox.pifire.core.model.settings.Setting
import com.weberbox.pifire.core.network.model.settings.SettingsDto
import org.junit.Assert.assertEquals
import org.junit.Test

class SettingsDtoToDataMapperTest {

    private val json = JsonModule.provideJson()

    private fun map(body: String) = SettingsDtoToDataMapper.map(json.decodeFromString<SettingsDto>(body))

    @Test
    fun `Celsius decimals in temp settings decode`() {
        val settings = map(
            """
            {
              "globals": {"grill_name": "Smoker", "units": "C"},
              "safety": {"minstartuptemp": 37.8, "maxstartuptemp": 65.5, "maxtemp": 287.8},
              "smoke_plus": {"min_temp": 71.1, "max_temp": 93.3},
              "keep_warm": {"temp": 65.6},
              "pwm": {"temp_range_list": [1.5, 3, 5.5]}
            }
            """,
        ).settings
        assertEquals("Smoker", settings.grillName)
        assertEquals(38, settings.safetyMinStartupTemp)
        assertEquals(66, settings.safetyMaxStartupTemp)
        assertEquals(288, settings.safetyMaxTemp)
        assertEquals(71, settings.sPlusMinTemp)
        assertEquals(93, settings.sPlusMaxTemp)
        assertEquals(66, settings.keepWarmTemp)
        assertEquals(listOf(2, 3, 6), settings.pwmTempRangeList)
    }

    @Test
    fun `missing sections fall back to the setting defaults`() {
        val server = map("""{"server_info": {"uuid": "server-1"}, "a_future_section": {"x": 1}}""")
        assertEquals("server-1", server.uuid)
        assertEquals(Setting.grillName.value, server.name)
        assertEquals(Setting.safetyMaxTemp.value, server.settings.safetyMaxTemp)
        assertEquals(Setting.tempUnits.value, server.settings.tempUnits)
    }

    @Test
    fun `a bad value in one section leaves the rest intact`() {
        val settings = map(
            """
            {
              "globals": {"grill_name": "Smoker"},
              "safety": {"maxtemp": "n/a", "reigniteretries": 3}
            }
            """,
        ).settings
        assertEquals("Smoker", settings.grillName)
        assertEquals(Setting.safetyMaxTemp.value, settings.safetyMaxTemp)
        assertEquals(3, settings.safetyReigniteRetries)
    }
}
