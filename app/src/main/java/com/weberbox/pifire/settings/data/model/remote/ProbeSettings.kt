package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProbeSettings(
    @SerialName("probe_map")
    val probeMap: ProbeMap? = null,

    @SerialName("probe_profiles")
    val probeProfiles: Map<String, ProbeProfile>? = null
) {

    @Serializable
    data class ProbeProfile(
        @SerialName("A")
        val a: Double? = null,

        @SerialName("B")
        val b: Double? = null,

        @SerialName("C")
        val c: Double? = null,

        @SerialName("id")
        val id: String? = null,

        @SerialName("name")
        val name: String? = null
    )

    @Serializable
    data class ProbeMap(
        @SerialName("probe_devices")
        val probeDevices: List<ProbeDevice>? = null,

        @SerialName("probe_info")
        val probeInfo: List<ProbeInfo>? = null
    ) {

        @Serializable
        data class ProbeInfo(
            @SerialName("device")
            val device: String? = null,

            @SerialName("enabled")
            val enabled: Boolean? = null,

            @SerialName("label")
            val label: String? = null,

            @SerialName("name")
            val name: String? = null,

            @SerialName("port")
            val port: String? = null,

            @SerialName("type")
            val type: String? = null,

            @SerialName("profile")
            val profile: ProbeProfile? = null
        )

        @Serializable
        data class ProbeDevice(
            @SerialName("config")
            val config: Config? = null,

            @SerialName("device")
            val device: String? = null,

            @SerialName("module")
            val module: String? = null,

            @SerialName("ports")
            val ports: List<String>? = null
        ) {

            @Serializable
            data class Config(
                @SerialName("ADC0_rd")
                val adc0Rd: String? = null,

                @SerialName("ADC1_rd")
                val adc1Rd: String? = null,

                @SerialName("ADC2_rd")
                val adc2Rd: String? = null,

                @SerialName("ADC3_rd")
                val adc3Rd: String? = null,

                @SerialName("i2c_bus_addr")
                val i2cBusAddr: String? = null,

                @SerialName("transient")
                val transient: String? = null,

                @SerialName("voltage_ref")
                val voltageRef: String? = null,

                @SerialName("cs")
                val cs: String? = null,

                @SerialName("ref_resistor")
                val refResistor: String? = null,

                @SerialName("rtd_nominal")
                val rtdNominal: String? = null,

                @SerialName("wires")
                val wires: String? = null,

                @SerialName("probes_list")
                val probesList: List<String>? = null,
            )
        }
    }
}

