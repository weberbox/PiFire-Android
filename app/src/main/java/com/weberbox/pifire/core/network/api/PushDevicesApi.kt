package com.weberbox.pifire.core.network.api

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.network.model.push.PushDeviceDto
import com.weberbox.pifire.core.network.model.push.PushDeviceRegisterDto

/** Push devices on the connected PiFire server; API v2 only (docs/V2_API.md section 6). */
interface PushDevicesApi {
    suspend fun register(device: PushDeviceRegisterDto): Result<Unit, DataError>
    suspend fun list(): Result<List<PushDeviceDto>, DataError>
    suspend fun remove(id: String): Result<Unit, DataError>

    /** Removes a OneSignal entry this phone left from when it used Socket.IO. */
    suspend fun removeOneSignalDevice(playerId: String): Result<Unit, DataError>
}
