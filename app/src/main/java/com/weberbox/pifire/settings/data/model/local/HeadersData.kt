package com.weberbox.pifire.settings.data.model.local

import com.weberbox.pifire.common.domain.Uuid
import kotlinx.serialization.Serializable

@Serializable
data class HeadersData(
    val headersMap: Map<Uuid, Headers> = emptyMap()
) {
    @Serializable
    data class Headers(
        val credentials: BasicAuth = BasicAuth(),
        val extraHeaders: List<ExtraHeader> = emptyList()
    ) {
        @Serializable
        data class BasicAuth(
            val user: String = "",
            val pass: String = ""
        )

        @Serializable
        data class ExtraHeader(
            var key: String = "",
            var value: String = ""
        )
    }
}
