package com.weberbox.pifire.core.ui.util

import org.junit.Assert.assertEquals
import org.junit.Test

class PermissionStateTest {
    @Test
    fun `granted wins whatever the rationale says`() {
        assertEquals(PermissionOutcome.Granted, outcomeAfterAsking(granted = true, showRationale = true))
        assertEquals(PermissionOutcome.Granted, outcomeAfterAsking(granted = true, showRationale = false))
    }

    @Test
    fun `declined with a rationale due offers to ask again`() {
        assertEquals(PermissionOutcome.AskAgain, outcomeAfterAsking(granted = false, showRationale = true))
    }

    @Test
    fun `declined without a rationale due means only the settings can grant it`() {
        assertEquals(PermissionOutcome.OpenSettings, outcomeAfterAsking(granted = false, showRationale = false))
    }
}
