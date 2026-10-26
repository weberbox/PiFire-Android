package com.weberbox.pifire.core.notification

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remembers recently shown alert ids, so an alert arriving through both FCM and the live stream
 * (or a PiFire retry) shows once. In memory: both paths deliver into the same process.
 */
@Singleton
class AlertDeduper @Inject constructor() {

    private val seen = object : LinkedHashMap<String, Unit>() {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Unit>?): Boolean = size > MAX_REMEMBERED
    }

    /** True the first time [alertId] is seen. */
    @Synchronized
    fun isNew(alertId: String): Boolean = seen.put(alertId, Unit) == null

    private companion object {
        const val MAX_REMEMBERED = 100
    }
}
