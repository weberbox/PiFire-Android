package com.weberbox.pifire.core.repo.mapper

import com.weberbox.pifire.core.common.model.DualItemMapper
import com.weberbox.pifire.core.model.settings.ProbeMap
import com.weberbox.pifire.core.network.model.settings.ProbeSettings.ProbeMap.ProbeDevice
import com.weberbox.pifire.core.network.model.settings.ProbeSettings.ProbeMap.ProbeInfo

object ProbeMapMapper : DualItemMapper<List<ProbeDevice>?, List<ProbeInfo>?, ProbeMap> {

    override fun map(
        inA: List<ProbeDevice>?,
        inB: List<ProbeInfo>?,
    ): ProbeMap {
        inA?.also {
            inB?.also {
                val probeDevices = inA.map { device ->
                    ProbeMap.ProbeDevice(
                        device = device.device.orEmpty(),
                        module = device.module.orEmpty(),
                        moduleFilename = device.moduleFilename.orEmpty(),
                        ports = device.ports.orEmpty(),
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
                        profile = ProbeProfileMapper.map(info.profile),
                    )
                }

                return ProbeMap(
                    probeDevices = probeDevices,
                    probeInfo = probeInfo,
                )
            }
        }
        return ProbeMap()
    }
}
