package com.weberbox.pifire.core.repo.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.core.model.settings.ProbeProfile
import com.weberbox.pifire.core.network.model.settings.ProbeSettings

object ProbeProfilesMapper :
    Mapper<Map<String, ProbeSettings.ProbeProfile>?, Map<String, ProbeProfile>> {

    override fun map(from: Map<String, ProbeSettings.ProbeProfile>?): Map<String, ProbeProfile> = from?.mapValues { (_, value) ->
        ProbeProfileMapper.map(value)
    } ?: emptyMap()
}
