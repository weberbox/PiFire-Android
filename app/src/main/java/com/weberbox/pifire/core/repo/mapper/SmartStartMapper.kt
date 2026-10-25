package com.weberbox.pifire.core.repo.mapper

import com.weberbox.pifire.core.common.model.DualItemMapper
import com.weberbox.pifire.core.model.settings.SmartStart
import com.weberbox.pifire.core.network.model.settings.Startup

object SmartStartMapper :
    DualItemMapper<List<Int>?, List<Startup.SmartStart.SSProfile>?, List<SmartStart>> {

    override fun map(
        inA: List<Int>?,
        inB: List<Startup.SmartStart.SSProfile>?,
    ): List<SmartStart> {
        inA?.also {
            inB?.also {
                var smartStartList = listOf<SmartStart>()

                for (i in inA.indices) {
                    smartStartList = smartStartList.plus(
                        SmartStart(
                            temp = inA[i],
                            startUp = inB[i].startUpTime ?: 360,
                            augerOn = inB[i].augerOnTime ?: 15,
                            pMode = inB[i].pMode ?: 2,
                        ),
                    )

                    if (i == inA.lastIndex) {
                        smartStartList = smartStartList.plus(
                            SmartStart(
                                temp = inA[inA.lastIndex] + 1,
                                startUp = inB[i + 1].startUpTime ?: 360,
                                augerOn = inB[i + 1].augerOnTime ?: 15,
                                pMode = inB[i + 1].pMode ?: 2,
                            ),
                        )
                    }
                }

                return smartStartList
            }
        }
        return emptyList()
    }
}
