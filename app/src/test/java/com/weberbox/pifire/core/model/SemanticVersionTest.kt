package com.weberbox.pifire.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SemanticVersionTest {

    @Test
    fun `parses plain and prefixed versions`() {
        assertEquals(SemanticVersion(major = 1, minor = 10, patch = 0), SemanticVersion.fromString("1.10.0"))
        assertEquals(SemanticVersion(major = 1, minor = 10, patch = 0), SemanticVersion.fromString("v1.10.0"))
    }

    @Test
    fun `unreadable parts become 0 without shifting the others`() {
        assertEquals(SemanticVersion(major = 1, minor = 9, patch = 0), SemanticVersion.fromString("1.9.0-beta"))
        assertEquals(SemanticVersion(major = 1, minor = 0, patch = 0), SemanticVersion.fromString("1"))
        assertEquals(SemanticVersion(major = 0, minor = 0, patch = 0), SemanticVersion.fromString("garbage"))
    }

    @Test
    fun `compares by major, minor, then patch`() {
        assertTrue(SemanticVersion.fromString("1.10.0") > SemanticVersion.fromString("1.9.9"))
        assertTrue(SemanticVersion.fromString("2.0.0") > SemanticVersion.fromString("1.99.99"))
    }
}
