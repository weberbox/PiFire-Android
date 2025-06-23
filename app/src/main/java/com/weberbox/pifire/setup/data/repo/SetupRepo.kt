package com.weberbox.pifire.setup.data.repo

import com.weberbox.pifire.common.data.interfaces.DataError
import com.weberbox.pifire.common.data.interfaces.Result
import com.weberbox.pifire.settings.presentation.model.SettingsData.Server
import com.weberbox.pifire.setup.presentation.model.VersionsData

interface SetupRepo {

    suspend fun getVersions(
        url: String,
        headerMap: Map<String, String>
    ): Result<VersionsData, DataError>

    suspend fun getSettingsData(
        server: Server,
    ): Result<Server, DataError>
}