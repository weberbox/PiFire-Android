package com.weberbox.pifire.core.remoteconfig

import android.content.Context
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.weberbox.pifire.BuildConfig
import com.weberbox.pifire.R
import com.weberbox.pifire.core.constants.AppConfig
import com.weberbox.pifire.core.data.util.readJsonFromAssets
import com.weberbox.pifire.core.ui.util.DialogAction
import com.weberbox.pifire.core.ui.util.DialogController
import com.weberbox.pifire.core.ui.util.DialogEvent
import com.weberbox.pifire.core.ui.util.UiText
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.tasks.await
import kotlinx.serialization.json.Json
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.pow
import kotlin.time.Duration.Companion.milliseconds

class RemoteConfigRepoImpl @Inject constructor(
    @param:ApplicationContext private val appContext: Context,
    private val remoteConfig: FirebaseRemoteConfig,
    private val json: Json,
) : RemoteConfigRepo {
    private val configSettings = FirebaseRemoteConfigSettings.Builder()
        .setFetchTimeoutInSeconds(15)
        .setMinimumFetchIntervalInSeconds(if (AppConfig.DEBUG) 0 else 3600)
        .build()

    private var fetchErrorShown = false

    override suspend fun fetchServerSupportRule(): ServerSupportRule? {
        val key = AppConfig.SERVER_SUPPORT_CONFIG
        val bundled = readJsonFromAssets("config_server_support_v2.json", context = appContext)
        fetchAndActivate(key = key, bundled = bundled)

        return decodeConfig<ServerSupportConfigData>(key = key, bundled = bundled)
            ?.ruleFor(appVersionCode = BuildConfig.VERSION_CODE)
    }

    override suspend fun fetchAppUpdateConfig(): AppUpdateConfigData? {
        val key = AppConfig.APP_UPDATE_CONFIG
        val bundled = readJsonFromAssets("config_app_update_v2.json", context = appContext)
        // Updates are checked again on every return to the app; tell the user only once.
        if (!fetchAndActivate(key = key, bundled = bundled) && !fetchErrorShown) {
            fetchErrorShown = true
            showFirebaseErrorDialog()
        }

        return decodeConfig<AppUpdateConfigData>(key = key, bundled = bundled)
    }

    override suspend fun fetchFeatureSupportConfig(): Map<String, RemoteFeatureConfigData> {
        val key = AppConfig.FEATURE_SUPPORT_CONFIG
        val bundled = readJsonFromAssets("config_feature_support.json", context = appContext)
        fetchAndActivate(key = key, bundled = bundled)

        return decodeConfig<List<FeatureFlagsConfig>>(key = key, bundled = bundled)
            ?.firstOrNull() ?: emptyMap()
    }

    // Returns false when the fetch failed; the last activated or bundled values stay in use.
    private suspend fun fetchAndActivate(key: String, bundled: String): Boolean = try {
        remoteConfig.setConfigSettingsAsync(configSettings).await()
        remoteConfig.setDefaultsAsync(mapOf(key to bundled)).await()
        withRetryBackoff {
            remoteConfig.fetchAndActivate().await().also { activated ->
                if (!activated) Timber.d("No new Remote Config values for $key")
            }
        }
        true
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Timber.e(e, "Firebase fetch exception for $key, using local values")
        false
    }

    // A malformed remote value falls back to the bundled asset instead of crashing the caller.
    private inline fun <reified T> decodeConfig(key: String, bundled: String): T? = try {
        json.decodeFromString<T>(remoteConfig.getString(key))
    } catch (e: IllegalArgumentException) {
        Timber.e(e, "Remote Config decode failed for $key, using bundled defaults")
        try {
            json.decodeFromString<T>(bundled)
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Bundled config decode failed for $key")
            null
        }
    }

    private suspend fun showFirebaseErrorDialog() {
        DialogController.sendEvent(
            event = DialogEvent(
                title = UiText(R.string.dialog_firebase_failed_title),
                message = UiText(R.string.dialog_firebase_failed_message),
                dismissible = false,
                positiveAction = DialogAction(
                    buttonText = UiText(R.string.close),
                ),
            ),
        )
    }

    private suspend fun <T> withRetryBackoff(
        maxRetries: Int = 3,
        baseDelayMillis: Long = 1000,
        block: suspend () -> T,
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
                delay(backoff.milliseconds)
            }
        }
        error("Unreachable") // compiler safety
    }
}
