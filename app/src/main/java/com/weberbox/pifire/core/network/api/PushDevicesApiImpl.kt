package com.weberbox.pifire.core.network.api

import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.common.model.andThen
import com.weberbox.pifire.core.common.model.map
import com.weberbox.pifire.core.network.model.push.PushDeviceDto
import com.weberbox.pifire.core.network.model.push.PushDeviceRegisterDto
import com.weberbox.pifire.core.network.transport.HttpSseTransport
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import javax.inject.Inject

class PushDevicesApiImpl @Inject constructor(
    private val transport: HttpSseTransport,
    private val json: Json,
) : PushDevicesApi {

    override suspend fun register(device: PushDeviceRegisterDto): Result<Unit, DataError> = transport.request(
        method = "POST",
        path = "/push/devices",
        body = json.encodeToString(serializer = PushDeviceRegisterDto.serializer(), value = device),
    ).map { }

    override suspend fun list(): Result<List<PushDeviceDto>, DataError> = transport.request(
        method = "GET",
        path = "/push/devices",
    ).andThen { data ->
        try {
            Result.Success(
                data?.let { json.decodeFromJsonElement(ListSerializer(PushDeviceDto.serializer()), it) }.orEmpty(),
            )
        } catch (_: IllegalArgumentException) {
            Result.Error(DataError.Local.JSON_ERROR)
        }
    }

    override suspend fun remove(id: String): Result<Unit, DataError> = transport.request(
        method = "DELETE",
        path = "/push/devices/${id.encodePath()}",
    ).map { }

    override suspend fun removeOneSignalDevice(playerId: String): Result<Unit, DataError> = transport.request(
        method = "DELETE",
        path = "/push/onesignal/${playerId.encodePath()}",
    ).map { }

    private fun String.encodePath(): String = URLEncoder.encode(this, Charsets.UTF_8.name()).replace("+", "%20")
}
