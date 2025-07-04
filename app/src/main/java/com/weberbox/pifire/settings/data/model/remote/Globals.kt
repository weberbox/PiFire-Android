package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Globals(
    @SerialName("grill_name")
    val grillName: String? = null,

    @SerialName("debug_mode")
    val debugMode: Boolean? = null,

    @SerialName("prime_ignition")
    val primeIgnition: Boolean? = null,

    @SerialName("boot_to_monitor")
    val bootToMonitor: Boolean? = null,

    @SerialName("units")
    val units: String? = null,

    @SerialName("augerrate")
    val augerRate: Double? = null,

    @SerialName("first_time_setup")
    val firstTimeSetup: Boolean? = null,

    @SerialName("ext_data")
    val extData: Boolean? = null,

    @SerialName("updated_message")
    val updatedMessage: Boolean? = null,

    @SerialName("venv")
    val venv: Boolean? = null,

)
