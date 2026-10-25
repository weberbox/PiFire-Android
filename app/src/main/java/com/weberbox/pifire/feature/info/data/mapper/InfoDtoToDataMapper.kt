package com.weberbox.pifire.feature.info.data.mapper

import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.core.annotations.Compatibility
import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.model.info.GPIODeviceData
import com.weberbox.pifire.core.model.info.GPIOInOutData
import com.weberbox.pifire.core.model.info.InfoData.Info
import com.weberbox.pifire.core.ui.util.getFormattedDate
import com.weberbox.pifire.feature.info.data.model.InfoDto
import java.util.Locale

object InfoDtoToDataMapper : Mapper<InfoDto, Info> {

    override fun map(from: InfoDto): Info {
        val gpioInPins = ArrayList<GPIOInOutData>()
        val gpioOutPins = ArrayList<GPIOInOutData>()
        val gpioDevPins = ArrayList<GPIODeviceData>()

        from.outPins?.forEach { (key: String, value: Int?) ->
            val pin = value?.toString() ?: "-"
            if (key.contains("dc_fan") || key.contains("pwm")) {
                from.dcFan?.also { dcFan ->
                    if (dcFan) {
                        gpioOutPins.add(
                            GPIOInOutData(
                                name = key.replace("_", " "),
                                pin = pin,
                            ),
                        )
                    }
                }
            } else {
                gpioOutPins.add(
                    GPIOInOutData(
                        name = key.replace("_", " "),
                        pin = pin,
                    ),
                )
            }
        }

        from.inPins?.forEach { (key: String, value: Int?) ->
            val pin = value?.toString() ?: "-"
            if (key.contains("dc_fan") || key.contains("pwm")) {
                from.dcFan?.also { dcFan ->
                    if (dcFan) {
                        gpioInPins.add(
                            GPIOInOutData(
                                name = key.replace("_", " "),
                                pin = pin,
                            ),
                        )
                    }
                }
            } else {
                gpioInPins.add(
                    GPIOInOutData(
                        name = key.replace("_", " "),
                        pin = pin,
                    ),
                )
            }
        }

        from.devPins?.forEach { (device: String, functions: Map<String, Int?>?) ->
            functions?.entries?.forEachIndexed { index, (function: String, pin: Int?) ->
                gpioDevPins.add(
                    GPIODeviceData(
                        // Only a device's first row shows its name
                        name = if (index == 0) device.replaceFirstChar { it.uppercase() } else "",
                        function = function.replace("_", " "),
                        pin = pin?.toString() ?: "-",
                    ),
                )
            }
        }

        @Compatibility(versionBelow = "1.10.0", build = "37")
        var cpuSeparated: Array<String>
        var cpuInfo = String()
        from.cpuInfo?.forEach { cpu ->
            if (cpu.contains("model name")) {
                cpuSeparated =
                    cpu.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                cpuInfo = cpuSeparated[1].trim()
            }
        }

        @Compatibility(versionBelow = "1.10.0", build = "37")
        var inetSeparated: Array<String?>
        val networkString = StringBuilder()
        from.ifConfig?.forEach { network ->
            if (network.contains("netmask")) {
                if (!network.contains("127.0.0.1")) {
                    inetSeparated = network.trim().split(" ".toRegex())
                        .dropLastWhile { it.isEmpty() }.toTypedArray()
                    networkString.append("Address:    ").append(inetSeparated[1]).append("\n\n")
                    networkString.append("Netmask:    ").append(inetSeparated[4]).append("\n\n")
                    networkString.append("Broadcast:    ").append(inetSeparated[7])
                }
            }
        }

        return Info(
            uuid = from.uuid.orEmpty(),
            platformInfo = Info.PlatformInfo(
                systemModel = from.platformInfo?.systemModel.orEmpty(),
                cpuModel = from.platformInfo?.cpuModel.orEmpty(),
                cpuHardware = from.platformInfo?.cpuHardware.orEmpty(),
                cpuCores = from.platformInfo?.cpuCores?.toString().orEmpty(),
                cpuFrequency = from.platformInfo?.cpuFrequency?.toString().orEmpty(),
                totalRamBytes = from.platformInfo?.totalRam,
                availableRamBytes = from.platformInfo?.availableRam,
            ),
            osInfo = Info.OsInfo(
                prettyName = from.osInfo?.prettyName.orEmpty(),
                version = from.osInfo?.version.orEmpty(),
                codeName = from.osInfo?.codeName.orEmpty(),
                architecture = from.osInfo?.architecture.orEmpty(),
            ),
            networkMap = from.networkInfo
                ?.filterKeys { it != "lo" }
                ?.mapValues { (_, value) ->
                    Info.NetworkInfo(
                        ipAddress = value.ipAddress.orEmpty(),
                        macAddress = value.macAddress.orEmpty(),
                    )
                } ?: emptyMap(),
            wifiQualityValue = from.wifiQualityValue?.toString().orEmpty(),
            wifiQualityMax = from.wifiQualityMax?.toString().orEmpty(),
            wifiQualityPercent = from.wifiQualityPercentage?.takeIf { it.isFinite() },
            cpuThrottled = from.cpuThrottled ?: false,
            cpuUnderVolt = from.cpuUnderVolt ?: false,
            cpuInfo = cpuInfo,
            networkInfo = networkString.toString(),
            uptime = from.upTime?.trim().orEmpty(),
            cpuTemp = from.cpuTemp.orEmpty(),
            outPins = gpioOutPins,
            inPins = gpioInPins,
            devPins = gpioDevPins,
            pipList = PipListMapper.map(from.pipList),
            platform = from.platform.orEmpty(),
            display = from.display.orEmpty(),
            distance = from.distance.orEmpty(),
            serverVersion = from.serverVersion.orEmpty(),
            serverBuild = from.serverBuild.orEmpty(),
            appVersion = BuildConfig.VERSION_NAME,
            appVersionCode = String.format(Locale.US, BuildConfig.VERSION_CODE.toString()),
            appBuildType = BuildConfig.BUILD_TYPE,
            appFlavor = BuildConfig.FLAVOR,
            appBuildDate = getFormattedDate(BuildConfig.BUILD_TIME, "MM-dd-yy HH:mm"),
            appGitBranch = BuildConfig.GIT_BRANCH,
            appGitRev = BuildConfig.GIT_REV,
            alphaBuild = BuildConfig.ALPHA_BUILD,
            debugBuild = AppConfig.DEBUG,
        )
    }
}
