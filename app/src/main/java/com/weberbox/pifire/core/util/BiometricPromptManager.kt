package com.weberbox.pifire.core.util

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.showAlerter

@Composable
fun rememberBiometricPromptManager(): BiometricPromptManager? {
    val activity = LocalActivity.current as? AppCompatActivity
    var biometricManager: BiometricPromptManager? = null

    val launchEnroll = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { biometricManager?.retryLastAuth() }
    )

    return remember(activity) {
        activity?.let {
            BiometricPromptManager(
                activity = it,
                launchEnroll = { intent -> launchEnroll.launch(intent) }
            ).also { manager ->
                biometricManager = manager
            }
        }
    }
}

class BiometricPromptManager(
    private val activity: AppCompatActivity,
    private val launchEnroll: (Intent) -> Unit
) {
    private val manager = BiometricManager.from(activity)
    private val authenticators = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
    } else {
        BIOMETRIC_STRONG
    }
    private var lastAuthRequest: (() -> Unit)? = null

    fun authenticate(
        title: String = activity.getString(R.string.dialog_biometric_title),
        description: String = activity.getString(R.string.dialog_biometric_description),
        onAuthenticationSuccess: () -> Unit
    ) {

        lastAuthRequest = {
            authenticate(title, description, onAuthenticationSuccess)
        }

        val promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
            .setConfirmationRequired(false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            promptInfo.setNegativeButtonText(activity.getString(R.string.cancel))
        }

        when (manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                activity.showAlerter(
                    message = UiText(R.string.alerter_biometric_not_available),
                    isError = true
                )
                return
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                activity.showAlerter(
                    message = UiText(R.string.alerter_biometric_not_supported),
                    isError = true
                )
                return
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                        putExtra(
                            Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
                        )
                    }
                    launchEnroll.invoke(enrollIntent)
                } else {
                    activity.showAlerter(
                        message = UiText(R.string.alerter_biometric_not_enrolled),
                        isError = true
                    )
                }
                return
            }

            else -> Unit
        }

        val executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    if (errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                        activity.showAlerter(
                            message = UiText(errString.toString()),
                            isError = true
                        )
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    onAuthenticationSuccess()
                }
            }
        )
        prompt.authenticate(promptInfo.build())
    }

    fun retryLastAuth() {
        if (manager.canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS) {
            lastAuthRequest?.invoke()
        } else {
            activity.showAlerter(
                message = UiText(R.string.alerter_biometric_still_not_enrolled),
                isError = true
            )
        }
    }
}