package com.weberbox.pifire.feature.landing.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class ServerData(
    val servers: List<Server> = emptyList(),
) {
    @Serializable
    data class Server(
        val uuid: String = "",
        val name: String = "",
        // 1-based position, for the default name when the server has none
        val number: Int = 0,
        val address: String = "",
        val online: Boolean = false,
    )
}
