package com.weberbox.pifire.core.model.settings

/** A change to one probe's name, profile or enabled state; null fields are left unchanged. */
data class ProbeInfoUpdate(
    val name: String? = null,
    val label: String? = null,
    val profileId: String? = null,
    val enabled: Boolean? = null,
)
