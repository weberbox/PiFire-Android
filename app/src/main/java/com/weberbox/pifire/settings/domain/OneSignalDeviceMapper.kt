package com.weberbox.pifire.settings.domain

import com.weberbox.pifire.common.data.interfaces.Mapper
import com.weberbox.pifire.settings.data.model.remote.NotifyServices.OneSignalPush
import com.weberbox.pifire.settings.presentation.model.OneSignalPush.OneSignalDeviceInfo


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