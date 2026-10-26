package com.weberbox.pifire.core.model.push

import kotlinx.serialization.Serializable

/**
 * This phone's push relay registration. The push key authorizes sends to this phone, so it's
 * stored encrypted and never logged. Empty until the app first needs relay push.
 */
@Serializable
data class PushRegistration(
    val pushKey: String = "",
    /** The FCM token the relay has for [pushKey]. */
    val fcmToken: String = "",
)
