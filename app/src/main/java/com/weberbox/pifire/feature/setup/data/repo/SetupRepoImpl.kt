package com.weberbox.pifire.feature.setup.data.repo

import androidx.datastore.core.DataStore
import com.weberbox.pifire.R
import com.weberbox.pifire.core.annotations.Compatibility
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.constants.ServerConstants
import com.weberbox.pifire.core.model.settings.SettingsData
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.core.network.SocketManager
import com.weberbox.pifire.core.network.api.SettingsApi
import com.weberbox.pifire.core.network.headers.HeadersData
import com.weberbox.pifire.core.network.headers.HeadersData.Headers
import com.weberbox.pifire.core.network.networkAvailable
import com.weberbox.pifire.core.network.parseGetResponse
import com.weberbox.pifire.core.network.parseResponse
import com.weberbox.pifire.core.network.toNetworkError
import com.weberbox.pifire.core.repo.mapper.SettingsDtoToDataMapper
import com.weberbox.pifire.core.ui.util.getReasonPhrase
import com.weberbox.pifire.core.ui.util.uiTextArgsOf
import com.weberbox.pifire.feature.setup.data.api.SetupApi
import com.weberbox.pifire.feature.setup.data.mapper.LegacyVersionsDtoToDataMapper
import com.weberbox.pifire.feature.setup.data.mapper.VersionsDtoToDataMapper
import com.weberbox.pifire.feature.setup.data.model.VersionsData
import com.weberbox.pifire.feature.setup.data.util.ServerDataCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class SetupRepoImpl @Inject constructor(
    private val socketManager: SocketManager,
    private val setupApi: SetupApi,
    private val settingsApi: SettingsApi,
    private val settingsDataStore: DataStore<SettingsData>,
    private val headersDataStore: DataStore<HeadersData>,
    private val serverDataCache: ServerDataCache,
    private val json: Json,
) : SetupRepo {

    override suspend fun getVersions(
        url: String,
        headerMap: Map<String, String>,
    ): Result<VersionsData, DataError> = withContext(Dispatchers.IO) {
        if (networkAvailable()) {
            try {
                setupApi.getVersions(url, headerMap).let { response ->
                    if (response.isSuccessful) {
                        return@withContext response.body()?.let { r ->
                            parseGetResponse(
                                response = r.string(),
                                json = json,
                                mapper = VersionsDtoToDataMapper,
                            ).let { data ->
                                r.close()
                                checkLegacyResponse(
                                    url = url,
                                    headerMap = headerMap,
                                    data = data,
                                )
                            }
                        } ?: Result.Error(DataError.Network.NULL_RESPONSE)
                    } else {
                        return@withContext if (response.code() == 401) {
                            Result.Error(DataError.Network.AUTHENTICATION)
                        } else {
                            Result.Error(
                                DataError.UiText(
                                    R.string.data_error_http_unsuccessful,
                                    uiTextArgsOf(
                                        response.code().toString(),
                                        getReasonPhrase(response.code()),
                                    ),
                                ),
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                return@withContext Result.Error(e.toNetworkError())
            }
        } else {
            return@withContext Result.Error(DataError.Network.NO_CONNECTION)
        }
    }

    @Compatibility(versionBelow = "1.10.0", build = "25")
    override suspend fun getVersionsLegacy(
        url: String,
        headerMap: Map<String, String>,
    ): Result<VersionsData, DataError> = withContext(Dispatchers.IO) {
        if (networkAvailable()) {
            try {
                setupApi.getVersions(url, headerMap).let { response ->
                    if (response.isSuccessful) {
                        return@withContext response.body()?.let { r ->
                            parseResponse(
                                response = r.string(),
                                json = json,
                                mapper = LegacyVersionsDtoToDataMapper,
                            ).let { data ->
                                r.close()
                                when (data) {
                                    is Result.Error -> Result.Error(data.error)
                                    is Result.Success -> Result.Success(data.data)
                                }
                            }
                        } ?: Result.Error(DataError.Network.NULL_RESPONSE)
                    } else {
                        return@withContext if (response.code() == 401) {
                            Result.Error(DataError.Network.AUTHENTICATION)
                        } else {
                            Result.Error(
                                DataError.UiText(
                                    R.string.data_error_http_unsuccessful,
                                    uiTextArgsOf(
                                        response.code().toString(),
                                        getReasonPhrase(response.code()),
                                    ),
                                ),
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                return@withContext Result.Error(e.toNetworkError())
            }
        } else {
            return@withContext Result.Error(DataError.Network.NO_CONNECTION)
        }
    }

    override suspend fun getSettingsData(
        server: Server,
    ): Result<Server, DataError> = withContext(Dispatchers.IO) {
        socketManager.initSocket(
            server = server,
            disconnectOnCancellation = true,
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
                                    mapper = SettingsDtoToDataMapper,
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

    @Compatibility(versionBelow = "1.10.0", build = "25")
    private suspend fun checkLegacyResponse(
        url: String,
        headerMap: Map<String, String>,
        data: Result<VersionsData, DataError>,
    ): Result<VersionsData, DataError> = when (data) {
        is Result.Error -> {
            when (data.error) {
                is DataError.Server -> {
                    getVersionsLegacy(
                        url = url.replace(
                            oldValue = "get/versions",
                            newValue = "settings",
                        ),
                        headerMap = headerMap,
                    )
                }

                else -> Result.Error(data.error)
            }
        }

        is Result.Success -> Result.Success(data.data)
    }

    override suspend fun completeSetup(
        server: Server,
        headers: Headers,
    ): Result<Unit, DataError> = withContext(Dispatchers.IO) {
        when (val result = settingsApi.setGrillName(server.name)) {
            is Result.Error -> Result.Error(result.error)

            is Result.Success -> try {
                settingsDataStore.updateData { data ->
                    data.copy(serverMap = data.serverMap.plus(server.uuid to server))
                }
                headersDataStore.updateData { data ->
                    data.copy(headersMap = data.headersMap.plus(server.uuid to headers))
                }
                serverDataCache.clearServerData()
                Result.Success(Unit)
            } catch (e: IOException) {
                Timber.e(e, "Failed saving the new server")
                Result.Error(DataError.Local.UNKNOWN)
            }
        }
    }
}
