package com.weberbox.pifire.core.notification

import com.google.firebase.messaging.FirebaseMessaging
import com.weberbox.pifire.R
import com.weberbox.pifire.core.common.model.DataError
import com.weberbox.pifire.core.common.model.Result
import com.weberbox.pifire.core.ui.util.EmptyUiArgs
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

/** This install's FCM token, from the app's own Firebase project. */
interface FcmTokenSource {
    suspend fun getToken(): Result<String, DataError>
}

class FirebaseFcmTokenSource @Inject constructor() : FcmTokenSource {

    override suspend fun getToken(): Result<String, DataError> = withTimeoutOrNull(TOKEN_TIMEOUT_MS.milliseconds) {
        suspendCancellableCoroutine { continuation ->
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                // task.result throws for a failed task, so check first.
                val token = if (task.isSuccessful) task.result else null
                if (token.isNullOrBlank()) {
                    Timber.w(task.exception, "FCM token unavailable")
                    continuation.resume(Result.Error(TOKEN_ERROR))
                } else {
                    continuation.resume(Result.Success(token))
                }
            }
        }
    } ?: Result.Error(TOKEN_ERROR)

    private companion object {
        const val TOKEN_TIMEOUT_MS = 15_000L
        val TOKEN_ERROR = DataError.UiText(resId = R.string.data_error_push_token, args = EmptyUiArgs)
    }
}
