package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Controller(
    @SerialName("config")
    val config: Config? = null,

    @SerialName("selected")
    val selected: String? = null
) {

    @Serializable
    data class Config(
        @SerialName("pid")
        val pid: Pid? = null,

        @SerialName("pid_ac")
        val pidAc: PidAc? = null,

        @SerialName("pid_sp")
        val pidSp: PidSp? = null
    ) {

        @Serializable
        data class Pid(
            @SerialName("PB")
            val pb: Double? = null,

            @SerialName("Td")
            val td: Double? = null,

            @SerialName("Ti")
            val ti: Double? = null,

            @SerialName("center")
            val center: Double? = null
        )

        @Serializable
        data class PidAc(
            @SerialName("PB")
            val pb: Double? = null,

            @SerialName("Td")
            val td: Double? = null,

            @SerialName("Ti")
            val ti: Double? = null,

            @SerialName("center_factor")
            val centerFactor: Double? = null,

            @SerialName("stable_window")
            val stableWindow: Int? = null,
        )

        @Serializable
        data class PidSp(
            @SerialName("PB")
            val pb: Double? = null,

            @SerialName("Td")
            val td: Double? = null,

            @SerialName("Ti")
            val ti: Double? = null,

            @SerialName("center_factor")
            val centerFactor: Double? = null,

            @SerialName("stable_window")
            val stableWindow: Int? = null,

            @SerialName("tau")
            val tau: Int? = null,

            @SerialName("theta")
            val theta: Int? = null
        )

    }

}
