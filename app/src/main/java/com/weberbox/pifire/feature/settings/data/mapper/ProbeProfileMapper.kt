package com.weberbox.pifire.feature.settings.data.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.feature.settings.data.model.remote.ProbeSettings
import com.weberbox.pifire.feature.settings.presentation.model.ProbeProfile

object ProbeProfileMapper : Mapper<ProbeSettings.ProbeProfile?, ProbeProfile> {
    override fun map(from: ProbeSettings.ProbeProfile?): ProbeProfile {
        return ProbeProfile(
            id = from?.id.orEmpty(),
            name = from?.name.orEmpty()
        )
    }
}