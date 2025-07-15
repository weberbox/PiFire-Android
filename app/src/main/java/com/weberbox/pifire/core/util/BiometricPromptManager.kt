package com.weberbox.pifire.core.util

import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.PromptInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.weberbox.pifire.R
import com.weberbox.pifire.common.presentation.util.UiText
import com.weberbox.pifire.common.presentation.util.showAlerter

@Composable
fun rememberBiometricPromptManager(): BiometricPromptManager? {
    val context = LocalContext.current
    val activity = context as? AppCompatActivity ?: return null

    val managerRef = remember { mutableStateOf<BiometricPromptManager?>(null) }

    val launchEnroll = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = {
            managerRef.value?.retryLastAuth()
        }
    )

    val biometricManager = remember(activity) {
        BiometricPromptManager(
            context = activity,
            launchEnroll = { intent -> launchEnroll.launch(intent) }
        )
    }

    DisposableEffect(activity) {
        managerRef.value = biometricManager
        onDispose {
            managerRef.value = null
        }
    }

    return biometricManager
}

class BiometricPromptManager(
    private val context: Context,
    private val launchEnroll: (Intent) -> Unit
) {
    private val manager = BiometricManager.from(context)
    private val authenticators = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        BIOMETRIC_STRONG or DEVICE_CREDENTIAL
    } else {
        BIOMETRIC_STRONG
    }
    private var lastAuthRequest: (() -> Unit)? = null

    private fun getActivity(): AppCompatActivity? {
        return context as? AppCompatActivity
    }

    fun authenticate(
        title: String = context.getString(R.string.dialog_biometric_title),
        description: String = context.getString(R.string.dialog_biometric_description),
        onAuthenticationSuccess: () -> Unit
    ) {
        val activity = getActivity() ?: return

        lastAuthRequest = {
            authenticate(title, description, onAuthenticationSuccess)
        }

        val promptInfo = PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
            .setConfirmationRequired(false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            promptInfo.setNegativeButtonText(context.getString(R.string.cancel))
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

        val executor = ContextCompat.getMainExecutor(context)
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
        val activity = getActivity() ?: return
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