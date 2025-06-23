package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Platform(
    @SerialName("devices")
    val devices: Devices? = null,

    @SerialName("inputs")
    val inputs: Inputs? = null,

    @SerialName("outputs")
    val outputs: Outputs? = null,

    @SerialName("system")
    val system: System? = null,

    @SerialName("current")
    val current: String? = null,

    @SerialName("dc_fan")
    val dcFan: Boolean? = null,

    @SerialName("triggerlevel")
    val triggerLevel: String? = null,

    @SerialName("buttonslevel")
    val buttonsLevel: String? = null,

    @SerialName("standalone")
    val standalone: Boolean? = null,

    @SerialName("real_hw")
    val realHw: Boolean? = null,

    @SerialName("system_type")
    val systemType: String? = null,
) {

    @Serializable
    data class Devices(
        @SerialName("display")
        val display: Display? = null,

        @SerialName("distance")
        val distance: Distance? = null,

        @SerialName("input")
        val input: Input? = null
    ) {

        @Serializable
        data class Display(
            @SerialName("dc")
            val dc: Int? = null,

            @SerialName("led")
            val led: Int? = null,

            @SerialName("rst")
            val rst: Int? = null
        )

        @Serializable
        data class Distance(
            @SerialName("echo")
            val echo: Int? = null,

            @SerialName("trig")
            val trig: Int? = null
        )

        @Serializable
        data class Input(
            @SerialName("down_dt")
            val downDt: Int? = null,

            @SerialName("enter_sw")
            val enterSw: Int? = null,

            @SerialName("up_clk")
            val upClk: Int? = null
        )
    }

    @Serializable
    data class Inputs(
        @SerialName("selector")
        val selector: Int? = null,

        @SerialName("shutdown")
        val shutdown: Int? = null
    )

    @Serializable
    data class Outputs(
        @SerialName("auger")
        val auger: Int? = null,

        @SerialName("dc_fan")
        val dcFan: Int? = null,

        @SerialName("fan")
        val fan: Int? = null,

        @SerialName("igniter")
        val igniter: Int? = null,

        @SerialName("power")
        val power: Int? = null,

        @SerialName("pwm")
        val pwm: Int? = null
    )

    @Serializable
    data class System(
        @SerialName("SPI0")
        val spi0: Spi0? = null,
    ) {

        @Serializable
        data class Spi0(
            @SerialName("CE0")
            val ce0: Int? = null,

            @SerialName("CE1")
            val ce1: Int? = null
        )
    }
}
