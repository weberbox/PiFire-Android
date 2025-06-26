package com.weberbox.pifire.setup.data.model

import com.weberbox.pifire.core.annotations.Compatibility
import kotlinx.serialization.Serializable

@Compatibility(versionBelow = "1.10.0", build = "25")
@Serializable
data class LegacyVersionsDto(
    val settings: Settings? = null
) {
    @Serializable
    data class Settings(
        val versions: Versions? = null,
    ) {
        @Serializable
        data class Versions(
            val server: String? = null,
            val build: String? = null
        )
    }
}