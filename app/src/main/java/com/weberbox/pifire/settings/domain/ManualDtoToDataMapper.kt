package com.weberbox.pifire.settings.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.settings.data.model.remote.ManualDto
import com.weberbox.pifire.settings.presentation.model.ManualData

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