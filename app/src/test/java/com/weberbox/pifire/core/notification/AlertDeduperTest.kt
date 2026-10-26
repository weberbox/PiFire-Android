package com.weberbox.pifire.core.notification

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AlertDeduperTest {

    private val deduper = AlertDeduper()

    @Test
    fun `an alert id is new only once`() {
        assertTrue(deduper.isNew("alert-1"))
        assertFalse(deduper.isNew("alert-1"))
        assertTrue(deduper.isNew("alert-2"))
    }

    @Test
    fun `forgets the oldest ids past its limit`() {
        repeat(101) { deduper.isNew("alert-$it") }
        assertTrue(deduper.isNew("alert-0"))
        assertFalse(deduper.isNew("alert-100"))
    }
}
