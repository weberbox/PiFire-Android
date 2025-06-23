package com.weberbox.pifire.settings.domain

import com.weberbox.pifire.common.data.interfaces.DualItemMapper
import com.weberbox.pifire.settings.data.model.remote.ProbeSettings.ProbeMap.ProbeDevice
import com.weberbox.pifire.settings.data.model.remote.ProbeSettings.ProbeMap.ProbeInfo
import com.weberbox.pifire.settings.presentation.model.ProbeMap

object ProbeMapMapper : DualItemMapper<List<ProbeDevice>?, List<ProbeInfo>?, ProbeMap> {

    override fun map(
        inA: List<ProbeDevice>?,
        inB: List<ProbeInfo>?
    ): ProbeMap {
        inA?.also {
            inB?.also {

                val probeDevices = inA.map { device ->
                    ProbeMap.ProbeDevice(
                        device = device.device.orEmpty(),
                        module = device.module.orEmpty(),
                        ports = device.ports.orEmpty()
                    )
                }

                val probeInfo = inB.map { info ->
                    ProbeMap.ProbeInfo(
                        device = info.device.orEmpty(),
                        enabled = info.enabled != false,
                        label = info.label.orEmpty(),
                        name = info.name.orEmpty(),
                        port = info.port.orEmpty(),
                        type = info.type.orEmpty(),
                        profile = ProbeProfileMapper.map(info.profile)
                    )
                }

                return ProbeMap(
                    probeDevices = probeDevices,
                    probeInfo = probeInfo
                )
            }
        }
        return ProbeMap()
    }
}