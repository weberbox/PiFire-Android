package com.weberbox.pifire.core.repo.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.core.model.settings.ProbeProfile
import com.weberbox.pifire.core.network.model.settings.ProbeSettings

object ProbeProfileMapper : Mapper<ProbeSettings.ProbeProfile?, ProbeProfile> {
    override fun map(from: ProbeSettings.ProbeProfile?): ProbeProfile {
        return ProbeProfile(
            id = from?.id.orEmpty(),
            name = from?.name.orEmpty()
        )
    }
}