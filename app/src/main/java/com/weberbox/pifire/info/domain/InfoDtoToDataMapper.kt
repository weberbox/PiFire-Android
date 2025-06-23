package com.weberbox.pifire.info.domain

import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.common.presentation.util.getFormattedDate
import com.weberbox.pifire.info.data.model.InfoDto
import com.weberbox.pifire.info.presentation.model.GPIODeviceData
import com.weberbox.pifire.info.presentation.model.GPIOInOutData
import com.weberbox.pifire.info.presentation.model.InfoData.Info
import java.util.Locale
import java.util.concurrent.atomic.AtomicInteger

object InfoDtoToDataMapper : Mapper<InfoDto, Info> {

    override fun map(from: InfoDto): Info {
        val gpioInPins = ArrayList<GPIOInOutData>()
        val gpioOutPins = ArrayList<GPIOInOutData>()
        val gpioDevPins = ArrayList<GPIODeviceData>()
        val cycle = AtomicInteger()

        from.outPins?.forEach { (key: String, value: Int?) ->
            val pin = value?.toString() ?: ""
            if (key.contains("dc_fan") || key.contains("pwm")) {
                from.dcFan?.also {
                    gpioOutPins.add(
                        GPIOInOutData(
                            name = key.replace("_", " "),
                            pin = pin
                        )
                    )
                }
            } else {
                gpioOutPins.add(
                    GPIOInOutData(
                        name = key.replace("_", " "),
                        pin = pin
                    )
                )
            }
        }

        from.inPins?.forEach { (key: String, value: Int?) ->
            val pin = value?.toString() ?: ""
            if (key.contains("dc_fan") || key.contains("pwm")) {
                from.dcFan?.also {
                    gpioInPins.add(
                        GPIOInOutData(
                            name = key.replace("_", " "),
                            pin = pin
                        )
                    )
                }
            } else {
                gpioInPins.add(
                    GPIOInOutData(
                        name = key.replace("_", " "),
                        pin = pin
                    )
                )
            }
        }

        from.devPins?.forEach { (key: String, value: Map<String, Int>) ->
            cycle.getAndSet(1)
            value.forEach { (function: String, pin: Int) ->
                if (cycle.toInt() == 1) {
                    cycle.getAndIncrement()
                    val devPin = String.format(Locale.US, pin.toString())
                    val capName = key.replaceFirstChar { firstChar -> firstChar.uppercase() }
                    gpioDevPins.add(
                        GPIODeviceData(
                            name = capName,
                            function = function.replace("_", " "),
                            pin = devPin
                        )
                    )
                } else {
                    val devPin = String.format(Locale.US, pin.toString())
                    gpioDevPins.add(
                        GPIODeviceData(
                            name = "",
                            function = function.replace("_", " "),
                            pin = devPin
                        )
                    )
                }
                cycle.getAndSet(0)
            }
        }

        var cpuSeparated: Array<String>
        var cpuInfo = String()
        from.cpuInfo?.forEach { cpu ->
            if (cpu.contains("model name")) {
                cpuSeparated =
                    cpu.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                cpuInfo = cpuSeparated[1].trim()
            }
        }

        var inetSeparated: Array<String?>
        val networkString = StringBuilder()
        from.ifConfig?.forEach { network ->
            if (network.contains("netmask")) {
                if (!network.contains("127.0.0.1")) {
                    inetSeparated = network.trim().split(" ".toRegex())
                        .dropLastWhile { it.isEmpty() }.toTypedArray()
                    networkString.append("Address:    ").append(inetSeparated[1]).append("\n\n")
                    networkString.append("Netmask:    ").append(inetSeparated[4]).append("\n\n")
                    networkString.append("Broadcast:    ").append(inetSeparated[7]).append("\n")
                }
            }
        }

        return Info(
            uuid = from.uuid.orEmpty(),
            cpuInfo = cpuInfo,
            networkInfo = networkString.toString(),
            uptime = from.upTime?.trim().orEmpty(),
            cpuTemp = from.cpuTemp.orEmpty(),
            outPins = gpioOutPins,
            inPins = gpioInPins,
            devPins = gpioDevPins,
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
            appGitRev = BuildConfig.GIT_REV
        )
    }
}

