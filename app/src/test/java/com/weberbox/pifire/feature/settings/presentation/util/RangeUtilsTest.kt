package com.weberbox.pifire.feature.settings.presentation.util

import org.junit.Assert.assertEquals
import org.junit.Test

class RangeUtilsTest {

    @Test
    fun `sorting keeps each profile with its temp and the top profile last`() {
        val (temps, profiles) = sortRangesByTemp(
            temps = listOf(30, 10, 20),
            profiles = listOf("p30", "p10", "p20", "top"),
        )
        assertEquals(listOf(10, 20, 30), temps)
        assertEquals(listOf("p10", "p20", "p30", "top"), profiles)
    }

    @Test
    fun `editing a row replaces its temp and profile`() {
        // Rows as the screen lists them: one per range plus the top row
        val (temps, profiles) = updateRange(
            index = 0,
            temp = 15,
            profile = "new",
            rowTemps = listOf(10, 20, 21),
            rowProfiles = listOf("p10", "p20", "top"),
        )
        assertEquals(listOf(15, 20), temps)
        assertEquals(listOf("new", "p20", "top"), profiles)
    }

    @Test
    fun `adding a range appends it and the new profile becomes the top one`() {
        val (temps, profiles) = updateRange(
            index = -1,
            temp = 40,
            profile = "new",
            rowTemps = listOf(10, 20, 21),
            rowProfiles = listOf("p10", "p20", "top"),
        )
        assertEquals(listOf(10, 20, 40), temps)
        assertEquals(listOf("p10", "p20", "top", "new"), profiles)
    }

    @Test
    fun `adding to an empty table creates the first range`() {
        val (temps, profiles) = updateRange(
            index = -1,
            temp = 25,
            profile = "only",
            rowTemps = emptyList(),
            rowProfiles = emptyList(),
        )
        assertEquals(listOf(25), temps)
        assertEquals(listOf("only", "only"), profiles)
    }
}
