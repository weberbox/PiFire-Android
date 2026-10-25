package com.weberbox.pifire.feature.settings.data.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.core.network.model.settings.ManualDto
import com.weberbox.pifire.feature.settings.data.model.ManualData

object ManualDtoToDataMapper : Mapper<ManualDto, ManualData> {

    override fun map(from: ManualDto): ManualData {
        return ManualData(
            active = from.active == true,
            fan = from.manual?.fan == true,
            auger = from.manual?.auger == true,
            igniter = from.manual?.igniter == true,
            power = from.manual?.power == true,
            pwm = from.manual?.pwm ?: 100,
            dcFan = from.dcFan == true
        )
    }

}