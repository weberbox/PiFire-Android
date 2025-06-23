package com.weberbox.pifire.settings.data.model.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SettingsDto(
    @SerialName("server_info")
    val serverInfo: ServerInfo? = null,

    @SerialName("versions")
    val versions: Versions? = null,

    @SerialName("probe_settings")
    val probeSettings: ProbeSettings? = null,

    @SerialName("globals")
    val globals: Globals? = null,

    @SerialName("notify_services")
    val notifyServices: NotifyServices? = null,

    @SerialName("cycle_data")
    val cycleData: CycleData? = null,

    @SerialName("dashboard")
    var dashboard : Dashboard? = null,

    @SerialName("controller")
    val controller: Controller? = null,

    @SerialName("keep_warm")
    val keepWarm: KeepWarm? = null,

    @SerialName("smoke_plus")
    val smokePlus: SmokePlus? = null,

    @SerialName("safety")
    val safety: Safety? = null,

    @SerialName("startup")
    val startup: Startup? = null,

    @SerialName("shutdown")
    val shutdown: Shutdown? = null,

    @SerialName("pelletlevel")
    val pelletLevel: PelletLevel? = null,

    @SerialName("modules")
    val modules: Modules? = null,

    @SerialName("pwm")
    val pwm: Pwm? = null,

    @SerialName("platform")
    val platform: Platform? = null
)
