package com.weberbox.pifire.settings.data.model.remote

import com.weberbox.pifire.settings.data.serializer.IntOrDoubleSerializer
import kotlinx.serialization.Serializable

@Serializable
data class ManualDto(
    val manual: Manual? = null,
    val active: Boolean? = null,
    val dcFan: Boolean? = null
) {
    @Serializable
    data class Manual(
        val output: String? = null,
        val change: String? = null,
        val fan: Boolean? = null,
        val auger: Boolean? = null,
        val igniter: Boolean? = null,
        val power: Boolean? = null,

        @Serializable(with = IntOrDoubleSerializer::class)
        val pwm: Int? = null
    )
}
