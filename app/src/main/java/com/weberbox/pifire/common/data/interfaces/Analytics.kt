package com.weberbox.pifire.common.data.interfaces

interface Analytics {
    fun logEvent(name: String, params: Map<String, Any>? = null)
    fun setUserId(userId: String?)
    fun setUserProperty(name: String, value: String?)
}