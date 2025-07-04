package com.weberbox.pifire.settings.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.settings.data.model.remote.ProbeSettings
import com.weberbox.pifire.settings.presentation.model.ProbeProfile

object ProbeProfileMapper : Mapper<ProbeSettings.ProbeProfile?, ProbeProfile> {
    override fun map(from: ProbeSettings.ProbeProfile?): ProbeProfile {
        return ProbeProfile(
            id = from?.id.orEmpty(),
            name = from?.name.orEmpty()
        )
    }
}