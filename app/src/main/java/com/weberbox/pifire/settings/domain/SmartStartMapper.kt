package com.weberbox.pifire.settings.domain

import com.weberbox.pifire.common.data.interfaces.DualItemMapper
import com.weberbox.pifire.settings.data.model.remote.Startup
import com.weberbox.pifire.settings.presentation.model.SmartStart

object SmartStartMapper :
    DualItemMapper<List<Int>?,  List<Startup.SmartStart.SSProfile>?, List<SmartStart>> {

    override fun map(
        inA: List<Int>?,
        inB: List<Startup.SmartStart.SSProfile>?
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
                            pMode = inB[i].pMode ?: 2
                        )
                    )

                    if (i == inA.lastIndex) {
                        smartStartList = smartStartList.plus(
                            SmartStart(
                                temp = inA[inA.lastIndex] + 1,
                                startUp = inB[i + 1].startUpTime ?: 360,
                                augerOn = inB[i + 1].augerOnTime ?: 15,
                                pMode = inB[i + 1].pMode ?: 2
                            )
                        )
                    }
                }

                return smartStartList
            }
        }
        return emptyList()
    }
}