package com.weberbox.pifire.settings.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.settings.data.model.remote.ProbeSettings
import com.weberbox.pifire.settings.presentation.model.ProbeProfile

object ProbeProfilesMapper :
    Mapper<Map<String, ProbeSettings.ProbeProfile>?, Map<String, ProbeProfile>> {

    override fun map(from: Map<String, ProbeSettings.ProbeProfile>?): Map<String, ProbeProfile> {
        return from?.mapValues { (_, value) ->
            ProbeProfileMapper.map(value)
        } ?: emptyMap()
    }
}