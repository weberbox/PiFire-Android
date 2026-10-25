package com.weberbox.pifire.core.common.util

import org.junit.Assert.assertEquals
import org.junit.Test

class UniqueKeysTest {

    @Test
    fun `keys are the ids, with repeats numbered`() {
        val keys = listOf("Alder", "Hickory", "Alder", "Alder").withUniqueKeys { it }.map { it.key }
        assertEquals(listOf("Alder", "Hickory", "Alder#1", "Alder#2"), keys)
    }

    @Test
    fun `values keep their order`() {
        val values = listOf(3, 1, 2).withUniqueKeys { it }.map { it.value }
        assertEquals(listOf(3, 1, 2), values)
    }
}
