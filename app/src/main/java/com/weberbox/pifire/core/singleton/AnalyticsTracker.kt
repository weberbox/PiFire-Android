package com.weberbox.pifire.core.singleton

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.weberbox.pifire.common.data.interfaces.Analytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsTracker @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : Analytics {

    override fun logEvent(name: String, params: Map<String, Any>?) {
        val bundle = Bundle().apply {
            params?.forEach { (k, v) ->
                when (v) {
                    is String -> putString(k, v)
                    is Int -> putInt(k, v)
                    is Long -> putLong(k, v)
                    is Double -> putDouble(k, v)
                    is Float -> putFloat(k, v)
                    is Boolean -> putString(k, v.toString())
                    else -> putString(k, v.toString())
                }
            }
        }
        firebaseAnalytics.logEvent(name, bundle)
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(name: String, value: String?) {
        firebaseAnalytics.setUserProperty(name, value)
    }
}