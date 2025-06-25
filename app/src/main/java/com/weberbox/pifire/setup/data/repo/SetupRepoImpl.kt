package com.weberbox.pifire.setup.data.repo

import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.common.data.parser.parseGetResponse
import com.weberbox.pifire.common.data.util.networkAvailable
import com.weberbox.pifire.common.presentation.util.getReasonPhrase
import com.weberbox.pifire.common.presentation.util.uiTextArgsOf
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.singleton.SocketManager
import com.weberbox.pifire.settings.domain.SettingsDtoToDataMapper
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.setup.data.api.SetupApi
import com.weberbox.pifire.setup.domain.VersionsDtoToDataMapper
import com.weberbox.pifire.setup.presentation.model.VersionsData
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.serialization.json.Json
import javax.inject.Inject

class SetupRepoImpl @Inject constructor(
    private val socketManager: SocketManager,
    private val setupApi: SetupApi,
    private val json: Json
): SetupRepo {

    override suspend fun getVersions(
        url: String,
        headerMap: Map<String, String>
    ): Result<VersionsData, DataError> {
        if (networkAvailable()) {
            try {
                setupApi.getVersions(url, headerMap).let { response ->
                    if (response.isSuccessful) {
                        return response.body()?.let { r ->
                            parseGetResponse(
                                response = r.string(),
                                json = json,
                                mapper = VersionsDtoToDataMapper
                            ).let { data ->
                                r.close()
                                when (data) {
                                    is Result.Error -> Result.Error(data.error)
                                    is Result.Success -> Result.Success(data.data)
                                }
                            }
                        } ?: Result.Error(DataError.Network.NULL_RESPONSE)
                    } else {
                        return if (response.code() == 401) {
                            Result.Error(DataError.Network.AUTHENTICATION)
                        } else {
                            Result.Error(
                                DataError.UiText(
                                    R.string.data_error_http_unsuccessful,
                                    uiTextArgsOf(
                                        response.code().toString(),
                                        getReasonPhrase(response.code())
                                    )
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                e.message?.let { message ->
                    return Result.Error(DataError.Server(message))
                }
                return Result.Error(DataError.Network.SERVER_ERROR)
            }
        } else {
            return Result.Error(DataError.Network.NO_CONNECTION)
        }
    }

    override suspend fun getSettingsData(
        server: Server,
    ): Result<Server, DataError> {
        return socketManager.initSocket(
            server = server,
            disconnectOnCancellation = true
        ).let { connected ->
            when (connected) {
                false -> Result.Error(DataError.Network.SOCKET_ERROR)
                true -> {
                    socketManager.emitGet(ServerConstants.GA_SETTINGS_DATA).let { result ->
                        when (result) {
                            is Result.Error -> Result.Error(result.error)
                            is Result.Success -> {
                                parseGetResponse(
                                    response = result.data,
                                    json = json,
                                    mapper = SettingsDtoToDataMapper
                                ).let { data ->
                                    when (data) {
                                        is Result.Error -> Result.Error(data.error)
                                        is Result.Success -> Result.Success(data.data)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}