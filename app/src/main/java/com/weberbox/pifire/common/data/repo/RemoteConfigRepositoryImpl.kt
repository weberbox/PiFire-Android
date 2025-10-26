package com.weberbox.pifire.common.data.repo

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.common.data.util.readJsonFromAssets
import com.weberbox.pifire.common.presentation.util.DialogAction
import com.weberbox.pifire.common.presentation.util.DialogController
import com.weberbox.pifire.common.presentation.util.DialogEvent
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.util.AppUpdateConfigData
import com.weberbox.pifire.core.util.FeatureFlagsConfig
import com.weberbox.pifire.core.util.RemoteFeatureConfigData
import com.weberbox.pifire.core.util.ServerSupportConfigData
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.pow

class RemoteConfigRepositoryImpl @Inject constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val json: Json
) : RemoteConfigRepository {
    override suspend fun fetchServerSupportConfig(): ServerSupportConfigData? {
        val key = AppConfig.SERVER_SUPPORT_CONFIG
        val defaults = mapOf(
            key to readJsonFromAssets("config_server_support.json")
        )
        remoteConfig.setDefaultsAsync(defaults).await()

        return try {
            withRetryBackoff {
                remoteConfig.fetchAndActivate().await().also {
                    if (!it) Timber.d("Server Support Using Local Configs")
                }

                remoteConfig.fetchConfigJson<List<ServerSupportConfigData>>(key, json)
                    .find { it.appVersionCode == BuildConfig.VERSION_CODE }
            }
        } catch (e: Exception) {
            Timber.e(e, "Firebase Server Support fetch exception, fallback to defaults")
            remoteConfig.fetchConfigJson<List<ServerSupportConfigData>>(key, json)
                .find { it.appVersionCode == BuildConfig.VERSION_CODE }
        }
    }

    override suspend fun fetchAppUpdateConfig(): AppUpdateConfigData? {
        val key = AppConfig.APP_UPDATE_CONFIG
        val defaults = mapOf(
            key to readJsonFromAssets("config_app_update.json")
        )
        remoteConfig.setDefaultsAsync(defaults).await()

        return try {
            withRetryBackoff {
                remoteConfig.fetchAndActivate().await().also {
                    if (!it) Timber.d("App Update Using Local Configs")
                }

                remoteConfig.fetchConfigJson<List<AppUpdateConfigData>>(key, json)
                    .filter {
                        it.appVersionCode > BuildConfig.VERSION_CODE &&
                                (BuildConfig.ALPHA_BUILD || !it.isAlpha)
                    }
                    .maxByOrNull { it.appVersionCode }
            }
        } catch (e: Exception) {
            Timber.e(e, "Firebase App Update fetch exception, fallback to defaults")
            showFirebaseErrorDialog()
            remoteConfig.fetchConfigJson<List<AppUpdateConfigData>>(key, json)
                .filter {
                    it.appVersionCode > BuildConfig.VERSION_CODE &&
                            (BuildConfig.ALPHA_BUILD || !it.isAlpha)
                }
                .maxByOrNull { it.appVersionCode }
        }
    }

    override suspend fun fetchFeatureSupportConfig(): Map<String, RemoteFeatureConfigData> {
        val key = AppConfig.FEATURE_SUPPORT_CONFIG
        val defaults = mapOf(
            key to readJsonFromAssets("config_feature_support.json")
        )
        remoteConfig.setDefaultsAsync(defaults).await()

        return try {
            withRetryBackoff {
                remoteConfig.fetchAndActivate().await().also {
                    if (!it) Timber.d("App Update Using Local Configs")
                }

                remoteConfig.fetchConfigJson<List<FeatureFlagsConfig>>(key, json)
                    .firstOrNull() ?: emptyMap()
            }
        } catch (e: Exception) {
            Timber.e(e, "Firebase Feature Support fetch exception, fallback to defaults")
            remoteConfig.fetchConfigJson<List<FeatureFlagsConfig>>(key, json)
                .firstOrNull() ?: emptyMap()
        }
    }

    private suspend fun showFirebaseErrorDialog() {
        DialogController.sendEvent(
            event = DialogEvent(
                title = UiText(R.string.dialog_firebase_failed_title),
                message = UiText(R.string.dialog_firebase_failed_message),
                dismissible = false,
                positiveAction = DialogAction(
                    buttonText = UiText(R.string.close)
                )
            )
        )
    }

    private inline fun <reified T> FirebaseRemoteConfig.fetchConfigJson(
        key: String,
        json: Json
    ): T {
        val jsonString = getString(key)
        return json.decodeFromString(jsonString)
    }

    private suspend fun <T> withRetryBackoff(
        maxRetries: Int = 3,
        baseDelayMillis: Long = 1000,
        block: suspend () -> T
    ): T {
        repeat(maxRetries) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()

                if (attempt == maxRetries - 1) {
                    throw e // last attempt, rethrow
                }

                val backoff = baseDelayMillis * (2.0.pow(attempt.toDouble())).toLong()
                Timber.w(e, "Retry attempt ${attempt + 1} failed, retrying in $backoff ms")
                delay(backoff)
            }
        }
        error("Unreachable") // compiler safety
    }
}