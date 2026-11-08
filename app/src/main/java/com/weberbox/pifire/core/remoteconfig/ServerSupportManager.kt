package com.weberbox.pifire.core.remoteconfig

import com.weberbox.pifire.core.model.SemanticVersion
import kotlinx.serialization.Serializable
import javax.inject.Inject

class ServerSupportManager @Inject constructor(
    private val remoteConfigRepo: RemoteConfigRepo,
) {

    suspend fun isServerVersionSupported(current: ServerInfo): ServerSupportResult {
        val config = remoteConfigRepo.fetchServerSupportRule() ?: return ServerSupportResult.Untested

        val min = config.minServerInfo
        val max = config.maxServerInfo

        val currentVersion = SemanticVersion.fromString(current.version)
        val minVersion = SemanticVersion.fromString(min.version)
        val maxVersion = if (max.version.isNotBlank()) {
            SemanticVersion.fromString(max.version)
        } else {
            null
        }

        val currentBuild = current.build.toIntOrNull() ?: 0
        val minBuild = min.build.toIntOrNull() ?: 0
        val maxBuild = max.build.toIntOrNull() ?: Int.MAX_VALUE

        // Version Checks
        if (currentVersion < minVersion) {
            return ServerSupportResult.UnsupportedMin(
                minVersion = min.version,
                minBuild = min.build,
                currentVersion = current.version,
                currentBuild = current.build,
                isMandatory = config.isMandatory,
            )
        }

        if (maxVersion != null && currentVersion > maxVersion) {
            return ServerSupportResult.UnsupportedMax(
                maxVersion = max.version,
                maxBuild = max.build,
                currentVersion = current.version,
                currentBuild = current.build,
                isMandatory = config.isMandatory,
            )
        }

        // Build Checks (only when versions match)
        if (currentVersion == minVersion && currentBuild < minBuild) {
            return ServerSupportResult.UnsupportedMin(
                minVersion = min.version,
                minBuild = min.build,
                currentVersion = current.version,
                currentBuild = current.build,
                isMandatory = config.isMandatory,
            )
        }

        if (maxVersion != null && currentVersion == maxVersion && currentBuild > maxBuild) {
            return ServerSupportResult.UnsupportedMax(
                maxVersion = max.version,
                maxBuild = max.build,
                currentVersion = current.version,
                currentBuild = current.build,
                isMandatory = config.isMandatory,
            )
        }

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
        val isMandatory: Boolean,
    ) : ServerSupportResult()

    data class UnsupportedMax(
        val maxVersion: String,
        val maxBuild: String,
        val currentVersion: String,
        val currentBuild: String,
        val isMandatory: Boolean,
    ) : ServerSupportResult()
}

@Serializable
data class ServerInfo(
    val version: String = "",
    val build: String = "",
)

/** Remote Config `server_support_config_v2`. */
@Serializable
data class ServerSupportConfigData(
    val rules: List<ServerSupportRule> = emptyList(),
)

/**
 * The PiFire range that app versions [minAppVersionCode] to [maxAppVersionCode] work with. A rule
 * stays open-ended until a PiFire release breaks it, so a new app release needs no new rule.
 */
@Serializable
data class ServerSupportRule(
    val minAppVersionCode: Int = 0,
    /** Null covers every later app version too. */
    val maxAppVersionCode: Int? = null,
    val isMandatory: Boolean = true,
    val minServerInfo: ServerInfo = ServerInfo(),
    /** A blank version means no upper limit. */
    val maxServerInfo: ServerInfo = ServerInfo(),
)

/** The rule covering [appVersionCode]; where ranges overlap, the one starting latest wins. */
fun ServerSupportConfigData.ruleFor(appVersionCode: Int): ServerSupportRule? = rules
    .filter { appVersionCode in it.minAppVersionCode..(it.maxAppVersionCode ?: Int.MAX_VALUE) }
    .maxByOrNull { it.minAppVersionCode }
