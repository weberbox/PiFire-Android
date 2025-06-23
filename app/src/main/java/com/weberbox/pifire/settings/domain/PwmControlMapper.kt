package com.weberbox.pifire.settings.domain

import com.weberbox.pifire.common.data.interfaces.DualItemMapper
import com.weberbox.pifire.settings.data.model.remote.Pwm.Profile
import com.weberbox.pifire.settings.presentation.model.PwmControl

object PwmControlMapper : DualItemMapper<List<Int>, List<Profile>, List<PwmControl>> {

    override fun map(
        inA: List<Int>?,
        inB: List<Profile>?
    ): List<PwmControl> {
        inA?.also {
            inB?.also {
                var pwmControlList = listOf<PwmControl>()

                for (i in inA.indices) {
                    pwmControlList = pwmControlList.plus(
                        PwmControl(
                            temp = inA[i],
                            dutyCycle = inB.getOrNull(i)?.dutyCycle ?: 100
                        )
                    )

                    if (i == inA.lastIndex && inB.size > i + 1) {
                        pwmControlList = pwmControlList.plus(
                            PwmControl(
                                temp = inA[i] + 1,
                                dutyCycle = inB[i + 1].dutyCycle ?: 100
                            )
                        )
                    }
                }

                return pwmControlList
            }
        }
        return emptyList()
    }
}