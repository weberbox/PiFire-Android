package com.weberbox.pifire.core.util

import com.weberbox.pifire.common.data.model.SemanticVersion
import com.weberbox.pifire.common.data.repo.RemoteConfigRepository
import kotlinx.serialization.Serializable
import javax.inject.Inject

class ServerSupportManager @Inject constructor(
    private val remoteConfigRepository: RemoteConfigRepository
) {

    suspend fun isServerVersionSupported(current: ServerInfo): ServerSupportResult {
        val serverConfigData = remoteConfigRepository.fetchServerSupportConfig()
        val config = serverConfigData ?: return ServerSupportResult.Untested

        val min = config.minServerInfo
        val max = config.maxServerInfo

        val currentVersion = SemanticVersion.fromString(current.version)
        val minVersion = SemanticVersion.fromString(min.version)
        val maxVersion = if (max.version.isNotBlank())
            SemanticVersion.fromString(max.version) else null

        val currentBuild = current.build.toIntOrNull() ?: 0
        val minBuild = min.build.toIntOrNull() ?: 0
        val maxBuild = max.build.toIntOrNull() ?: Int.MAX_VALUE

        // Check version boundaries
        if (currentVersion < minVersion)
            return ServerSupportResult.UnsupportedMin(
                minVersion = min.version,
                minBuild = min.build,
                currentVersion = current.version,
                currentBuild = current.build,
                isMandatory = config.isMandatory
            )
        if (maxVersion != null && currentVersion > maxVersion)
            return ServerSupportResult.UnsupportedMax(
                maxVersion = max.version,
                maxBuild = max.build,
                currentVersion = current.version,
                currentBuild = current.build,
                isMandatory = config.isMandatory
            )

        // Check build boundaries
        if (currentBuild < minBuild)
            return ServerSupportResult.UnsupportedMin(
                minVersion = min.version,
                minBuild = min.build,
                currentVersion = current.version,
                currentBuild = current.build,
                isMandatory = config.isMandatory
            )
        if (currentBuild > maxBuild)
            return ServerSupportResult.UnsupportedMax(
                maxVersion = max.version,
                maxBuild = max.build,
                currentVersion = current.version,
                currentBuild = current.build,
                isMandatory = config.isMandatory
            )

        return ServerSupportResult.Supported
    }
}

sealed class ServerSupportResult {
    data object Supported : ServerSupportResult()
    data object Untested : ServerSupportResult()
    data class UnsupportedMin(
        val minVersion: String,
        val minBuild: String,
        val currentVersion: String,
        val currentBuild: String,
        val isMandatory: Boolean
    ) : ServerSupportResult()

    data class UnsupportedMax(
        val maxVersion: String,
        val maxBuild: String,
        val currentVersion: String,
        val currentBuild: String,
        val isMandatory: Boolean
    ) : ServerSupportResult()
}

@Serializable
data class ServerInfo(
    val version: String,
    val build: String
)

@Serializable
data class ServerSupportConfigData(
    val appVersionCode: Int,
    val isMandatory: Boolean,
    val minServerInfo: ServerInfo,
    val maxServerInfo: ServerInfo
)