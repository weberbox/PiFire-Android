package com.weberbox.pifire.landing.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class ServerData(
    val servers: List<Server> = emptyList()
) {
    @Serializable
    data class Server(
        val uuid: String = "",
        val name: String = "",
        val address: String = "",
        val online: Boolean = false
    )
}