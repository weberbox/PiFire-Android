package com.weberbox.pifire.feature.info.data.mapper

import com.weberbox.pifire.core.ui.util.getFormattedDate
import com.weberbox.pifire.feature.info.data.model.InfoDto
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InfoDtoToDataMapperTest {

    // The app build date goes through android.text.format.DateFormat, which unit tests don't have
    @Before
    fun setUp() {
        mockkStatic(::getFormattedDate)
        every { getFormattedDate(any(), any()) } returns ""
    }

    @After
    fun tearDown() {
        unmockkStatic(::getFormattedDate)
    }

    @Test
    fun `legacy cpu and ifconfig lines are parsed`() {
        val info = InfoDtoToDataMapper.map(
            InfoDto(
                cpuInfo = listOf("processor\t: 0", "model name\t: ARMv7 Processor rev 4 (v7l)"),
                ifConfig = listOf(
                    "        inet 127.0.0.1  netmask 255.0.0.0",
                    "        inet 192.168.1.5  netmask 255.255.255.0  broadcast 192.168.1.255",
                ),
            ),
        )
        assertEquals("ARMv7 Processor rev 4 (v7l)", info.cpuInfo)
        assertEquals(
            "Address:    192.168.1.5\n\nNetmask:    255.255.255.0\n\nBroadcast:    192.168.1.255",
            info.networkInfo,
        )
    }

    @Test
    fun `malformed legacy lines don't break the payload`() {
        val info = InfoDtoToDataMapper.map(
            InfoDto(
                uuid = "server-1",
                cpuInfo = listOf("model name"),
                ifConfig = listOf("netmask"),
            ),
        )
        assertEquals("server-1", info.uuid)
        assertEquals("", info.cpuInfo)
        assertEquals("Address:    \n\nNetmask:    \n\nBroadcast:    ", info.networkInfo)
    }

    @Test
    fun `fan pins only show with a DC fan, and loopback is hidden`() {
        val dto = InfoDto(
            outPins = mapOf("power" to 4, "dc_fan" to 26, "pwm" to null),
            networkInfo = mapOf(
                "lo" to InfoDto.NetworkInfo(ipAddress = "127.0.0.1"),
                "wlan0" to InfoDto.NetworkInfo(ipAddress = "192.168.1.5"),
            ),
        )
        val withoutFan = InfoDtoToDataMapper.map(dto.copy(dcFan = false))
        assertEquals(listOf("power"), withoutFan.outPins.map { it.name })
        assertEquals(setOf("wlan0"), withoutFan.networkMap.keys)

        val withFan = InfoDtoToDataMapper.map(dto.copy(dcFan = true))
        assertEquals(listOf("4", "26", "-"), withFan.outPins.map { it.pin })
    }

    @Test
    fun `an empty payload maps without errors`() {
        val info = InfoDtoToDataMapper.map(InfoDto())
        assertTrue(info.outPins.isEmpty())
        assertTrue(info.networkMap.isEmpty())
        assertEquals("", info.networkInfo)
    }
}
