package com.weberbox.pifire.feature.settings.data.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.core.model.settings.ProbeProfile
import com.weberbox.pifire.feature.settings.data.model.remote.ProbeSettings

object ProbeProfilesMapper :
    Mapper<Map<String, ProbeSettings.ProbeProfile>?, Map<String, ProbeProfile>> {

    override fun map(from: Map<String, ProbeSettings.ProbeProfile>?): Map<String, ProbeProfile> {
        return from?.mapValues { (_, value) ->
            ProbeProfileMapper.map(value)
        } ?: emptyMap()
    }
}