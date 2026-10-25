package com.weberbox.pifire.feature.pellets.data.mapper

import com.weberbox.pifire.core.di.JsonModule
import com.weberbox.pifire.feature.pellets.data.model.PelletsDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PelletsDtoToDataMapperTest {

    private val json = JsonModule.provideJson()

    private fun map(body: String) = PelletsDtoToDataMapper.map(json.decodeFromString<PelletsDto>(body))

    @Test
    fun `the current pellets come from their archived profile`() {
        val pellets = map(
            """
            {
              "uuid": "server-1",
              "pellets": {
                "archive": {
                  "p1": {"brand": "Brand", "wood": "Hickory", "rating": 5, "comments": "Good", "id": "p1"}
                },
                "current": {"date_loaded": "2024-05-01 10:00:00", "est_usage": 1500, "hopper_level": 80, "pelletid": "p1"},
                "brands": ["Brand"],
                "woods": ["Hickory"],
                "log": {"2024-05-01 10:00:00": "p1"},
                "added_in_a_newer_server": true
              }
            }
            """,
        )
        assertEquals("server-1", pellets.uuid)
        assertEquals("Brand", pellets.currentBrand)
        assertEquals("Hickory", pellets.currentWood)
        assertEquals(5, pellets.currentRating)
        assertEquals(1500.0, pellets.estUsageGrams, 0.0)
        assertEquals(80, pellets.hopperLevel)
        assertEquals("Brand Hickory", pellets.logsList.single().pelletName)
        assertEquals("p1", pellets.profilesList.single().id)
    }

    @Test
    fun `a log entry for a deleted profile has no name rather than null text`() {
        val pellets = map(
            """
            {"pellets": {"archive": {}, "log": {"2024-05-01 10:00:00": "deleted"}}}
            """,
        )
        assertEquals("", pellets.logsList.single().pelletName)
    }

    @Test
    fun `a missing log or archive is empty, not a placeholder entry`() {
        val pellets = map("""{"pellets": {"current": {"hopper_level": 10}}}""")
        assertTrue(pellets.logsList.isEmpty())
        assertTrue(pellets.profilesList.isEmpty())
        assertEquals(10, pellets.hopperLevel)
    }
}
