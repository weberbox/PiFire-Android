package com.weberbox.pifire.core.repo.mapper

import com.weberbox.pifire.core.common.model.Mapper
import com.weberbox.pifire.core.model.settings.OneSignalPush.OneSignalDeviceInfo
import com.weberbox.pifire.core.network.model.settings.NotifyServices.OneSignalPush


object OneSignalDeviceMapper :
    Mapper<Map<String, OneSignalPush.OneSignalDeviceInfo>?, Map<String, OneSignalDeviceInfo>> {

    override fun map(
        from: Map<String, OneSignalPush.OneSignalDeviceInfo>?): Map<String, OneSignalDeviceInfo> {
        return from?.mapValues { (_, value) ->
            OneSignalDeviceInfo(
                deviceName = value.deviceName.orEmpty(),
                friendlyName = value.friendlyName.orEmpty(),
                appVersion = value.appVersion.orEmpty()
            )
        } ?: emptyMap()
    }
}