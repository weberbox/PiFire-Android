package com.weberbox.pifire.core.singleton

import com.weberbox.pifire.common.data.interfaces.Analytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoOpAnalyticsTracker @Inject constructor() : Analytics {

    override fun logEvent(name: String, params: Map<String, Any>?) {
        // no-op
    }

    override fun setUserId(userId: String?) {
        // no-op
    }

    override fun setUserProperty(name: String, value: String?) {
        // no-op
    }
}