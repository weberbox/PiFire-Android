package com.weberbox.pifire.feature.dashboard.data.mapper

import com.weberbox.pifire.feature.dashboard.data.model.DashDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DashDtoToDataMapperTest {

    @Test
    fun `food probe limits map to their own fields`() {
        val dash = DashDtoToDataMapper.map(
            DashDto(
                foodProbes = listOf(
                    DashDto.Probe(
                        label = "probe1",
                        lowLimitReq = true,
                        lowLimitShutdown = true,
                        highLimitShutdown = false,
                    ),
                ),
            ),
        )
        val probe = dash.foodProbes.single()
        assertEquals(true, probe.lowLimitReq)
        assertEquals(true, probe.lowLimitShutdown)
        assertEquals(false, probe.highLimitShutdown)
    }

    @Test
    fun `a NaN reading has no temp, and 0 is a real temp`() {
        val dash = DashDtoToDataMapper.map(
            DashDto(
                foodProbes = listOf(DashDto.Probe(label = "probe1", temp = Double.NaN)),
                primaryProbe = DashDto.Probe(label = "grill", temp = 0.0),
            ),
        )
        assertNull(dash.foodProbes.single().temp)
        assertEquals(0, dash.primaryProbe.temp)
    }
}
