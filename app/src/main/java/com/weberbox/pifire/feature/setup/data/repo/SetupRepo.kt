package com.weberbox.pifire.feature.setup.data.repo

import com.weberbox.pifire.core.annotations.Compatibility
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.model.settings.SettingsData.Server
import com.weberbox.pifire.feature.setup.data.model.VersionsData

interface SetupRepo {

    suspend fun getVersions(
        url: String,
        headerMap: Map<String, String>
    ): Result<VersionsData, DataError>

    @Compatibility(versionBelow = "1.10.0", build = "25")
    suspend fun getVersionsLegacy(
        url: String,
        headerMap: Map<String, String>
    ): Result<VersionsData, DataError>

    suspend fun getSettingsData(
        server: Server,
    ): Result<Server, DataError>
}